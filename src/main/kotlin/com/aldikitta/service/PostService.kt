package com.aldikitta.service

import com.aldikitta.data.models.Post
import com.aldikitta.data.repository.post.PostRepository
import com.aldikitta.data.requests.CreatePostRequest
import com.aldikitta.data.responses.PostResponse
import com.aldikitta.util.Constants

class PostService(
    private val postRepository: PostRepository
) {
    suspend fun createPost(request: CreatePostRequest, userId: String, imageUrl: String): Boolean {
        return postRepository.createPost(
            Post(
                imageUrl = imageUrl,
                userId = userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
        )
    }

    suspend fun getPostsForFollows(
        ownUserId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): List<PostResponse> {
        return postRepository.getPostsByFollows(
            ownUserId = ownUserId,
            page = page,
            pageSize = pageSize
        )
    }

    suspend fun getPostsForProfile(
        ownUserId: String,
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): List<PostResponse> {
        return postRepository.getPostsForProfile(
            ownUserId = ownUserId,
            userId = userId,
            page = page,
            pageSize = pageSize
        )
    }

    suspend fun getPost(postId: String): Post? = postRepository.getPost(postId)

    suspend fun getPostDetails(ownUserId: String, postId: String): PostResponse? {
        return postRepository.getPostDetails(ownUserId, postId)
    }

    suspend fun deletePost(postId: String) {
        postRepository.deletePost(postId)
    }
}