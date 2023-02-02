package com.aldikitta.plugins

import com.aldikitta.repository.user.UserRepository
import com.aldikitta.routes.createUserRoute
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()

    routing {
        createUserRoute(userRepository = userRepository)
    }
}
