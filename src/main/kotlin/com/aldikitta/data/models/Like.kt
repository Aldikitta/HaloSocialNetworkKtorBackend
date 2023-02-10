package com.aldikitta.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Like(
    @BsonId
    val id: String = ObjectId().toString(),
    val userId: String,
    val parentId: String,
    val parentType: Int,
    val timeStamp: Long
)