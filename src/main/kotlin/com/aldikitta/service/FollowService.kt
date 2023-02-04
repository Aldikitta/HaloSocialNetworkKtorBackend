package com.aldikitta.service

import com.aldikitta.data.repository.follow.FollowRepository
import com.aldikitta.data.requests.FollowUpdateRequest

class FollowService(
    private val followRepository: FollowRepository
) {
    suspend fun followUserIfExists(request: FollowUpdateRequest, followingUserId: String): Boolean {
        return followRepository.followUserIfExists(
            followingUserId,
            request.followedUserId
        )
    }

    suspend fun unfollowUserIfExists(request: FollowUpdateRequest, followingUserId: String): Boolean {
        return followRepository.unfollowIfUserExists(
            followingUserId,
            request.followedUserId
        )
    }
}