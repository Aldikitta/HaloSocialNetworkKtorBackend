package com.aldikitta.service

import com.aldikitta.data.repository.follow.FollowRepository
import com.aldikitta.data.requests.FollowUpdateRequest

class FollowService(
    private val followRepository: FollowRepository
) {
    suspend fun followUserIfExists(request: FollowUpdateRequest): Boolean {
        return followRepository.followUserIfExists(
            request.followingUserId,
            request.followedUserId
        )
    }

    suspend fun unfollowUserIfExists(request: FollowUpdateRequest): Boolean {
        return followRepository.unfollowIfUserExists(
            request.followingUserId,
            request.followedUserId
        )
    }
}