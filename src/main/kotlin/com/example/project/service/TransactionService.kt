package com.example.project.service

import com.example.project.errors.ErrorDetails
import com.example.project.errors.ErrorResponses
import com.example.project.model.*
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
        val account = getAccount(transaction.account) ?: return toJson(ErrorResponses.ACCOUNT_NOT_FOUND)

        // Get the category based on the merchant name
        val category = merchantMCCMapping.getCategoryByMerchant(transaction.merchant)

        // Get the MCC from the merchant name or default MCC if not found
        val mcc = merchantMCCMapping.getMCC(transaction.merchant, transaction.mcc)
        // Get the fallback category based on the MCC
        val categoryFallback = merchantMCCMapping.mapMCCToCategory(mcc)

        // Prioritize the category derived from the merchant name
        val finalCategory = if (category != TransactionCategory.CASH.name) {
            category
        } else {
            categoryFallback // Use the fallback category if the merchant category is CASH
        }

        return processTransaction(transaction, account, finalCategory)
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
            ?: return toJson(ErrorResponses.CASH_BALANCE_NOT_FOUND)

        return if (canDeduct(cashBalance, transaction.totalAmount)) {
            deductAmount(cashBalance, transaction.totalAmount)
            saveTransaction(transaction)
            toJson(TransactionResponse.Success())
        } else {
            saveTransaction(transaction)
            toJson(ErrorResponses.INSUFFICIENT_FUNDS)
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
            is TransactionResponse.Failure -> "{ \"code\": \"${response.code}\", \"message\": \"${response.message}\", \"description\": \"${response.description}\" }"
            else -> "{ \"code\": \"99\", \"message\": \"UNKNOWN_ERROR\" }" // Fallback for unexpected cases
        }
    }
}