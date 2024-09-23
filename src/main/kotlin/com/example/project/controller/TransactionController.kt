
package com.example.project.controller

import com.example.project.model.Transaction
import com.example.project.service.TransactionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class TransactionController (private val transactionService: TransactionService) {

    @PostMapping("/authorize")
    fun authorize(@RequestBody transaction: Transaction): ResponseEntity<String> {
        val response = transactionService.authorizeTransaction(transaction)
        return ResponseEntity.ok(response)
    }
}