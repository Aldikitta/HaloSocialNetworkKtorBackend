package com.aldikitta.service

import com.aldikitta.data.models.Post
import com.aldikitta.data.repository.post.PostRepository
import com.aldikitta.data.requests.CreatePostRequest
import com.aldikitta.util.Constants

class PostService(
    private val postRepository: PostRepository
) {
    suspend fun createPostIfUserExists(request: CreatePostRequest, userId: String): Boolean {
        return postRepository.createPostIfUserExists(
            Post(
                imageUrl = "",
                userId = userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
        )
    }

    suspend fun getPostsForFollows(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post> {
        return postRepository.getPostByFollows(
            userId = userId,
            page = page,
            pageSize = pageSize
        )
    }

    suspend fun getPostsForProfile(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post> {
        return postRepository.getPostsForProfile(
            userId = userId,
            page = page,
            pageSize = pageSize
        )
    }

    suspend fun getPost(postId: String): Post? = postRepository.getPost(postId)

    suspend fun deletePost(postId: String) {
        postRepository.deletePost(postId)
    }
}