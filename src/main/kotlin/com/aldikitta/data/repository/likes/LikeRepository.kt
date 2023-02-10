package com.aldikitta.data.repository.likes

import com.aldikitta.data.models.Like
import com.aldikitta.util.Constants

interface LikeRepository {
    suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean

    suspend fun unLikeParent(userId: String, parentId: String): Boolean

    suspend fun deleteLikesForParent(parentId: String)
    suspend fun getLikesForParent(
        parentId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Like>
}