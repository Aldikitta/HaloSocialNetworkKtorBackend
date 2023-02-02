package com.aldikitta.routes

import com.aldikitta.controller.user.UserController
import com.aldikitta.data.models.User
import com.aldikitta.data.requests.CreateAccountRequest
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.util.ApiResponseMessages.FIELD_BLANK
import com.aldikitta.util.ApiResponseMessages.USER_ALREADY_EXIST
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val userController: UserController by inject()
    route("/api/user/create") {
        post {
            val request = call.receiveNullable<CreateAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userExists = userController.getUserByEmail(request.email) != null
            if (userExists) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = USER_ALREADY_EXIST
                    )
                )
                return@post
            }
            if (request.email.isBlank() || request.username.isBlank() || request.password.isBlank()) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = FIELD_BLANK
                    )
                )
                return@post
            }
            userController.createUser(
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
            call.respond(
                BasicApiResponse(
                    successful = true
                )
            )
        }
    }
}