package com.aldikitta.plugins

import com.aldikitta.routes.*
import com.aldikitta.service.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import org.koin.ktor.ext.inject
import java.io.File

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()
    val commentService: CommentService by inject()
    val activityService: ActivityService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    routing {
        static {
            resources("static")
        }
        // User routes
        createUser(
            userService = userService
        )
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )
        searchUser(
            userService = userService
        )
        getUserProfile(
            userService = userService
        )
        getPostsForProfile(
            postService = postService
        )
        updateUserProfile(
            userService = userService
        )

        // Following routes
        followUser(
            followService = followService,
            activityService = activityService
        )
        unfollowUser(
            followService = followService
        )

        // Post routes
        createPost(
            postService = postService,
            userService = userService
        )
        getPostsForFollows(
            postService = postService
        )
        deletePost(
            postService = postService,
            likeService = likeService,
            commentService = commentService
        )

        // Like routes
        likeParent(
            likeService = likeService,
            activityService = activityService
        )
        unLikeParent(
            likeService = likeService
        )
        getLikeForParent(
            likeService = likeService
        )

        // Comment routes
        createComment(
            commentService = commentService,
            activityService = activityService
        )
        deleteComment(
            commentService = commentService,
            likeService = likeService
        )
        getCommentsForPost(
            commentService = commentService
        )

        // Activity routes
        getActivities(
            activityService = activityService
        )
    }
}
