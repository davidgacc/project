package com.example.project.service

import com.example.project.model.Account
import com.example.project.model.Balance
import com.example.project.model.Transaction
import com.example.project.model.User
import com.example.project.repository.AccountRepository
import com.example.project.repository.BalanceRepository
import com.example.project.repository.TransactionRepository
import com.example.project.util.MerchantMCCMapping
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.test.assertEquals

class TransactionServiceTest {

    private lateinit var balanceRepository: BalanceRepository
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var merchantMCCMapping: MerchantMCCMapping
    private lateinit var transactionService: TransactionService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        balanceRepository = mock(BalanceRepository::class.java)
        transactionRepository = mock(TransactionRepository::class.java)
        accountRepository = mock(AccountRepository::class.java)
        merchantMCCMapping = MerchantMCCMapping()
        transactionService = TransactionService(balanceRepository, transactionRepository, accountRepository, merchantMCCMapping)
    }

    @Test
    fun `should authorize transaction when balance is sufficient`() {
        val account = Account(1, User(1, "John Doe"))
        val balance = Balance(1, account, "MEAL", 10.0)
        val transaction = Transaction(1, account.id, 5.0, "PADARIA DO ZE SAO PAULO BR", "5811")

        `when`(accountRepository.findById(account.id)).thenReturn(Optional.of(account))
        `when`(balanceRepository.findByCategoryAndAccount("MEAL", account)).thenReturn(balance)

        val response = transactionService.authorizeTransaction(transaction)

        assertEquals("{ \"code\": \"00\", \"message\": \"TRANSACTION_APPROVED\" }", response)
        verify(balanceRepository).save(balance)
        verify(transactionRepository).save(transaction)
    }

    @Test
    fun `should return insufficient funds when balance is not sufficient`() {
        val account = Account(1, User(1, "John Doe"))
        val balance = Balance(1, account, "MEAL", 2.0)
        val transaction = Transaction(1, account.id, 5.0, "PADARIA DO ZE SAO PAULO BR", "5811")

        `when`(accountRepository.findById(account.id)).thenReturn(Optional.of(account))
        `when`(balanceRepository.findByCategoryAndAccount("MEAL", account)).thenReturn(balance)

        val response = transactionService.authorizeTransaction(transaction)

        assertEquals("{ \"code\": \"51\", \"message\": \"INSUFFICIENT_FUNDS\", \"description\": \"Not enough cash balance available.\" }", response)
        verify(balanceRepository, never()).save(balance)
        verify(transactionRepository, never()).save(transaction)
    }

    @Test
    fun `should return account not found error`() {
        val transaction = Transaction(1, 1, 5.0, "PADARIA DO ZE SAO PAULO BR", "5811")

        `when`(accountRepository.findById(transaction.account)).thenReturn(Optional.empty())

        val response = transactionService.authorizeTransaction(transaction)

        assertEquals("{ \"code\": \"07\", \"message\": \"ACCOUNT_NOT_FOUND\", \"description\": \"The specified account does not exist.\" }", response)
    }

    @Test
    fun `should fallback to CASH when mcc not mapping and balance is sufficient`() {
        val account = Account(1, User(1, "John Doe"))
        val cashBalance = Balance(2, account, "CASH", 10.0)
        val transaction = Transaction(1, account.id, 5.0, "PADARIA DO ZE SAO PAULO BR TEST", "5811")

        `when`(accountRepository.findById(account.id)).thenReturn(Optional.of(account))
        `when`(balanceRepository.findByCategoryAndAccount("CASH", account)).thenReturn(cashBalance)

        val response = transactionService.authorizeTransaction(transaction)

        assertEquals("{ \"code\": \"00\", \"message\": \"TRANSACTION_APPROVED\" }", response)
        verify(balanceRepository).save(cashBalance)
        verify(transactionRepository).save(transaction)
    }

    @Test
    fun `should fallback to CASH when mcc not mapping and balance is insufficient`() {
        val account = Account(1, User(1, "John Doe"))
        val cashBalance = Balance(2, account, "CASH", 10.0)
        val transaction = Transaction(1, account.id, 15.0, "PADARIA DO ZE SAO PAULO BR TEST", "5811")

        `when`(accountRepository.findById(account.id)).thenReturn(Optional.of(account))
        `when`(balanceRepository.findByCategoryAndAccount("CASH", account)).thenReturn(cashBalance)

        val response = transactionService.authorizeTransaction(transaction)

        assertEquals("{ \"code\": \"51\", \"message\": \"INSUFFICIENT_FUNDS\", \"description\": \"Not enough cash balance available.\" }", response)
        verify(transactionRepository).save(transaction)
    }

}