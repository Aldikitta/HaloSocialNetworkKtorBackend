package com.aldikitta.plugins

import com.aldikitta.routes.*
import com.aldikitta.service.FollowService
import com.aldikitta.service.LikeService
import com.aldikitta.service.PostService
import com.aldikitta.service.UserService
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    routing {
        // User routes
        createUser(userService = userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )

        // Following routes
        followUser(followService = followService)
        unfollowUser(followService = followService)

        // Post routes
        createPost(postService = postService, userService = userService)
        getPostsForFollows(userService = userService, postService = postService)
        deletePost(postService = postService, userService = userService)]

        // Like routes
        likeParent(userService = userService, likeService = likeService)
        unLikeParent(userService = userService, likeService = likeService)
    }
}
