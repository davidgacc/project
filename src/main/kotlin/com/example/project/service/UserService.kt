package com.example.project.service

import com.example.project.model.User
import com.example.project.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun createUser(user: User): User {
        return userRepository.save(user)
    }

    fun getUserById(id: Long): User? {
        return userRepository.findById(id).orElse(null)
    }
}