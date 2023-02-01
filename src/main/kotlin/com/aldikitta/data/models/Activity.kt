package com.aldikitta.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Activity(
    @BsonId
    val id: String = ObjectId().toString(),
    val timestamp: String,
    val byUserId: String,
    val toUSerId: String,
    val type: Int,
    val parentId: String
)

