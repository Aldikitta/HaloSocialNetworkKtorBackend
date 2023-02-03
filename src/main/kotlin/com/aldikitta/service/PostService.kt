package com.aldikitta.service

import com.aldikitta.data.models.Post
import com.aldikitta.data.repository.post.PostRepository
import com.aldikitta.data.requests.CreatePostRequest

class PostService(
    private val postRepository: PostRepository
) {
    suspend fun createPostIfUserExists(request: CreatePostRequest): Boolean {
        return postRepository.createPostIfUserExists(
            Post(
                imageUrl = "",
                userId = request.userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
        )
    }
}