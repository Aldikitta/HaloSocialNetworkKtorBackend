package com.aldikitta.data.repository.follow

interface FollowRepository {
    suspend fun followUserIfExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun unfollowIfUserExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean
}