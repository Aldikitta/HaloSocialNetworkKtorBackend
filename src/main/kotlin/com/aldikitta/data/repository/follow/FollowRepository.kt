package com.aldikitta.data.repository.follow

import com.aldikitta.data.models.Following

interface FollowRepository {
    suspend fun followUserIfExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun unfollowIfUserExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun getFollowsByUser(userId: String): List<Following>
    suspend fun doesUserFollow(followingUserId: String, followedUserId: String): Boolean
}