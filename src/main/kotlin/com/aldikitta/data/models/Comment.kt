package com.aldikitta.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Comment(
    @BsonId
    val id: String = ObjectId().toString(),
    val username: String,
    val profileImageUrl: String,
    val comment: String,
    val userId: String,
    val postId: String,
    val timestamp: Long,
    val likeCount: Int
)
