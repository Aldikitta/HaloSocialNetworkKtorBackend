package com.aldikitta.routes

import com.aldikitta.data.repository.user.UserRepository
import com.aldikitta.data.models.User
import com.aldikitta.data.requests.CreateAccountRequest
import com.aldikitta.data.requests.LoginRequest
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.util.ApiResponseMessages.FIELD_BLANK
import com.aldikitta.util.ApiResponseMessages.INVALID_CREDENTIALS
import com.aldikitta.util.ApiResponseMessages.USER_ALREADY_EXIST
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createUserRoute(
    userRepository: UserRepository
) {
    route("/api/user/create") {
        post {
            val request = call.receiveNullable<CreateAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userExists = userRepository.getUserByEmail(request.email) != null
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
            call.respond(
                BasicApiResponse(
                    successful = true
                )
            )
        }
    }
}

fun Route.loginUser(userRepository: UserRepository) {
    route("/api/user/login") {
        post {
            val request = call.receiveNullable<LoginRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if (request.email.isBlank() || request.password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val isCorrectPassword = userRepository.doesPasswordForUserMatch(
                email = request.email,
                enteredPassword = request.password
            )
            if (isCorrectPassword) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = true
                    )
                )
            }else{
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = false,
                        message = INVALID_CREDENTIALS
                    )
                )
            }
        }
    }
}