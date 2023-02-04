package com.aldikitta.routes

import com.aldikitta.data.models.Activity
import com.aldikitta.data.requests.FollowUpdateRequest
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.data.util.ActivityType
import com.aldikitta.service.ActivityService
import com.aldikitta.service.FollowService
import com.aldikitta.util.ApiResponseMessages.USER_NOT_FOUND
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.followUser(
    followService: FollowService,
    activityService: ActivityService
) {
    authenticate {
        route("/api/following/follow") {
            post {
                val request = call.receiveNullable<FollowUpdateRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val userId = call.userId
                val didUserExist = followService.followUserIfExists(request, userId)
                if (didUserExist) {
                    activityService.createActivity(
                        Activity(
                            timestamp = System.currentTimeMillis(),
                            byUserId = userId,
                            toUserId = request.followedUserId,
                            type = ActivityType.FollowedUser.type,
                            parentId = ""
                        )
                    )
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(
                            successful = true
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

fun Route.unfollowUser(followService: FollowService) {
    authenticate {
        route("/api/following/unfollow") {
            delete {
                val request = call.receiveNullable<FollowUpdateRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                val didUserExist = followService.unfollowUserIfExists(request, call.userId)
                if (didUserExist) {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(
                            successful = true
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