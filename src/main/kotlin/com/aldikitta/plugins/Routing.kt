package com.aldikitta.plugins

import com.aldikitta.data.repository.user.UserRepository
import com.aldikitta.routes.createUserRoute
import com.aldikitta.routes.loginUser
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()

    routing {
        createUserRoute(userRepository = userRepository)
        loginUser(userRepository = userRepository)
    }
}
