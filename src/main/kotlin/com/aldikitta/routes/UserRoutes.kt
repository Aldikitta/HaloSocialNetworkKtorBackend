package com.aldikitta.routes

import com.aldikitta.data.requests.CreateAccountRequest
import com.aldikitta.data.requests.LoginRequest
import com.aldikitta.data.responses.AuthResponse
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.service.UserService
import com.aldikitta.util.ApiResponseMessages.FIELD_BLANK
import com.aldikitta.util.ApiResponseMessages.INVALID_CREDENTIALS
import com.aldikitta.util.ApiResponseMessages.USER_ALREADY_EXIST
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.createUser(
    userService: UserService
) {
    route("/api/user/create") {
        post {
            val request = call.receiveNullable<CreateAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if (userService.doesUserWithEmailExist(request.email)) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = USER_ALREADY_EXIST
                    )
                )
                return@post
            }
            when (userService.validateCreateAccountRequest(request)) {
                is UserService.ValidationEvent.ErrorFieldEmpty -> {
                    call.respond(
                        BasicApiResponse(
                            successful = false,
                            message = FIELD_BLANK
                        )
                    )
                }

                is UserService.ValidationEvent.Success -> {
                    userService.createUser(request)
                    call.respond(
                        BasicApiResponse(
                            successful = true
                        )
                    )
                }
            }
        }
    }
}

fun Route.loginUser(
    userService: UserService,
    jwtIssuer: String,
    jwtAudience: String,
    jwtSecret: String
) {
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

            val user = userService.getUserByEmail(request.email) ?: kotlin.run {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = false,
                        message = INVALID_CREDENTIALS
                    )
                )
                return@post
            }
            val isCorrectPassword = userService.isValidPassword(
                enteredPassword = request.password,
                actualPassword = user.password
            )
            if (isCorrectPassword) {
                val expiresIn = 1000L * 60L * 60L * 24L * 365L
                val token = JWT.create()
                    .withClaim("userId", user.id)
                    .withIssuer(jwtIssuer)
                    .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                    .withAudience(jwtAudience)
                    .sign(Algorithm.HMAC256(jwtSecret))
                call.respond(
                    HttpStatusCode.OK,
                    AuthResponse(
                        token = token
                    )
                )
            } else {
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