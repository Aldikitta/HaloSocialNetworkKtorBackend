package com.aldikitta.routes

import com.aldikitta.data.requests.LikeUpdateRequest
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.data.util.ParentType
import com.aldikitta.service.ActivityService
import com.aldikitta.service.LikeService
import com.aldikitta.util.ApiResponseMessages
import com.aldikitta.util.ApiResponseMessages.USER_NOT_FOUND
import com.aldikitta.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.likeParent(
    likeService: LikeService,
    activityService: ActivityService
) {
    authenticate {
        route("/api/like") {
            post {
                val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val userId = call.userId
                val likeSuccessful = likeService.likeParent(
                    userId = userId,
                    parentId = request.parentId,
                    parentType = request.parentType
                )
                if (likeSuccessful) {
                    activityService.addLikeActivity(
                        byUserId = userId,
                        parentType = ParentType.fromType(request.parentType),
                        parentId = request.parentId
                    )
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = true,
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = false,
                            message = USER_NOT_FOUND
                        )
                    )
                }
            }
        }
    }
}


fun Route.unLikeParent(
    likeService: LikeService,
) {
    authenticate {
        route("/api/unlike") {
            delete {
                val parentId = call.parameters[QueryParams.PARAM_PARENT_ID] ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                val parentType = call.parameters[QueryParams.PARAM_PARENT_TYPE]?.toIntOrNull() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                val unlikeSuccessful = likeService.unLikeParent(call.userId, parentId, parentType)
                if(unlikeSuccessful) {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessages.USER_NOT_FOUND
                        )
                    )
                }
            }
        }
    }
}

fun Route.getLikeForParent(
    likeService: LikeService
) {
    authenticate {
        get("/api/like/parent") {
            val parentId = call.parameters[QueryParams.PARAM_PARENT_ID] ?: kotlin.run {
                call.respond(
                    HttpStatusCode.BadRequest
                )
                return@get
            }
            val usersWhoLikedParent = likeService.getUsersWhoLikedParent(
                parentId = parentId,
                userId = call.userId
            )
            call.respond(
                HttpStatusCode.OK,
                usersWhoLikedParent
            )
        }
    }
}