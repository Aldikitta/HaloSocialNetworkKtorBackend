package com.aldikitta.data.requests

data class CreateAccountRequest(
    val email: String,
    val username: String,
    val password: String
)
