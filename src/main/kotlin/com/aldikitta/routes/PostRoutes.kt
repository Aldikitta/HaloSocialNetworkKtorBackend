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
import com.aldikitta.util.Constants.POST_PICTURE_PATH
import com.aldikitta.util.QueryParams
import com.aldikitta.util.save
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

fun Route.createPost(
    postService: PostService,
    userService: UserService
) {
    val gson by inject<Gson>()
    authenticate {
        route("/api/post/create") {
            post {
                val multipart = call.receiveMultipart()
                var createPostRequest: CreatePostRequest? = null
                var fileName: String? = null
                multipart.forEachPart { partData ->
                    when (partData) {
                        is PartData.FormItem -> {
                            if (partData.name == "post_data") {
                                createPostRequest =
                                    gson.fromJson(partData.value, CreatePostRequest::class.java)
                            }
                        }

                        is PartData.FileItem -> {
                            fileName = partData.save(POST_PICTURE_PATH)
                        }

                        is PartData.BinaryItem -> Unit
                        else -> {}
                    }
                }
                val postPictureUrl = "${Constants.BASE_URL}post_pictures/$fileName"

                createPostRequest?.let { request ->
                    val createPostAcknowledged = postService.createPost(
                        request = request,
                        userId = call.userId,
                        imageUrl = postPictureUrl
                    )
                    if (createPostAcknowledged) {
                        call.respond(
                            HttpStatusCode.OK, BasicApiResponse(
                                successful = true
                            )
                        )
                    } else {
                        File("${Constants.POST_PICTURE_PATH}/$fileName").delete()
                        call.respond(
                            HttpStatusCode.InternalServerError
                        )
                    }
                } ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
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