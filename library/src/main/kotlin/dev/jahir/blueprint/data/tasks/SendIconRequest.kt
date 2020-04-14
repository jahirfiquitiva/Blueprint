package dev.jahir.blueprint.data.tasks

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.blueprint.extensions.clean
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.resources.hasContent
import kotlinx.coroutines.launch
import java.io.File

object SendIconRequest {

    private var requestInProgress: Boolean = false

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
        onFinish: (Boolean) -> Unit = {}
    ) {
        if (requestInProgress) return
        activity?.let { actvt ->
            val email: String = actvt.string(R.string.email)
            if (selectedApps.isNotEmpty() && email.hasContent()) {
                actvt.lifecycleScope.launch {
                    val emailSubject: String = actvt.string(R.string.request_title, "Icons Request")
                    requestInProgress = true
                    Log.d("Blueprint", "Building request files")
                    val success = zipFiles(activity, selectedApps)
                    onFinish(success)
                }
            } else onFinish(false)
        } ?: { onFinish(false) }()
    }
}