@file:Suppress("RemoveExplicitTypeArguments")

package dev.jahir.blueprint.data.requests

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.blueprint.data.requests.RequestStateManager.getRequestState
import dev.jahir.blueprint.data.requests.RequestStateManager.registerRequestAttempt
import dev.jahir.blueprint.extensions.EmailBuilder
import dev.jahir.blueprint.extensions.clean
import dev.jahir.blueprint.extensions.safeDrawableName
import dev.jahir.blueprint.extensions.saveAll
import dev.jahir.blueprint.extensions.saveIcon
import dev.jahir.blueprint.extensions.zip
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.resources.createIfDidNotExist
import dev.jahir.frames.extensions.resources.deleteEverything
import dev.jahir.frames.extensions.resources.getUri
import dev.jahir.frames.extensions.resources.hasContent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SendIconRequest {
    private var requestInProgress: Boolean = false
    private var SERVICE: RequestManagerService? = null

    private fun getService(baseUrl: String) =
        SERVICE ?: Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(RequestManagerService::class.java)
            .also {
                SERVICE = it
            }

    private fun getRequestsLocationPath(basePath: File?, context: Context?): String? {
        basePath ?: return null
        context ?: return null
        return "$basePath/${context.getAppName().clean()}/Requests/"
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
            val appStorage = context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val cacheDir = context?.cacheDir

            val possibleLocations: ArrayList<String?> = arrayListOf()
            possibleLocations.add(getRequestsLocationPath(externalStorage, context))
            possibleLocations.add(getRequestsLocationPath(appStorage, context))
            possibleLocations.add(getRequestsLocationPath(cacheDir, context))

            val possibleFolders = possibleLocations.map {
                it?.let { File(it).apply { createIfDidNotExist() } }
            }
            return possibleFolders.firstOrNull {
                it?.let { it.exists() && it.isDirectory && it.canWrite() } ?: false
            }
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
            } catch (_: Exception) {
            }
        }

    private suspend fun buildTextFiles(
        context: Context?,
        correctList: ArrayList<Pair<String, RequestApp>>,
        date: String,
        uploadToRequestManager: Boolean
    ): Pair<ArrayList<File>, String> = withContext(IO) {
        val requestLocation =
            getRequestsLocation(context)
                ?: return@withContext Pair<ArrayList<File>, String>(ArrayList<File>(), "")

        var xmlSb: StringBuilder? = null
        var amSb: StringBuilder? = null
        var trSb: StringBuilder? = null
        var jsonSb: StringBuilder? = null

        if (!uploadToRequestManager) {
            xmlSb = StringBuilder(
                "<resources>\n\t<iconback img1=\"iconback\"/>\n\t<iconmask " +
                        "img1=\"iconmask\"/>\n\t<iconupon img1=\"iconupon\"/>\n\t" +
                        "<scale factor=\"1.0\"/>"
            )
        }

        if (!uploadToRequestManager) amSb = StringBuilder("<appmap>")

        if (!uploadToRequestManager) {
            trSb = StringBuilder(
                "<Theme version=\"1\">\n\t<Label value=\"${context?.getAppName()}\"/>\n\t" +
                        "<Wallpaper image=\"wallpaper_01\"/>\n\t<LockScreenWallpaper " +
                        "image=\"wallpaper_02\"/>\n\t<ThemePreview image=\"preview1\"/>\n\t" +
                        "<ThemePreviewWork image=\"preview1\"/>\n\t<ThemePreviewMenu " +
                        "image=\"preview1\"/>\n\t<DockMenuAppIcon selector=\"drawer\"/>"
            )
        }

        if (uploadToRequestManager) jsonSb = StringBuilder("{\n\t\"components\": [")

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
            if (appfilter.saveAll(xmlSb.toString())) textFiles.add(appfilter)
        }

        if (amSb != null) {
            amSb.append("\n\n</appmap>")
            val appmap = File(requestLocation, "appmap_$date.xml")
            if (appmap.saveAll(amSb.toString())) textFiles.add(appmap)
        }

        if (trSb != null) {
            trSb.append("\n\n</Theme>")
            val themeRes = File(requestLocation, "theme_resources_$date.xml")
            if (themeRes.saveAll(trSb.toString())) textFiles.add(themeRes)
        }

        Pair(textFiles, jsonSb?.append("\n\t]\n}")?.toString().orEmpty())
    }

    private suspend fun buildZipFile(date: String, location: File, files: ArrayList<File>): File? =
        withContext(IO) {
            if (files.isEmpty()) return@withContext null
            try {
                File(location, "IconRequest-$date.zip").zip(files)
            } catch (e: Exception) {
                null
            }
        }

    private suspend fun zipFiles(
        context: Context?,
        selectedApps: ArrayList<RequestApp>,
        uploadToRequestManager: Boolean
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

            val iconFile = File(
                requestLocation,
                if (uploadToRequestManager) "${app.packageName}.png" else "$correctIconName.png"
            )

            try {
                val saved = iconFile.saveIcon(app.icon?.toBitmap())
                if (saved) iconFile.let { emailZipFiles.add(it) }

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

        val (textFiles, jsonContent) =
            buildTextFiles(context, correctList, date, uploadToRequestManager)
        emailZipFiles.addAll(textFiles)

        val zipFile = buildZipFile(date, requestLocation, emailZipFiles)
        Pair(zipFile, jsonContent)
    }

    private suspend fun uploadToRequestManager(
        zipFile: File?,
        jsonContent: String,
        apiKey: String,
        baseUrl: String
    ): Pair<Boolean, String?> {
        zipFile ?: return false to "File does not exist!"
        return withContext(IO) {
            var fileType = URLConnection.guessContentTypeFromName(zipFile.name)
            if (fileType == null || !fileType.hasContent()) fileType = "application/zip"
            val requestBody: RequestBody = zipFile.asRequestBody(fileType.toMediaTypeOrNull())
            val fileToUpload =
                MultipartBody.Part.createFormData("archive", zipFile.name, requestBody)
            var succeeded = false
            val message = try {
                getService(baseUrl).uploadRequest(apiKey, jsonContent, fileToUpload).let {
                    succeeded = it.status != "error"
                    it.message
                }
            } catch (e: HttpException) {
                e.message()
            } catch (e: Exception) {
                e.message
            }
            succeeded to message
        }
    }

    @Suppress("DEPRECATION")
    private fun buildEmailIntent(
        context: Context?,
        zipFile: File?,
        selectedApps: ArrayList<RequestApp>,
        callback: RequestCallback? = null
    ) {
        if (context == null || zipFile == null) {
            callback?.onRequestError("Unable to save files!")
            return
        }

        val email = context.string(R.string.email)
        val subject = context.string(R.string.request_title)
        if (!email.hasContent() || !subject.hasContent()) {
            callback?.onRequestError()
            return
        }

        val sb = StringBuilder()
        for (i in selectedApps.indices) {
            if (i > 0) sb.append("<br/><br/>")
            val app = selectedApps[i]
            sb.append("Name: <b>${app.name}</b><br/>")
            sb.append("ComponentInfo: <b>${app.component}</b><br/>")
            sb.append("Link: https://play.google.com/store/apps/details?id=${app.packageName}<br/>")
        }

        val body = sb.toString().trim()
        if (!body.hasContent()) {
            callback?.onRequestError()
            return
        }

        val zipUri = zipFile.getUri(context)

        val intent = EmailBuilder(email, subject, body).apply {
            formatAsHtml = true
            addAttachment(zipUri)
        }.buildIntent(context)

        registerRequestAttempt(context, selectedApps.size)
        callback?.onRequestEmailIntent(intent)
    }

    fun sendIconRequest(
        activity: FragmentActivity?,
        selectedApps: ArrayList<RequestApp>?,
        callback: RequestCallback? = null
    ) {
        val theCallback = callback ?: object : RequestCallback {}
        if (requestInProgress) {
            theCallback.onRequestRunning()
            return
        }
        val email: String = activity?.string(R.string.email).orEmpty()
        if (!email.hasContent()) {
            theCallback.onRequestError()
            return
        }
        if (selectedApps.isNullOrEmpty()) {
            theCallback.onRequestEmpty()
            return
        }
        requestInProgress = true
        activity?.lifecycleScope?.launch {
            val state = getRequestState(activity, selectedApps, true)
            if (state.state == RequestState.State.NORMAL) {
                theCallback.onRequestStarted()

                val apiKey = activity.string(R.string.request_manager_backend_api_key)
                val uploadToRequestManager = apiKey.hasContent()

                val (zipFile, jsonContent) =
                    zipFiles(activity, selectedApps, uploadToRequestManager)
                cleanFiles(activity)

                if (uploadToRequestManager) {
                    val baseUrl = activity.string(R.string.request_manager_base_url)
                    val (succeeded, message) =
                        uploadToRequestManager(zipFile, jsonContent, apiKey, baseUrl)
                    if (succeeded) theCallback.onRequestUploadFinished(true)
                    else theCallback.onRequestError(message)

                    registerRequestAttempt(activity, selectedApps.size)
                    cleanFiles(activity, true)
                } else buildEmailIntent(activity, zipFile, selectedApps, theCallback)
                requestInProgress = false
            } else {
                theCallback.onRequestLimited(state, true)
                requestInProgress = false
            }
        } ?: run {
            theCallback.onRequestError()
            requestInProgress = false
        }
    }
}
