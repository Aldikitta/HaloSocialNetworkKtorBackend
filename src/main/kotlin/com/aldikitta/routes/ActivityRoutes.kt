package com.aldikitta.routes

import com.aldikitta.service.ActivityService
import com.aldikitta.util.Constants
import com.aldikitta.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getActivities(
    activityService: ActivityService
) {
    authenticate {
        route("/api/activity/get") {
            get {
                val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
                val pageSize =
                    call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

                val activities = activityService.getActivitiesForUser(userId = call.userId, page = page, pageSize = pageSize)
                call.respond(
                    HttpStatusCode.OK,
                    activities
                )
            }
        }
    }
}