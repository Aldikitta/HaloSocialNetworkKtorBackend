package com.aldikitta.data.util

sealed class ActivityType(val type: Int) {
    object LikedPost: ActivityType(0)
    object CommentedOnPost: ActivityType(1)
    object FollowedUser: ActivityType(2)
}

enum class ActivityTypeEnum {
    LIKEDPOST, COMMENTEDONPOST, FOLLOWEDUSER
}