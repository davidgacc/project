package com.example.project.errors

data class ErrorDetails(
    val code: String,
    val message: String,
    val description: String? = null
)