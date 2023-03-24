package com.aldikitta.data.models

import com.aldikitta.data.responses.ProfileResponse
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val id: String = ObjectId().toString(),
    val email: String,
    val username: String,
    val password: String,
    val profileImageUrl: String,
    val bannerUrl: String?,
    val bio: String,
    val skills: List<String> = listOf(),
    val githubUrl: String?,
    val instagramUrl: String?,
    val linkedinUrl: String?,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val postCount: Int = 0,
)