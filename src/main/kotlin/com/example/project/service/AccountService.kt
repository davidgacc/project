package com.example.project.service

import com.example.project.model.Account
import com.example.project.repository.AccountRepository
import org.springframework.stereotype.Service

@Service
class AccountService(private val accountRepository: AccountRepository) {
    fun createAccount(account: Account): Account {
        return accountRepository.save(account)
    }

    fun getAccountById(id: Long): Account? {
        return accountRepository.findById(id).orElse(null)
    }
}