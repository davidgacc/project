package com.example.project.repository

import com.example.project.model.Account
import com.example.project.model.Balance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BalanceRepository : JpaRepository<Balance, Long> {
    fun findByCategoryAndAccount(category: String, account: Account): Balance?
}