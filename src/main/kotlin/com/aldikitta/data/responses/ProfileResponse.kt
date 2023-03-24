package com.aldikitta.data.responses

data class ProfileResponse (
    val username: String,
    val bio: String,
    val followerCount: Int,
    val followingCount: Int,
    val postCount: Int,
    val profilePictureUrl: String,
    val topSkillUrls: List<SkillDto>,
    val bannerUrl: String?,
    val githubUrl: String?,
    val instagramUrl: String?,
    val linkedInUrl: String?,
    val isFollowing: Boolean,
    val isOwnProfile: Boolean
)