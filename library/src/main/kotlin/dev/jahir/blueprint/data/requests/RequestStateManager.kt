@file:Suppress("unused")

package dev.jahir.blueprint.data.requests

import android.annotation.SuppressLint
import android.content.Context
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.BlueprintPreferences
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.frames.extensions.context.integer

@Suppress("MemberVisibilityCanBePrivate")
internal object RequestStateManager {
    internal fun saveRequestMoment(context: Context?) {
        context ?: return
        BlueprintPreferences(context).savedTime = System.currentTimeMillis()
    }

    internal fun saveRequestsLeft(context: Context?, requestsLeft: Int) {
        context ?: return
        BlueprintPreferences(context).maxApps = requestsLeft
    }

    @SuppressLint("SimpleDateFormat")
    internal fun getTimeLeft(context: Context?): Long {
        context ?: return -1
        val savedTime = BlueprintPreferences(context).savedTime
        if (savedTime < 0) return -1
        val elapsedTime = System.currentTimeMillis() - savedTime
        return context.integer(R.integer.time_limit_in_minutes) - elapsedTime - 500
    }

    internal fun getRequestsLeft(context: Context?): Int {
        context ?: return 0
        val prefs = BlueprintPreferences(context)
        val requestsLeft = prefs.maxApps
        return if (requestsLeft > -1) {
            requestsLeft
        } else {
            val requestLimit = context.integer(R.integer.max_apps_to_request, -1)
            saveRequestsLeft(context, requestLimit)
            prefs.maxApps
        }
    }

    internal fun getRequestState(
        context: Context?,
        selectedApps: ArrayList<RequestApp>,
        building: Boolean = false
    ): RequestState {
        context ?: return RequestState.STATE_UNKNOWN
        val requestLimit = context.integer(R.integer.max_apps_to_request, -1)
        val timeLimit = context.integer(R.integer.time_limit_in_minutes, -1)
        if (requestLimit <= 0 || timeLimit <= 0) return RequestState.STATE_NORMAL
        val extra = if (building) 0 else 1
        val requestsLeft = getRequestsLeft(context)
        val timeLeft = getTimeLeft(context)
        return when {
            (selectedApps.size + extra) > requestsLeft -> {
                when {
                    getTimeLeft(context) > 0 -> RequestState.STATE_TIME_LIMITED
                    requestsLeft == 0 -> {
                        saveRequestsLeft(context, -1)
                        RequestState.STATE_NORMAL
                    }
                    else -> RequestState.STATE_COUNT_LIMITED
                }
            }
            timeLeft > 0 -> RequestState.STATE_TIME_LIMITED
            else -> RequestState.STATE_NORMAL
        }
    }
}