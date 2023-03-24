package com.aldikitta.service

import com.aldikitta.data.repository.follow.FollowRepository
import com.aldikitta.data.repository.likes.LikeRepository
import com.aldikitta.data.repository.user.UserRepository
import com.aldikitta.data.responses.UserResponseItem

class LikeService(
    private val likeRepository: LikeRepository,
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {
    suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean {
        return likeRepository.likeParent(userId = userId, parentId = parentId, parentType = parentType)
    }

    suspend fun unLikeParent(userId: String, parentId: String): Boolean {
        return likeRepository.unlikeParent(userId = userId, parentId = parentId)
    }

    suspend fun deleteLikesForParent(parentId: String){
        likeRepository.deleteLikesForParent(parentId = parentId)
    }

    suspend fun getUsersWhoLikedParent(parentId: String, userId: String): List<UserResponseItem>{
        val userIds = likeRepository.getLikesForParent(parentId).map {
            it.userId
        }
        val users = userRepository.getUsers(userIds)
        val followsByUser = followRepository.getFollowsByUser(userId)
        return users.map { user ->
            val isFollowing = followsByUser.find { it.followedUserId == user.id } != null
            UserResponseItem(
                username = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = isFollowing
            )
        }
    }
}