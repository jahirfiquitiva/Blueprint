package dev.jahir.blueprint.data.tasks

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.IntDef
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.BlueprintPreferences
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.blueprint.extensions.clean
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.integer
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.resources.hasContent
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object SendIconRequest {
    internal const val STATE_UNKNOWN = -1
    internal const val STATE_NORMAL = 0
    internal const val STATE_COUNT_LIMITED = 1
    internal const val STATE_TIME_LIMITED = 2

    @IntDef(STATE_UNKNOWN, STATE_NORMAL, STATE_COUNT_LIMITED, STATE_TIME_LIMITED)
    @Retention(AnnotationRetention.SOURCE)
    annotation class State

    interface RequestCallback {
        fun onRunning() {}
        fun onStarted() {}
        fun onFinished(success: Boolean) {}
        fun onEmpty() {}
        fun onError(reason: String? = null, e: Throwable? = null) {}
        fun onLimited(
            @State reason: Int,
            requestsLeft: Int,
            timeLeft: Long,
            building: Boolean = false
        ) {
        }
    }

    private var requestInProgress: Boolean = false

    @SuppressLint("SimpleDateFormat")
    private fun getTimeLeft(context: Context?): Long {
        context ?: return -1
        val savedTime = BlueprintPreferences(context).savedTime
        if (savedTime < 0) return -1
        val elapsedTime = System.currentTimeMillis() - savedTime
        val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm:ss")
        val timeLeft = context.integer(R.integer.time_limit_in_minutes) - elapsedTime - 500
        // TODO: Delete log
        Log.d(
            "Blueprint",
            "\nRequest: [ Last: ${sdf.format(Date(savedTime))},\n" +
                    "Now: ${sdf.format(Date(System.currentTimeMillis()))}," +
                    "\nTime Left: ~ ${timeLeft / 1000} secs. aprox. ]"
        )
        return timeLeft
    }

    private fun saveRequestsLeft(context: Context?, requestsLeft: Int) {
        context ?: return
        BlueprintPreferences(context).maxApps = requestsLeft
    }

    private fun getRequestsLeft(context: Context?): Int {
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

    @State
    internal fun getRequestState(
        context: Context?,
        selectedApps: ArrayList<RequestApp>,
        building: Boolean = false
    ): Int {
        context ?: return STATE_UNKNOWN
        val requestLimit = context.integer(R.integer.max_apps_to_request, -1)
        val timeLimit = context.integer(R.integer.time_limit_in_minutes, -1)
        if (requestLimit <= 0 || timeLimit <= 0) return STATE_NORMAL
        val extra = if (building) 0 else 1
        val requestsLeft = getRequestsLeft(context)
        val timeLeft = getTimeLeft(context)
        return when {
            (selectedApps.size + extra) > requestsLeft -> {
                when {
                    getTimeLeft(context) > 0 -> STATE_TIME_LIMITED
                    requestsLeft == 0 -> {
                        saveRequestsLeft(context, -1)
                        STATE_NORMAL
                    }
                    else -> STATE_COUNT_LIMITED
                }
            }
            timeLeft > 0 -> STATE_TIME_LIMITED
            else -> STATE_NORMAL
        }
    }

    @Suppress("DEPRECATION")
    private fun getRequestsLocation(context: Context?): File? {
        try {
            val externalStorage = try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    Environment.getExternalStorageDirectory()
                } else {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                }
            } catch (e: Exception) {
                null
            }
            val appStorage = context?.getExternalFilesDir(null)
            val defFolder =
                if (appStorage?.absolutePath?.contains(context.packageName) == true) externalStorage
                else appStorage
            return File("$defFolder/${context?.getAppName()?.clean() ?: "Blueprint"}/Requests/")
        } catch (e: Exception) {
            return null
        }
    }

    private fun cleanFiles(context: Context?, everything: Boolean = false) {
        try {
            val files = getRequestsLocation(context)?.listFiles()
            files?.forEach {
                if (!it.isDirectory &&
                    (everything || (it.name.endsWith(".png") || it.name.endsWith(".xml")))) {
                    it.delete()
                }
            }
        } catch (e: Exception) {
        }
    }

    private suspend fun send(context: Context?) {}

    private suspend fun zipFiles(context: Context?, selectedApps: ArrayList<RequestApp>): Boolean {
        return true
    }

    fun sendIconRequest(
        activity: FragmentActivity?,
        selectedApps: ArrayList<RequestApp>,
        callback: RequestCallback? = null
    ) {
        if (requestInProgress) {
            callback?.onRunning()
            return
        }
        val email: String = activity?.string(R.string.email).orEmpty()
        if (!email.hasContent()) {
            callback?.onError()
            return
        }
        if (selectedApps.isNullOrEmpty()) {
            callback?.onEmpty()
            return
        }
        activity?.lifecycleScope?.launch {
            val state = getRequestState(activity, selectedApps, true)
            if (state == STATE_NORMAL) {
                callback?.onStarted()
                val emailSubject: String = activity.string(R.string.request_title, "Icons Request")
                requestInProgress = true
                Log.d("Blueprint", "Building request files")
                val success = zipFiles(activity, selectedApps)
                callback?.onFinished(success)
            } else {
                callback?.onLimited(state, getRequestsLeft(activity), getTimeLeft(activity), true)
            }
        } ?: { callback?.onError() }()
    }
}