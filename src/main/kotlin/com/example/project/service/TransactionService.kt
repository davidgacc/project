package com.example.project.service

import com.example.project.errors.ErrorDetails
import com.example.project.model.Account
import com.example.project.model.Balance
import com.example.project.model.Transaction
import com.example.project.model.TransactionResponse
import com.example.project.repository.AccountRepository
import com.example.project.repository.BalanceRepository
import com.example.project.repository.TransactionRepository
import com.example.project.util.MerchantMCCMapping
import org.springframework.stereotype.Service

@Service
class TransactionService (private val balanceRepository: BalanceRepository,
                          private val transactionRepository: TransactionRepository,
                          private val accountRepository: AccountRepository,
                          private val merchantMCCMapping: MerchantMCCMapping ) {

    fun authorizeTransaction(transaction: Transaction): String {
        val account = getAccount(transaction.account) ?: return toJson(
            TransactionResponse.Failure(ErrorDetails("07", "ACCOUNT_NOT_FOUND", "The specified account does not exist."))
        )

        val mcc = merchantMCCMapping.getMCC(transaction.merchant, transaction.mcc)
        val category = mapMCCToCategory(mcc)

        return processTransaction(transaction, account, category)
    }

    private fun getAccount(accountId: Long): Account? {
        return accountRepository.findById(accountId).orElse(null)
    }

    private fun processTransaction(transaction: Transaction, account: Account, category: String): String {
        val balance = getBalanceForCategory(account, category)

        // First check if the balance for the mapped category is sufficient
        if (balance != null && canDeduct(balance, transaction.totalAmount)) {
            deductAmount(balance, transaction.totalAmount)
            saveTransaction(transaction)
            return toJson(TransactionResponse.Success())
        }

        // If the mapped category is not sufficient or is not available, check for the CASH category
        return handleCashFallback(transaction, account, category)
    }

    private fun handleCashFallback(transaction: Transaction, account: Account, category: String): String {
        val cashBalance = getBalanceForCategory(account, category)
            ?: return toJson(TransactionResponse.Failure(ErrorDetails("51", "CASH_BALANCE_NOT_FOUND", "No cash balance available.")))

        return if (canDeduct(cashBalance, transaction.totalAmount)) {
            deductAmount(cashBalance, transaction.totalAmount)
            saveTransaction(transaction)
            toJson(TransactionResponse.Success())
        } else {
            saveTransaction(transaction)
            toJson(TransactionResponse.Failure(ErrorDetails("51", "INSUFFICIENT_FUNDS", "Not enough cash balance available.")))
        }
    }

    private fun getBalanceForCategory(account: Account, category: String): Balance? {
        return balanceRepository.findByCategoryAndAccount(category, account)
    }

    private fun canDeduct(balance: Balance, amount: Double): Boolean {
        return balance.amount >= amount
    }

    private fun deductAmount(balance: Balance, amount: Double) {
        balance.amount -= amount
        balanceRepository.save(balance)
    }

    private fun saveTransaction(transaction: Transaction) {
        transactionRepository.save(transaction)
    }


    private fun toJson(response: TransactionResponse): String {
        return when (response) {
            is TransactionResponse.Success -> "{ \"code\": \"${response.code}\", \"message\": \"${response.message}\" }"
            is TransactionResponse.Failure -> "{ \"code\": \"${response.error.code}\", \"message\": \"${response.error.message}\", \"description\": \"${response.error.description}\" }"
            else -> "{ \"code\": \"99\", \"message\": \"UNKNOWN_ERROR\" }" // Fallback for unexpected cases
        }
    }

    private fun mapMCCToCategory(mcc: String): String {
        return when (mcc) {
            "5411", "5412" -> TransactionCategory.FOOD.name
            "5811", "5812" -> TransactionCategory.MEAL.name
            else -> TransactionCategory.CASH.name
        }
    }

    enum class TransactionCategory {
        FOOD,
        MEAL,
        CASH
    }
}