@file:Suppress("unused")

package dev.jahir.blueprint.data.requests

import android.annotation.SuppressLint
import android.content.Context
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.BlueprintPreferences
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.frames.extensions.context.integer
import java.util.concurrent.TimeUnit

@Suppress("MemberVisibilityCanBePrivate")
internal object RequestStateManager {
    internal fun registerRequestAttempt(context: Context, selectedAppsCount: Int = -1) {
        if (selectedAppsCount > 0) {
            val requestsLeft = getRequestsLeft(context)
            val amount = requestsLeft - selectedAppsCount
            val rightAmount = if (amount < 0) 0 else amount
            saveRequestsLeft(context, rightAmount)
            if (rightAmount <= 0) saveRequestMoment(context)
        }
    }

    private fun saveRequestMoment(context: Context) {
        BlueprintPreferences(context).savedTime = System.currentTimeMillis()
    }

    private fun saveRequestsLeft(context: Context, requestsLeft: Int) {
        BlueprintPreferences(context).maxApps = requestsLeft
    }

    private fun getTimeLimit(context: Context): Long {
        val timeLimitMinutes = context.integer(R.integer.time_limit_in_minutes)
        return TimeUnit.MINUTES.toMillis(timeLimitMinutes.toLong())
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTimeLeft(context: Context): Long {
        val savedTime = BlueprintPreferences(context).savedTime
        if (savedTime < 0) return -1
        val elapsedTime = System.currentTimeMillis() - savedTime
        return getTimeLimit(context) - elapsedTime - 500
    }

    private fun getRequestsLeft(context: Context): Int {
        val prefs = BlueprintPreferences(context)
        val requestsLeft = prefs.maxApps
        return if (requestsLeft > -1) {
            requestsLeft
        } else {
            val requestLimit = context.integer(R.integer.max_apps_to_request, 0)
            saveRequestsLeft(context, requestLimit)
            prefs.maxApps
        }
    }

    internal fun getRequestState(
        context: Context?,
        selectedApps: ArrayList<RequestApp>?,
        building: Boolean = false
    ): RequestState {
        context ?: return RequestState.UNKNOWN()
        val requestLimit = context.integer(R.integer.max_apps_to_request, -1)
        if (requestLimit <= 0 || getTimeLimit(context) <= 0) return RequestState.NORMAL()
        val extra = if (building) 0 else 1
        val requestsLeft = getRequestsLeft(context)
        val timeLeft = getTimeLeft(context)
        if ((selectedApps.orEmpty().size + extra) > requestsLeft) {
            return when {
                timeLeft > 0 -> RequestState.TIME_LIMITED(timeLeft)
                requestsLeft == 0 -> {
                    saveRequestsLeft(context, -1)
                    RequestState.NORMAL()
                }
                else -> RequestState.COUNT_LIMITED(requestsLeft)
            }
        } else if (timeLeft > 0) {
            return RequestState.TIME_LIMITED(timeLeft)
        }
        return RequestState.NORMAL()
    }
}