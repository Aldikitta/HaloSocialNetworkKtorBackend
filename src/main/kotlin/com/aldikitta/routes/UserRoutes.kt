package com.aldikitta.routes

import com.aldikitta.data.models.User
import com.aldikitta.data.requests.CreateAccountRequest
import com.aldikitta.data.requests.LoginRequest
import com.aldikitta.data.requests.UpdateProfileRequest
import com.aldikitta.data.responses.AuthResponse
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.service.PostService
import com.aldikitta.service.UserService
import com.aldikitta.util.ApiResponseMessages.FIELD_BLANK
import com.aldikitta.util.ApiResponseMessages.INVALID_CREDENTIALS
import com.aldikitta.util.ApiResponseMessages.USER_ALREADY_EXIST
import com.aldikitta.util.ApiResponseMessages.USER_NOT_FOUND
import com.aldikitta.util.Constants
import com.aldikitta.util.Constants.BASE_URL
import com.aldikitta.util.Constants.PROFILE_PICTURE_PATH
import com.aldikitta.util.QueryParams
import com.aldikitta.util.save
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.nio.file.Paths
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

fun Route.searchUser(
    userService: UserService
) {
    authenticate {
        route("/api/user/search") {
            get {
                val query = call.parameters[QueryParams.PARAM_QUERY]
                if (query.isNullOrBlank()) {
                    call.respond(
                        HttpStatusCode.OK,
                        listOf<User>()
                    )
                    return@get
                }
                val searchResults = userService.searchForUsers(query, call.userId)
                call.respond(
                    HttpStatusCode.OK,
                    searchResults
                )
            }
        }
    }
}

fun Route.getPostsForProfile(
    postService: PostService
) {
    authenticate {
        route("/api/user/posts") {
            get {
                val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
                val pageSize =
                    call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

                val posts = postService.getPostsForProfile(
                    userId = call.userId,
                    page = page,
                    pageSize = pageSize
                )
                call.respond(
                    HttpStatusCode.OK,
                    posts
                )
            }
        }
    }
}

fun Route.updateUserProfile(
    userService: UserService
) {
    val gson: Gson by inject()
    authenticate {
        route("/api/user/update") {
            put {
                val multipart = call.receiveMultipart()
                var updateProfileRequest: UpdateProfileRequest? = null
                var fileName: String? = null
                multipart.forEachPart { partData ->
                    when (partData) {
                        is PartData.FormItem -> {
                            if (partData.name == "update_profile_data") {
                                updateProfileRequest =
                                    gson.fromJson(partData.value, UpdateProfileRequest::class.java)

                            }
                        }

                        is PartData.FileItem -> {
                            fileName = partData.save(PROFILE_PICTURE_PATH)
                        }

                        is PartData.BinaryItem -> Unit
                        else -> {}
                    }
                }
                val profilePictureUrl = "${BASE_URL}profile_pictures/$fileName"

                updateProfileRequest?.let { request ->
                    val updateAcknowledged = userService.updateUser(
                        userId = call.userId,
                        profileImageUr = profilePictureUrl,
                        updateProfileRequest = request
                    )
                    if (updateAcknowledged) {
                        call.respond(
                            HttpStatusCode.OK, BasicApiResponse(
                                successful = true
                            )
                        )
                    } else {
                        File("${PROFILE_PICTURE_PATH}/$fileName").delete()
                        call.respond(
                            HttpStatusCode.InternalServerError
                        )
                    }
                } ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }
            }
        }
    }
}

fun Route.getUserProfile(
    userService: UserService
) {
    authenticate {
        route("/api/user/profile") {
            get {
                val userId = call.parameters[QueryParams.PARAM_USER_ID]
                if (userId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val profileResponse = userService.getUserProfile(userId, call.userId)
                if (profileResponse == null) {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(
                            successful = false,
                            message = USER_NOT_FOUND
                        )
                    )
                    return@get
                }
                call.respond(
                    HttpStatusCode.OK,
                    profileResponse
                )
            }
        }
    }
}