package com.aldikitta.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Post (
    @BsonId
    val id: String = ObjectId().toString(),
    val imageUrl: String,
    val description: String,
    val userId: String,
    val timestamp: Long,
    val likeCount: Int = 0,
    val commentCount: Int = 0
)