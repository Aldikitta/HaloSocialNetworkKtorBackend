package com.aldikitta.data.requests

data class FollowUpdateRequest (
    val followingUserId: String,
    val followedUserId: String
)