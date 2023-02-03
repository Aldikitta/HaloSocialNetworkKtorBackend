package com.aldikitta.routes

import com.aldikitta.data.requests.CreateCommentRequest
import com.aldikitta.data.requests.CreatePostRequest
import com.aldikitta.data.requests.DeleteCommentRequest
import com.aldikitta.data.requests.LikeUpdateRequest
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.service.CommentService
import com.aldikitta.service.LikeService
import com.aldikitta.service.UserService
import com.aldikitta.util.ApiResponseMessages
import com.aldikitta.util.ApiResponseMessages.USER_NOT_FOUND
import com.aldikitta.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createComment(
    userService: UserService,
    commentService: CommentService
) {
    authenticate {
        route("/api/comment/create") {
            post {
                val request = call.receiveNullable<CreateCommentRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                ifEmailBelongsToUser(
                    userId = request.userId,
                    validateEmail = userService::doesEmailBelongToUserId
                ) {
                    when (commentService.createComment(request)) {
                        is CommentService.ValidationEvents.ErrorCommentToLong -> {
                            call.respond(
                                HttpStatusCode.OK, BasicApiResponse(
                                    successful = false,
                                    message = ApiResponseMessages.COMMENT_TO_LONG
                                )
                            )
                        }

                        is CommentService.ValidationEvents.ErrorFieldEmpty -> {
                            call.respond(
                                HttpStatusCode.OK, BasicApiResponse(
                                    successful = false,
                                    message = ApiResponseMessages.FIELD_BLANK
                                )
                            )
                        }

                        is CommentService.ValidationEvents.Success -> {
                            call.respond(
                                HttpStatusCode.OK, BasicApiResponse(
                                    successful = true,
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

fun Route.getCommentsForPost(
    commentService: CommentService,
) {
    authenticate {
        route("/api/comment/get") {
            get {
                val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
                    call.respond(
                        HttpStatusCode.BadRequest
                    )
                    return@get
                }
                val comments = commentService.getCommentsForPost(postId)
                call.respond(
                    HttpStatusCode.OK,
                    comments
                )
            }
        }
    }
}

fun Route.deleteComment(
    commentService: CommentService,
    userService: UserService,
    likeService: LikeService
) {
    authenticate {
        route("/api/comment/delete") {
            delete {
                val request = call.receiveNullable<DeleteCommentRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                ifEmailBelongsToUser(
                    userId = request.userId,
                    validateEmail = userService::doesEmailBelongToUserId
                ){
                    val deleted = commentService.deleteComment(request.commentId)
                    if (deleted){
                        likeService.deleteLikesForParent(request.commentId)
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = true
                            )
                        )
                    }else{
                        call.respond(
                            HttpStatusCode.NotFound,
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