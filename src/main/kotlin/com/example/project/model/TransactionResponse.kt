package com.example.project.model

import com.example.project.errors.ErrorDetails

sealed class TransactionResponse {
    data class Success(val code: String = "00", val message: String = "TRANSACTION_APPROVED") : TransactionResponse()
    data class Failure(val error: ErrorDetails) : TransactionResponse()
}