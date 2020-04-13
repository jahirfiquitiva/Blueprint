package dev.jahir.blueprint.data.tasks

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dev.jahir.blueprint.data.models.RequestApp
import kotlinx.coroutines.launch

object SendIconRequest {

    private suspend fun send(context: Context?) {}

    private suspend fun zipFiles(context: Context?, selectedApps: ArrayList<RequestApp>): Boolean {
        return true
    }

    fun sendIconRequest(
        activity: FragmentActivity?,
        selectedApps: ArrayList<RequestApp>,
        onFinish: (Boolean) -> Unit = {}
    ) {
        activity?.let { actvt ->
            if (selectedApps.isNotEmpty()) {
                actvt.lifecycleScope.launch {
                    val success = zipFiles(activity, selectedApps)
                    onFinish(success)
                }
            } else onFinish(false)
        } ?: { onFinish(false) }()
    }
}