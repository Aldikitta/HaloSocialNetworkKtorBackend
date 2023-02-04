package com.aldikitta.service

import com.aldikitta.data.models.User
import com.aldikitta.data.repository.user.UserRepository
import com.aldikitta.data.requests.CreateAccountRequest
import com.aldikitta.data.requests.LoginRequest

class UserService(
    private val userRepository: UserRepository
) {
    suspend fun doesUserWithEmailExist(email: String): Boolean {
        return userRepository.getUserByEmail(email = email) != null
    }

    suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean{
        return userRepository.doesEmailBelongToUserId(email = email, userId = userId)
    }

    suspend fun getUserByEmail(email: String): User?{
        return userRepository.getUserByEmail(email)
    }

    fun isValidPassword(enteredPassword: String, actualPassword: String): Boolean{
        return enteredPassword == actualPassword
    }

    suspend fun doesPasswordMatchForUser(request: LoginRequest): Boolean{
        return userRepository.doesPasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )
    }

    suspend fun createUser(request: CreateAccountRequest) {
        userRepository.createUser(
            user = User(
                email = request.email,
                username = request.username,
                password = request.password,
                profileImageUrl = "",
                bio = "",
                githubUrl = null,
                instagramUrl = null,
                linkedinUrl = null
            )
        )
    }

    fun validateCreateAccountRequest(request: CreateAccountRequest): ValidationEvent {
        if (request.email.isBlank() || request.username.isBlank() || request.password.isBlank()) {
            return ValidationEvent.ErrorFieldEmpty
        }
        return ValidationEvent.Success
    }

    sealed class ValidationEvent {
        object ErrorFieldEmpty : ValidationEvent()
        object Success : ValidationEvent()
    }
}