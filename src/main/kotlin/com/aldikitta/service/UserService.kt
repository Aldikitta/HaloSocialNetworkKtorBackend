package com.aldikitta.service

import com.aldikitta.data.models.User
import com.aldikitta.data.repository.follow.FollowRepository
import com.aldikitta.data.repository.user.UserRepository
import com.aldikitta.data.requests.CreateAccountRequest
import com.aldikitta.data.requests.LoginRequest
import com.aldikitta.data.requests.UpdateProfileRequest
import com.aldikitta.data.responses.ProfileResponse
import com.aldikitta.data.responses.UserResponseItem
import com.aldikitta.util.Constants.DEFAULT_BANNER_IMAGE_PATH
import com.aldikitta.util.Constants.DEFAULT_PROFILE_PICTURE_PATH

class UserService(
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {
    suspend fun doesUserWithEmailExist(email: String): Boolean {
        return userRepository.getUserByEmail(email = email) != null
    }

    suspend fun getUserProfile(userId: String, callerUserId: String): ProfileResponse? {
        val user = userRepository.getUserById(userId) ?: return null
        return ProfileResponse(
            userId = user.id,
            username = user.username,
            bio = user.bio,
            followerCount = user.followerCount,
            followingCount = user.followingCount,
            profilePictureUrl = user.profileImageUrl,
            postCount = user.postCount,
            topSkills = user.skills,
            githubUrl = user.githubUrl,
            instagramUrl = user.instagramUrl,
            linkedInUrl = user.linkedinUrl,
            isOwnProfile = userId == callerUserId,
            bannerUrl = user.bannerUrl,
            isFollowing = if (userId != callerUserId) {
                followRepository.doesUserFollow(callerUserId, userId)
            } else {
                false
            }
        )
    }

    suspend fun getUserByEmail(email: String): User? {
        return userRepository.getUserByEmail(email)
    }

    fun isValidPassword(enteredPassword: String, actualPassword: String): Boolean {
        return enteredPassword == actualPassword
    }

    suspend fun updateUser(
        userId: String,
        profileImageUrl: String?,
        bannerUrl: String?,
        updateProfileRequest: UpdateProfileRequest
    ): Boolean {
        return userRepository.updateUser(
            userId = userId,
            profileImageUrl = profileImageUrl,
            updateProfileRequest = updateProfileRequest,
            bannerUrl = bannerUrl
        )
    }

    suspend fun searchForUsers(query: String, userId: String): List<UserResponseItem> {
        val users = userRepository.searchForUsers(query)
        val followsByUser = followRepository.getFollowsByUser(userId)
        return users.map { user ->
            val isFollowing = followsByUser.find { it.followedUserId == user.id } != null
            UserResponseItem(
                userId = user.id,
                username = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = isFollowing
            )
        }
    }

    suspend fun createUser(request: CreateAccountRequest) {
        userRepository.createUser(
            user = User(
                email = request.email,
                username = request.username,
                password = request.password,
                profileImageUrl = DEFAULT_PROFILE_PICTURE_PATH,
                bannerUrl = DEFAULT_BANNER_IMAGE_PATH,
                bio = "",
                githubUrl = null,
                instagramUrl = null,
                linkedinUrl = null,
            )
        )
    }

    fun validateCreateAccountRequest(request: CreateAccountRequest): ValidationEvent {
        if (request.email.isBlank() || request.username.isBlank() || request.password.isBlank()) {
            return ValidationEvent.ErrorFieldEmpty
        }
        return ValidationEvent.Success
    }

    sealed class ValidationEvent {
        object ErrorFieldEmpty : ValidationEvent()
        object Success : ValidationEvent()
    }
}