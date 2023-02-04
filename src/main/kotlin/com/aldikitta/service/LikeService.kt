package com.aldikitta.service

import com.aldikitta.data.repository.likes.LikeRepository

class LikeService(
    private val likeRepository: LikeRepository
) {
    suspend fun likeParent(userId: String, parentId: String): Boolean {
        return likeRepository.likeParent(userId = userId, parentId = parentId)
    }

    suspend fun unLikeParent(userId: String, parentId: String): Boolean {
        return likeRepository.unLikeParent(userId = userId, parentId = parentId)
    }

    suspend fun deleteLikesForParent(parentId: String){
        likeRepository.deleteLikesForParent(parentId = parentId)
    }
}