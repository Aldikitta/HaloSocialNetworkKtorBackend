package com.aldikitta.service

import com.aldikitta.data.models.Comment
import com.aldikitta.data.repository.comment.CommentRepository
import com.aldikitta.data.requests.CreateCommentRequest
import com.aldikitta.util.Constants

class CommentService(
    private val commentRepository: CommentRepository
) {
    suspend fun createComment(createCommentRequest: CreateCommentRequest, userId: String): ValidationEvents {
        createCommentRequest.apply {
            if (comment.isBlank() || postId.isBlank()) {
                return ValidationEvents.ErrorFieldEmpty
            }
            if (comment.length > Constants.MAX_COMMENT_LENGTH) {
                return ValidationEvents.ErrorCommentToLong
            }
        }
        commentRepository.createComment(
            Comment(
                comment = createCommentRequest.comment,
                userId = userId,
                postId = createCommentRequest.postId,
                timestamp = System.currentTimeMillis()
            )
        )
        return ValidationEvents.Success
    }

    suspend fun deleteComment(commentId: String): Boolean {
        return commentRepository.deleteComment(commentId = commentId)
    }

    suspend fun getCommentsForPost(postId: String): List<Comment>{
        return commentRepository.getCommentsForPost(postId = postId)
    }

    suspend fun getCommentById(commentId: String): Comment?{
        return commentRepository.getComment(commentId = commentId)
    }
    sealed class ValidationEvents {
        object ErrorFieldEmpty : ValidationEvents()
        object ErrorCommentToLong : ValidationEvents()
        object Success : ValidationEvents()
    }
}