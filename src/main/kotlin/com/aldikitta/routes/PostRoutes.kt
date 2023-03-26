package com.aldikitta.routes

import com.aldikitta.data.requests.CreatePostRequest
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.service.CommentService
import com.aldikitta.service.LikeService
import com.aldikitta.service.PostService
import com.aldikitta.service.UserService
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
                            HttpStatusCode.OK, BasicApiResponse<Unit>(
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
                    call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                val posts = postService.getPostsForFollows(ownUserId = call.userId, page = page, pageSize = pageSize)
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
                val postId = call.parameters["postId"] ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                val post = postService.getPost(postId)
                if (post == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@delete
                }
                if (post.userId == call.userId) {
                    postService.deletePost(postId)
                    likeService.deleteLikesForParent(postId)
                    commentService.deleteCommentsForPost(postId)
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
    }
}

fun Route.getPostsForProfile(
    postService: PostService,
) {
    authenticate {
        route("/api/user/posts"){
            get {
                val userId = call.parameters[QueryParams.PARAM_USER_ID]
                val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
                val pageSize =
                    call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                val posts = postService.getPostsForProfile(
                    ownUserId = call.userId,
                    userId = userId ?: call.userId,
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

fun Route.getPostDetails(postService: PostService) {
    get("/api/post/details") {
        val postId = call.parameters["postId"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val post = postService.getPostDetails(call.userId, postId) ?: kotlin.run {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        call.respond(
            HttpStatusCode.OK,
            BasicApiResponse(
                successful = true,
                data = post
            )
        )
    }
}