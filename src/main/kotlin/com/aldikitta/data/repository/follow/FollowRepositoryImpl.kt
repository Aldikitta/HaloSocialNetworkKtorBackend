package com.aldikitta.data.repository.follow

import com.aldikitta.data.models.Following
import com.aldikitta.data.models.User
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class FollowRepositoryImpl(
    db: CoroutineDatabase
) : FollowRepository {
    private val following = db.getCollection<Following>()
    private val users = db.getCollection<User>()
    override suspend fun followUserIfExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean {
        val doesFollowingUserExist = users.findOneById(followingUserId) != null
        val doesFollowedUserExist = users.findOneById(followedUserId) != null
        if (!doesFollowingUserExist || !doesFollowedUserExist) {
            return false
        }
        following.insertOne(
            Following(
                followedUserId = followedUserId,
                followingUserId = followingUserId
            )
        )
        return true
    }

    override suspend fun unfollowIfUserExists(followingUserId: String, followedUserId: String): Boolean {
        val deleteResult = following.deleteOne(
            and(
                Following::followingUserId eq followingUserId,
                Following::followedUserId eq followedUserId
            )
        )
        return deleteResult.deletedCount > 0
    }

    override suspend fun doesUserFollow(followingUserId: String, followedUserId: String): Boolean {
        return following.findOne(
            and(
                Following::followingUserId eq followingUserId,
                Following::followedUserId eq followedUserId
            )
        ) != null
    }

    override suspend fun getFollowsByUser(userId: String): List<Following> {
        return following.find(Following::followingUserId eq userId).toList()
    }
}