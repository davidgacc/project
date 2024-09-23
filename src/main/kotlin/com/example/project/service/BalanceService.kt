package com.example.project.service

import com.example.project.model.Balance
import com.example.project.repository.BalanceRepository

class BalanceService (private val balanceRepository: BalanceRepository) {
    fun createBalance(balance: Balance): Balance {
        return balanceRepository.save(balance)
    }

    fun getBalanceById(id: Long): Balance? {
        return balanceRepository.findById(id).orElse(null)
    }
}