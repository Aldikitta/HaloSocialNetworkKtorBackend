package com.aldikitta.data.repository.post

import com.aldikitta.data.models.Post
import com.aldikitta.util.Constants

interface PostRepository{
    suspend fun createPostIfUserExists(post: Post): Boolean
    suspend fun deletePost(postId: String)
    suspend fun getPostByFollows(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post>

    suspend fun getPostsForProfile(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post>

    suspend fun getPost(postId: String): Post?
}
