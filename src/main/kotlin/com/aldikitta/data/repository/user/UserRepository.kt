package com.aldikitta.data.repository.user

import com.aldikitta.data.models.User
import com.aldikitta.data.requests.UpdateProfileRequest

interface UserRepository {
    suspend fun createUser(user: User)
    suspend fun getUserById(id: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun doesPasswordForUserMatch(email: String, enteredPassword: String): Boolean
    suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean
    suspend fun searchForUsers(query: String): List<User>
    suspend fun updateUser(
        userId: String,
        profileImageUrl: String?,
        bannerUrl: String?,
        updateProfileRequest: UpdateProfileRequest
    ): Boolean

    suspend fun getUsers(userIds: List<String>): List<User>
}