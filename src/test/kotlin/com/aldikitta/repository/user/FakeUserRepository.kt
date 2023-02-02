package com.aldikitta.repository.user

import com.aldikitta.data.models.User
import com.aldikitta.data.repository.user.UserRepository

class FakeUserRepository: UserRepository {
    private val users = mutableListOf<User>()
    override suspend fun createUser(user: User) {
        users.add(user)
    }

    override suspend fun getUserById(id: String): User? {
        return users.find {
            it.id == id
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.find {
            it.email == email
        }
    }

    override suspend fun doesPasswordForUserMatch(email: String, enteredPassword: String): Boolean {
        val user = getUserByEmail(email)
        return user?.password == enteredPassword
    }
}