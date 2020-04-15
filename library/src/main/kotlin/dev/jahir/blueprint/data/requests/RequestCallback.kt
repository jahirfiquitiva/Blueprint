package dev.jahir.blueprint.data.requests

import android.content.Intent
import android.util.Log

interface RequestCallback {
    fun onRequestRunning() {
        Log.d("Blueprint", "Request in progress!")
    }

    fun onRequestStarted() {
        Log.d("Blueprint", "Request started!")
    }

    fun onRequestUploadFinished(success: Boolean) {
        Log.d("Blueprint", "Request finished! - Success? $success")
    }

    fun onRequestEmpty() {
        Log.w("Blueprint", "Nothing to request")
    }

    fun onRequestError(reason: String? = null, e: Throwable? = null) {
        Log.e("Blueprint", "Request ERROR")
    }

    fun onRequestLimited(state: RequestState, building: Boolean = false) {
        Log.d(
            "Blueprint",
            "Request limited (${state.state.name})! Apps left: ${state.requestsLeft} - Time left: ${state.timeLeft}"
        )
    }

    fun onRequestEmailIntent(intent: Intent?) {
        Log.d("Blueprint", "Should send request via email!")
    }
}