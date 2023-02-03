package com.aldikitta.routes

import com.aldikitta.data.requests.LikeUpdateRequest
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.service.LikeService
import com.aldikitta.service.UserService
import com.aldikitta.util.ApiResponseMessages.USER_NOT_FOUND
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.likeParent(
    likeService: LikeService,
    userService: UserService
) {
    authenticate {
        route("/api/like") {
            post {
                val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                ifEmailBelongsToUser(
                    userId = request.userId,
                    validateEmail = userService::doesEmailBelongToUserId
                ) {
                    val likeSuccessful = likeService.likeParent(
                        userId = request.userId,
                        parentId = request.parentId
                    )
                    if (likeSuccessful) {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = true,
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = false,
                                message = USER_NOT_FOUND
                            )
                        )
                    }
                }
            }
        }
    }
}


fun Route.unLikeParent(
    likeService: LikeService,
    userService: UserService
) {
    authenticate {
        route("/api/unlike") {
            delete {
                val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                ifEmailBelongsToUser(
                    userId = request.userId,
                    validateEmail = userService::doesEmailBelongToUserId
                ) {
                    val unlikeSuccessful = likeService.unLikeParent(
                        userId = request.userId,
                        parentId = request.parentId
                    )
                    if (unlikeSuccessful) {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = true,
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = false,
                                message = USER_NOT_FOUND
                            )
                        )
                    }
                }
            }
        }
    }
}