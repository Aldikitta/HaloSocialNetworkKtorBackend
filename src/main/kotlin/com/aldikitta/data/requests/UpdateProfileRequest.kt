package com.aldikitta.data.requests

import com.aldikitta.data.responses.SkillDto

data class UpdateProfileRequest(
    val username: String,
    val bio: String,
    val githubUrl: String,
    val instagramUrl: String,
    val linkedInUrl: String,
    val skills: List<SkillDto>,
)