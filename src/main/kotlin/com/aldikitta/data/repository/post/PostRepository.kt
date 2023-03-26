package com.aldikitta.data.repository.post

import com.aldikitta.data.models.Post
import com.aldikitta.data.responses.PostResponse
import com.aldikitta.util.Constants

interface PostRepository{
    suspend fun createPost(post: Post): Boolean
    suspend fun deletePost(postId: String)
    suspend fun getPostsByFollows(
        ownUserId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): List<PostResponse>

    suspend fun getPostsForProfile(
        ownUserId: String,
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): List<PostResponse>

    suspend fun getPost(postId: String): Post?

    suspend fun getPostDetails(userId: String, postId: String): PostResponse?

}
