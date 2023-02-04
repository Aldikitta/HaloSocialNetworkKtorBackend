package com.aldikitta.data.requests

data class CreateCommentRequest (
    val comment: String,
    val postId: String,
)