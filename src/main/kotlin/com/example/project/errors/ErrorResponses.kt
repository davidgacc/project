package com.example.project.errors

import com.example.project.model.TransactionResponse

object ErrorResponses {
    val CASH_BALANCE_NOT_FOUND = TransactionResponse.Failure(
        "51", "CASH_BALANCE_NOT_FOUND", "No cash balance available.")

    val INSUFFICIENT_FUNDS = TransactionResponse.Failure(
        "51", "INSUFFICIENT_FUNDS", "Not enough cash balance available.")

    val ACCOUNT_NOT_FOUND = TransactionResponse.Failure("07", "ACCOUNT_NOT_FOUND", "The specified account does not exist.")
}