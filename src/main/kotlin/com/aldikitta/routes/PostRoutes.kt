package com.aldikitta.routes

import com.aldikitta.data.requests.CreatePostRequest
import com.aldikitta.data.requests.DeletePostRequest
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.service.CommentService
import com.aldikitta.service.LikeService
import com.aldikitta.service.PostService
import com.aldikitta.service.UserService
import com.aldikitta.util.ApiResponseMessages.USER_NOT_FOUND
import com.aldikitta.util.Constants
import com.aldikitta.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createPost(
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
                val userId = call.userId
                val didUserExist = postService.createPostIfUserExists(request, userId)
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

fun Route.getPostsForFollows(
    postService: PostService,
) {
    authenticate {
        route("/api/post/get") {
            get {
                val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
                val pageSize =
                    call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

                val posts = postService.getPostsForFollows(userId = call.userId, page = page, pageSize = pageSize)
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
    likeService: LikeService,
    commentService: CommentService
) {
    authenticate {
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
                if (post.userId == call.userId) {
                    postService.deletePost(request.postId)
                    likeService.deleteLikesForParent(request.postId)
                    commentService.deleteCommentsForPost(request.postId)
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(
                        HttpStatusCode.Unauthorized
                    )
                }

            }
        }
    }
}