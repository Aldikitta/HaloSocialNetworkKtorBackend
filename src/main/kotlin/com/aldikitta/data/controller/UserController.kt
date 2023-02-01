package com.aldikitta.data.controller

import com.aldikitta.data.models.User

interface UserController {
    suspend fun createUser(user: User)
    suspend fun getUserById(user: User): User

}