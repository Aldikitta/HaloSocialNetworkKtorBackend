package com.aldikitta.service

import com.aldikitta.data.repository.likes.LikeRepository
import com.aldikitta.data.util.ParentType

class LikeService(
    private val likeRepository: LikeRepository
) {
    suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean {
        return likeRepository.likeParent(userId = userId, parentId = parentId, parentType = parentType)
    }

    suspend fun unLikeParent(userId: String, parentId: String): Boolean {
        return likeRepository.unLikeParent(userId = userId, parentId = parentId)
    }

    suspend fun deleteLikesForParent(parentId: String){
        likeRepository.deleteLikesForParent(parentId = parentId)
    }
}