package com.aldikitta.data.repository.activity

import com.aldikitta.data.models.Activity
import com.aldikitta.data.models.User
import com.aldikitta.data.responses.ActivityResponse
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class ActivityRepositoryImpl(
    db: CoroutineDatabase
) : ActivityRepository {
    private val users = db.getCollection<User>()
    private val activities = db.getCollection<Activity>()
    override suspend fun getActivitiesForUser(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<ActivityResponse> {
        val activities = activities.find(Activity::toUserId eq userId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Activity::timestamp)
            .toList()
        val userIds = activities.map { activity ->
            activity.byUserId
        }
        val users = users.find(User::id `in` userIds).toList()
        return activities.mapIndexed { index, activity ->
            ActivityResponse(
                timestamp = activity.timestamp,
                userId = activity.byUserId,
                parentId = activity.parentId,
                type = activity.type,
                username = users[index].username,
                id = activity.id
            )
        }
    }

    override suspend fun createActivity(activity: Activity) {
        activities.insertOne(activity)
    }

    override suspend fun deleteActivity(activityId: String): Boolean {
        return activities.deleteOneById(activityId).wasAcknowledged()
    }
}