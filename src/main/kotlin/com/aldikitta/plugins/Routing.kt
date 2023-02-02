package com.aldikitta.plugins

import com.aldikitta.data.repository.follow.FollowRepository
import com.aldikitta.data.repository.user.UserRepository
import com.aldikitta.routes.createUserRoute
import com.aldikitta.routes.followUser
import com.aldikitta.routes.loginUser
import com.aldikitta.routes.unfollowUser
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository: FollowRepository by inject()

    routing {
        // User routes
        createUserRoute(userRepository = userRepository)
        loginUser(userRepository = userRepository)

        // Following routes
        followUser(followRepository = followRepository)
        unfollowUser(followRepository = followRepository)
    }
}
