package com.aldikitta.data.repository.likes

import com.aldikitta.data.models.Like
import com.aldikitta.data.models.User
import com.aldikitta.data.util.ParentType
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class LikeRepositoryImpl(
    db: CoroutineDatabase
) : LikeRepository {
    private val like = db.getCollection<Like>()
    private val users = db.getCollection<User>()
    override suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean {
        val doesUserExist = users.findOneById(userId) != null
        return if (doesUserExist) {
            like.insertOne(
                Like(
                    userId = userId,
                    parentId = parentId,
                    parentType = parentType
                )
            )
            true
        } else {
            false
        }
    }

    override suspend fun unLikeParent(userId: String, parentId: String): Boolean {
        val doesUserExist = users.findOneById(userId) != null
        return if (doesUserExist) {
            like.deleteOne(
                and(
                    Like::userId eq userId,
                    Like::parentId eq parentId
                )
            )
            true
        } else {
            false
        }
    }

    override suspend fun deleteLikesForParent(parentId: String) {
        like.deleteMany(Like::parentId eq parentId)
    }
}