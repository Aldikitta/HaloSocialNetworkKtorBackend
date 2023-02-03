package com.aldikitta.routes

import com.aldikitta.data.requests.CreatePostRequest
import com.aldikitta.data.requests.DeletePostRequest
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.plugins.email
import com.aldikitta.service.PostService
import com.aldikitta.service.UserService
import com.aldikitta.util.ApiResponseMessages.USER_NOT_FOUND
import com.aldikitta.util.Constants
import com.aldikitta.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createPostRoute(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        route("/api/post/create") {
            post {
                val request = call.receiveNullable<CreatePostRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                ifEmailBelongsToUser(
                    userId = request.userId,
                    validateEmail = userService::doesEmailBelongToUserId
                ) {
                    val didUserExist = postService.createPostIfUserExists(request)
                    if (!didUserExist) {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = false,
                                message = USER_NOT_FOUND
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = true,
                            )
                        )
                    }
                }
            }
        }
    }
}

fun Route.getPostsForFollows(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        get {
            val userId = call.parameters[QueryParams.PARAM_USER_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            ifEmailBelongsToUser(
                userId = userId,
                validateEmail = userService::doesEmailBelongToUserId
            ) {
                val posts = postService.getPostsForFollows(userId = userId, page = page, pageSize = pageSize)
                call.respond(
                    HttpStatusCode.OK,
                    posts
                )
            }
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    userService: UserService
) {
    route("/api/post/delete") {
        delete {
            val request = call.receiveNullable<DeletePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val post = postService.getPost(request.postId)
            if (post == null) {
                call.respond(
                    HttpStatusCode.NotFound
                )
                return@delete
            }
            ifEmailBelongsToUser(
                userId = post.userId,
                validateEmail = userService::doesEmailBelongToUserId
            ) {
                postService.deletePost(request.postId)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}