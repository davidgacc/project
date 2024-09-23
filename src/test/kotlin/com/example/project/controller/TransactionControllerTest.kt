package com.example.project.controller

import com.example.project.model.Transaction
import com.example.project.service.TransactionService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals

class TransactionControllerTest {
    private val transactionService: TransactionService = mock(TransactionService::class.java)
    private val transactionController = TransactionController(transactionService)

    @Test
    fun `should authorize transaction and return response`() {
        // Given
        val transaction = Transaction(1, 1, 5.0, "PADARIA DO ZE SAO PAULO BR", "5811")
        val expectedResponse = "{ \"code\": \"00\", \"message\": \"Transaction authorized\" }"

        // When
        `when`(transactionService.authorizeTransaction(transaction)).thenReturn(expectedResponse)

        // Then
        val response: ResponseEntity<String> = transactionController.authorize(transaction)
        assertEquals(200, response.statusCode.value())
        assertEquals(expectedResponse, response.body)

        verify(transactionService).authorizeTransaction(transaction)
    }
}