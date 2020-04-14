package dev.jahir.blueprint.data.tasks

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.IntDef
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.BlueprintPreferences
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.blueprint.extensions.clean
import dev.jahir.blueprint.extensions.safeDrawableName
import dev.jahir.blueprint.extensions.saveAll
import dev.jahir.blueprint.extensions.saveIcon
import dev.jahir.blueprint.extensions.zip
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.integer
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.resources.createIfDidNotExist
import dev.jahir.frames.extensions.resources.deleteEverything
import dev.jahir.frames.extensions.resources.hasContent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

    private suspend fun cleanFiles(context: Context?, everything: Boolean = false) =
        withContext(IO) {
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

    private suspend fun buildTextFiles(
        context: Context?,
        correctList: ArrayList<Pair<String, RequestApp>>,
        date: String,
        uploadToArctic: Boolean
    ): Pair<ArrayList<File>, String> = withContext(IO) {
        val requestLocation =
            getRequestsLocation(context) ?: return@withContext Pair(ArrayList(), "")

        var xmlSb: StringBuilder? = null
        var amSb: StringBuilder? = null
        var trSb: StringBuilder? = null
        var jsonSb: StringBuilder? = null

        if (!uploadToArctic) {
            xmlSb = StringBuilder(
                "<resources>\n\t<iconback img1=\"iconback\"/>\n\t<iconmask " +
                        "img1=\"iconmask\"/>\n\t<iconupon img1=\"iconupon\"/>\n\t" +
                        "<scale factor=\"1.0\"/>"
            )
        }

        if (!uploadToArctic) {
            amSb = StringBuilder("<appmap>")
        }

        if (!uploadToArctic) {
            trSb = StringBuilder(
                "<Theme version=\"1\">\n\t<Label value=\"${context?.getAppName()}\"/>\n\t" +
                        "<Wallpaper image=\"wallpaper_01\"/>\n\t<LockScreenWallpaper " +
                        "image=\"wallpaper_02\"/>\n\t<ThemePreview image=\"preview1\"/>\n\t" +
                        "<ThemePreviewWork image=\"preview1\"/>\n\t<ThemePreviewMenu " +
                        "image=\"preview1\"/>\n\t<DockMenuAppIcon selector=\"drawer\"/>"
            )
        }

        if (uploadToArctic) {
            jsonSb = StringBuilder("{\n\t\"components\": [")
        }

        var isFirst = true
        for ((iconName, app) in correctList) {
            if (xmlSb != null) {
                xmlSb.append("\n\n")
                xmlSb.append("\t<!-- ${app.name} -->\n")
                xmlSb.append(
                    "\t<item\n\t\tcomponent=\"ComponentInfo{${app.component}}\"\n\t\tdrawable=\"$iconName\"/>"
                )
            }

            if (amSb != null) {
                amSb.append("\n\n")
                amSb.append("\t<!-- ${app.name} -->\n")
                val rightCode = app.component.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1]
                amSb.append(
                    "\t<item\n\t\tclass=\"$rightCode\"\n\t\tname=\"$iconName\"/>"
                )
            }

            if (trSb != null) {
                trSb.append("\n\n")
                trSb.append("\t<!-- ${app.name} -->\n")
                trSb.append("\t<AppIcon\n\t\tname=\"${app.component}\"\n\t\timage=\"$iconName\"/>")
            }

            if (jsonSb != null) {
                if (!isFirst) jsonSb.append(",")
                jsonSb.append("\n\t\t{\n")
                    .append("\t\t\t\"name\": \"${app.name}\",\n")
                    .append("\t\t\t\"pkg\": \"${app.packageName}\",\n")
                    .append("\t\t\t\"componentInfo\": \"${app.component}\",\n")
                    .append("\t\t\t\"drawable\": \"$iconName\"")
                    .append("\n\t\t}")
            }

            if (isFirst) isFirst = false
        }

        val textFiles = ArrayList<File>()
        if (xmlSb != null) {
            xmlSb.append("\n\n</resources>")
            val appfilter = File(requestLocation, "appfilter_$date.xml")
            if (appfilter.saveAll(xmlSb.toString()))
                textFiles.add(appfilter)
        }

        if (amSb != null) {
            amSb.append("\n\n</appmap>")
            val appmap = File(requestLocation, "appmap_$date.xml")
            if (appmap.saveAll(amSb.toString()))
                textFiles.add(appmap)
        }

        if (trSb != null) {
            trSb.append("\n\n</Theme>")
            val themeRes = File(requestLocation, "theme_resources_$date.xml")
            if (themeRes.saveAll(trSb.toString()))
                textFiles.add(themeRes)
        }

        Pair(textFiles, jsonSb?.append("\n\t]\n}")?.toString().orEmpty())
    }

    private suspend fun buildZipFile(date: String, location: File, files: ArrayList<File>): File? {
        if (files.isEmpty()) return null
        return try {
            File(location, "IconRequest-$date.zip").zip(files)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun zipFiles(
        context: Context?,
        selectedApps: ArrayList<RequestApp>,
        uploadToArctic: Boolean
    ): Pair<File?, String> = withContext(IO) {
        context ?: return@withContext Pair(null, "")

        val requestLocation = getRequestsLocation(context) ?: return@withContext Pair(null, "")
        cleanFiles(context, true)
        requestLocation.deleteEverything()
        requestLocation.createIfDidNotExist()

        val emailZipFiles = ArrayList<File>()
        val correctList = ArrayList<Pair<String, RequestApp>>()
        val iconsNames = ArrayList<Pair<String, Int>>()

        for (app in selectedApps) {
            app.icon ?: continue

            val iconName = app.name.safeDrawableName()
            var correctIconName = iconName

            val inList = iconsNames.find { it.first.equals(iconName, true) }
            if (inList != null) correctIconName += "_${inList.second}"

            val iconFile: File? = File(
                requestLocation,
                if (uploadToArctic) "${app.packageName}.png" else "$correctIconName.png"
            )

            try {
                val saved = iconFile?.saveIcon(app.icon?.toBitmap()) == true
                if (saved) iconFile?.let { emailZipFiles.add(it) }

                val count =
                    (iconsNames.find { it.first.equals(iconName, true) }?.second ?: 0) + 1
                try {
                    iconsNames.removeAt(
                        iconsNames.indexOfFirst { it.first.equals(iconName, true) }
                    )
                } catch (ignored: Exception) {
                }
                iconsNames.add(Pair(iconName, count))
                correctList.add(Pair(correctIconName, app))
            } catch (e: Exception) {
                continue
            }
        }

        val date = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()).clean()

        val (textFiles, jsonContent) = buildTextFiles(context, correctList, date, uploadToArctic)
        emailZipFiles.addAll(textFiles)

        val zipFile = buildZipFile(date, requestLocation, emailZipFiles)

        Pair(zipFile, jsonContent)
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
        requestInProgress = true
        activity?.lifecycleScope?.launch {
            val state = getRequestState(activity, selectedApps, true)
            if (state == STATE_NORMAL) {
                callback?.onStarted()
                val emailSubject: String = activity.string(R.string.request_title, "Icons Request")
                val host = activity.string(R.string.arctic_backend_host)
                val apiKey = activity.string(R.string.arctic_backend_api_key)
                val uploadToArctic = host.hasContent() && apiKey.hasContent()

                val (zipFile, jsonContent) = zipFiles(activity, selectedApps, uploadToArctic)
                // TODO: Submit zipFile to Arctic or build activity intent
                callback?.onFinished(true)
            } else {
                callback?.onLimited(state, getRequestsLeft(activity), getTimeLeft(activity), true)
            }
        } ?: { callback?.onError() }()
    }
}