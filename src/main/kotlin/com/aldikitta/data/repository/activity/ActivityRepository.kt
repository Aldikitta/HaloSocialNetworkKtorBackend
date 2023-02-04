package com.aldikitta.data.repository.activity

import com.aldikitta.data.models.Activity
import com.aldikitta.util.Constants

interface ActivityRepository {
    suspend fun getActivitiesForUser(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Activity>

    suspend fun createActivity(activity: Activity)

    suspend fun deleteActivity(activityId: String): Boolean
}