package dev.jahir.blueprint.data.requests

import android.content.Intent
import android.util.Log

interface RequestCallback {
    fun onRequestRunning() {}
    fun onRequestStarted() {}
    fun onRequestUploadFinished(success: Boolean) {}
    fun onRequestEmpty() {}
    fun onRequestEmailIntent(intent: Intent?) {}

    fun onRequestError(reason: String? = null, e: Throwable? = null) {
        e?.printStackTrace()
    }

    fun onRequestLimited(state: RequestState, building: Boolean = false) {
        Log.d(
            "Blueprint",
            "Request limited (${state.state.name})! Apps left: ${state.requestsLeft} - Time left: ${state.timeLeft}"
        )
    }
}
