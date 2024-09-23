package com.example.project.model

import jakarta.persistence.*

@Entity
@Table(name = "balances")
data class Balance (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 1,
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false) // Foreign key to Account
    val account: Account, // Reference to Account entity
    val category: String, // e.g., "FOOD", "MEAL", "CASH"
    var amount: Double // Balance amount for the category
)
