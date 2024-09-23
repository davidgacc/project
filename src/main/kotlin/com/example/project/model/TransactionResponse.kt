package com.example.project.model

sealed class TransactionResponse {
    data class Success(val code: String = "00", val message: String = "TRANSACTION_APPROVED") : TransactionResponse()
    data class Failure(val code: String, val message: String, val description: String) : TransactionResponse()
}