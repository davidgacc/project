package com.example.project.model

import jakarta.persistence.*

@Entity
@Table(name = "transactions")
data class Transaction (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long=1,

    @Column(name = "account_id", nullable = false) // Store account ID directly
    val account: Long, // Reference to Account ID

    val totalAmount: Double,
    val merchant: String,
    val mcc: String,

    @Column(name = "transaction_date")
    val transactionDate: java.sql.Timestamp = java.sql.Timestamp(System.currentTimeMillis())
)