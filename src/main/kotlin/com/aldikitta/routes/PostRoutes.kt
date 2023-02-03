package com.aldikitta.routes

import com.aldikitta.data.requests.CreatePostRequest
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.service.PostService
import com.aldikitta.service.UserService
import com.aldikitta.util.ApiResponseMessages.USER_NOT_FOUND
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

                val email = call.principal<JWTPrincipal>()?.getClaim("email", String::class)
                val isEmailByUser = userService.doesEmailBelongToUserId(
                    email = email ?: "",
                    userId = request.userId
                )

                if (!isEmailByUser){
                    call.respond(HttpStatusCode.Unauthorized, "You are not who you say you are,")
                    return@post
                }

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