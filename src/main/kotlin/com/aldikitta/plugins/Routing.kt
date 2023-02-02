package com.aldikitta.plugins

import com.aldikitta.routes.userRoutes
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        userRoutes()
    }
}
