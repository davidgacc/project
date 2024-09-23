package com.example.project.model

import jakarta.persistence.*

@Entity
@Table(name = "accounts")
data class Account (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long=1,
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable=false)
    val user: User,
)