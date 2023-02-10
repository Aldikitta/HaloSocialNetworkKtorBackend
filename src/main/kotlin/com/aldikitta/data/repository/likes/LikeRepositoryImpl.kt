package com.aldikitta.data.repository.likes

import com.aldikitta.data.models.Like
import com.aldikitta.data.models.Post
import com.aldikitta.data.models.User
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
                    parentType = parentType,
                    timeStamp = System.currentTimeMillis()
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

    override suspend fun getLikesForParent(parentId: String, page: Int, pageSize: Int): List<Like> {
        return like
            .find(Like::parentId eq parentId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Like::timeStamp)
            .toList()
    }
}