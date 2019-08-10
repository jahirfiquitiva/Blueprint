/*
 * Copyright (c) 2018. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "DEPRECATION", "ProtectedInFinal")

package jahirfiquitiva.libs.blueprint.quest

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.XmlResourceParser
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.text.Html
import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.annotation.WorkerThread
import androidx.annotation.XmlRes
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.DataPart
import jahirfiquitiva.libs.blueprint.BuildConfig
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.BL
import jahirfiquitiva.libs.blueprint.quest.events.RequestsCallback
import jahirfiquitiva.libs.blueprint.quest.events.SendRequestCallback
import jahirfiquitiva.libs.blueprint.quest.utils.getInstalledApps
import jahirfiquitiva.libs.blueprint.quest.utils.safeDrawableName
import jahirfiquitiva.libs.blueprint.quest.utils.saveAll
import jahirfiquitiva.libs.blueprint.quest.utils.saveIcon
import jahirfiquitiva.libs.blueprint.quest.utils.wipe
import jahirfiquitiva.libs.blueprint.quest.utils.zip
import jahirfiquitiva.libs.frames.helpers.extensions.jfilter
import jahirfiquitiva.libs.kext.extensions.getAppVersion
import jahirfiquitiva.libs.kext.extensions.getAppVersionCode
import jahirfiquitiva.libs.kext.extensions.getUri
import jahirfiquitiva.libs.kext.extensions.hasContent
import jahirfiquitiva.libs.kext.extensions.readBooleanCompat
import jahirfiquitiva.libs.kext.extensions.resource
import jahirfiquitiva.libs.kext.extensions.string
import jahirfiquitiva.libs.kext.extensions.toBitmap
import jahirfiquitiva.libs.kext.extensions.writeBooleanCompat
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.HashSet
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Created by Allan Wang on 2016-08-20.
 */
class IconRequest private constructor() {
    
    @State
    private val _state = STATE_NORMAL
    
    private var builder: Builder? = null
    
    val apps: ArrayList<App> = ArrayList()
    val selectedApps: ArrayList<App> = ArrayList()
    
    val maxSelectable: Int
        get() = builder?.maxCount ?: -1
    
    private val body: String
        get() {
            val sb = StringBuilder()
            
            for (i in selectedApps.indices) {
                if (i > 0) sb.append("<br/><br/>")
                val app = selectedApps[i]
                sb.append("Name: <b>${app.name}</b><br/>")
                sb.append("ComponentInfo: <b>${app.comp}</b><br/>")
                sb.append("Link: https://play.google.com/store/apps/details?id=${app.pkg}<br/>")
            }
            
            sb.append("<br/>Blueprint Version: ${BuildConfig.LIB_VERSION}")
            sb.append(
                "<br/>App Version: ${builder?.context?.getAppVersionCode()} " +
                    "(${builder?.context?.getAppVersion()}) from " +
                    "${builder?.context?.packageManager?.getInstallerPackageName(
                        builder?.context?.packageName ?: "")}")
            return sb.toString()
        }
    
    val isNotEmpty: Boolean
        get() = apps.isNotEmpty()
    
    val isLoading: Boolean
        get() = builder?.isLoading ?: false
    
    private val timeLeft: Long
        @SuppressLint("SimpleDateFormat")
        get() {
            val savedTime: Long = builder?.prefs?.getLong(KEY_SAVED_TIME_MILLIS, -1) ?: -1
            if (savedTime < 0) return -1
            val elapsedTime = System.currentTimeMillis() - savedTime
            val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm:ss")
            val timeLeft = (builder?.timeLimit ?: 0) - elapsedTime - 500
            BL.d(
                "\nQuest: [ Last: ${sdf.format(Date(savedTime))},\n" +
                    "Now: ${sdf.format(Date(System.currentTimeMillis()))}," +
                    "\nTime Left: ~ ${timeLeft / 1000} secs. aprox. ]")
            return timeLeft
        }
    
    private val requestsLeft: Int
        get() {
            val requestsLeft = builder?.prefs?.getInt(MAX_APPS, -1) ?: -1
            return if (requestsLeft > -1) {
                requestsLeft
            } else {
                saveRequestsLeft(builder?.maxCount ?: 0)
                builder?.prefs?.getInt(MAX_APPS, builder?.maxCount ?: 0) ?: 0
            }
        }
    
    private constructor(builder: Builder) : this() {
        this.builder = builder
        request = this
    }
    
    @IntDef(STATE_NORMAL, STATE_COUNT_LIMITED, STATE_TIME_LIMITED)
    @Retention(AnnotationRetention.SOURCE)
    annotation class State
    
    class Builder : Parcelable {
        
        @Transient var context: Context? = null
        var saveDir: File? = null
            private set
        var appName: String = "Blueprint"
            private set
        
        var subject: String = "Icon Request"
        var email: String = "someone@mail.co"
            private set
        
        var apiHost: String = "http://arcticmanager.com/"
            private set
        var apiKey: String? = null
            private set
        
        var filterId = -1
            private set
        
        var maxCount = 0
            private set
        var timeLimit: Long = -1
            private set
        
        var prefs: SharedPreferences? = null
            private set
        var callback: RequestsCallback? = null
            private set
        
        var isLoading = false
        var debug = false
            private set
        
        constructor()
        
        constructor(context: Context) {
            this.context = context
            this.saveDir = File(Environment.getExternalStorageDirectory(), "IconRequest")
        }
        
        fun withAppName(appName: String, vararg args: Any): Builder {
            this.appName = if (args.isNotEmpty()) String.format(appName, args) else appName
            return this
        }
        
        fun withSubject(subject: String, vararg args: Any): Builder {
            this.subject = if (args.isNotEmpty()) String.format(subject, args) else subject
            return this
        }
        
        fun toEmail(email: String): Builder {
            this.email = email
            return this
        }
        
        fun withAPIHost(host: String): Builder {
            this.apiHost = host
            return this
        }
        
        fun withAPIKey(key: String?): Builder {
            val actualKey = key.orEmpty()
            this.apiKey = actualKey
            return this
        }
        
        fun saveDir(file: File): Builder {
            this.saveDir = file
            return this
        }
        
        fun filterXml(@XmlRes resId: Int): Builder {
            this.filterId = resId
            return this
        }
        
        fun withTimeLimit(minutes: Int, prefs: SharedPreferences?): Builder {
            this.prefs = prefs ?: context?.getSharedPreferences("quest_prefs", Context.MODE_PRIVATE)
            this.timeLimit = TimeUnit.MINUTES.toMillis(minutes.toLong())
            return this
        }
        
        fun maxSelectionCount(@IntRange(from = 0) count: Int): Builder {
            this.maxCount = count
            return this
        }
        
        fun setCallback(callback: RequestsCallback?): Builder {
            this.callback = callback
            return this
        }
        
        fun enableDebug(enable: Boolean): Builder {
            this.debug = enable
            return this
        }
        
        fun build(): IconRequest = IconRequest(this)
        
        override fun describeContents(): Int = 0
        
        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeSerializable(saveDir)
            dest.writeString(appName)
            dest.writeString(subject)
            dest.writeString(email)
            dest.writeString(apiHost)
            dest.writeString(apiKey.orEmpty())
            dest.writeInt(filterId)
            dest.writeInt(maxCount)
            dest.writeLong(timeLimit)
            dest.writeBooleanCompat(debug)
        }
        
        protected constructor(parcel: Parcel) {
            saveDir = parcel.readSerializable() as File
            appName = parcel.readString() ?: ""
            subject = parcel.readString() ?: ""
            email = parcel.readString() ?: ""
            apiHost = parcel.readString() ?: ""
            apiKey = parcel.readString()
            filterId = parcel.readInt()
            maxCount = parcel.readInt()
            timeLimit = parcel.readLong()
            debug = parcel.readBooleanCompat()
        }
        
        companion object CREATOR : Parcelable.Creator<Builder> {
            override fun createFromParcel(parcel: Parcel): Builder = Builder(parcel)
            override fun newArray(size: Int): Array<Builder?> = arrayOfNulls(size)
        }
    }
    
    @CallSuper
    @CheckResult
    private fun loadFilterApps(): HashSet<String>? {
        val defined = HashSet<String>()
        if (builder?.filterId == 0) {
            return defined
        }
        
        val componentsCount = ArrayList<Pair<String, Int>>()
        
        var parser: XmlResourceParser? = null
        try {
            parser = builder?.context?.resources?.getXml(builder?.filterId ?: 0)
            var eventType = parser?.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    val tagName = parser?.name
                    if (tagName == "item") {
                        getComponentInAppFilter(parser) { component ->
                            defined.add(component)
                            val count =
                                (componentsCount.find { it.first == component }?.second ?: 0) + 1
                            try {
                                componentsCount.removeAt(
                                    componentsCount.indexOfFirst { it.first == component })
                            } catch (ignored: Exception) {
                            }
                            componentsCount += component to count
                        }
                    }
                }
                eventType = parser?.next()
            }
        } catch (e: Exception) {
            BL.e("Error", e)
        } finally {
            parser?.close()
        }
        
        if (builder?.debug == true) {
            componentsCount.forEach {
                if (it.second > 1)
                    BL.w("Component \"${it.first}\" is duplicated ${it.second} time(s)")
            }
        }
        
        return defined
    }
    
    private fun getComponentInAppFilter(parser: XmlPullParser?, onSuccess: (String) -> Unit) {
        try {
            // Read package and activity name
            val component =
                parser?.getAttributeValue(null, "component").orEmpty()
            
            val drawable = parser?.getAttributeValue(null, "drawable").orEmpty()
            
            if (component.hasContent() && !component.startsWith(":")) {
                val actualComponent = component.substring(14, component.length - 1)
                if (actualComponent.hasContent() && !actualComponent.startsWith("/")
                    && !actualComponent.endsWith("/")) {
                    
                    if (builder?.debug == true) {
                        if (drawable.hasContent()) {
                            val res = builder?.context?.resource(drawable)
                            if (res == 0)
                                BL.w(
                                    "Drawable \"$drawable\" NOT found for component: \"$actualComponent\"")
                        } else {
                            BL.w("No drawable found for component: \"$actualComponent\"")
                        }
                    }
                    
                    onSuccess(actualComponent)
                } else {
                    if (builder?.debug == true)
                        BL.w("Found invalid component: \"$actualComponent\"")
                }
            }
        } catch (e: Exception) {
            BL.e(
                "Error adding parsed appfilter item! Due to Exception: ${e.message}")
        }
    }
    
    fun loadApps() {
        if (builder?.isLoading == true) return
        builder?.isLoading = true
        if (apps.isNotEmpty()) {
            builder?.callback?.onAppsLoaded(apps)
            builder?.isLoading = false
            return
        }
        Thread {
            BL.d("Loading apps to request...")
            val filter = loadFilterApps() ?: return@Thread
            apps.clear()
            apps.addAll(builder?.context?.getInstalledApps(filter, builder?.callback).orEmpty())
            builder?.isLoading = false
            builder?.callback?.onAppsLoaded(apps)
        }.start()
    }
    
    fun loadHighResIcons() {
        if (apps.isEmpty()) return
        Thread {
            apps.let {
                for (app in it) builder?.context?.let { app.getIcon(it) }
            }
        }.start()
    }
    
    private fun selectApp(app: App): Boolean {
        if (!selectedApps.contains(app)) {
            selectedApps.add(app)
            return true
        }
        return false
    }
    
    private fun deselectApp(app: App): Boolean = selectedApps.remove(app)
    
    fun toggleAppSelected(app: App): Boolean {
        return if (isAppSelected(app)) {
            deselectApp(app)
        } else {
            val actualState = getRequestState(false)
            if (actualState != STATE_NORMAL) {
                builder?.context?.let {
                    builder?.callback?.onRequestLimited(
                        it, actualState, requestsLeft, timeLeft, false)
                }
                false
            } else {
                selectApp(app)
            }
        }
    }
    
    fun isAppSelected(app: App): Boolean = selectedApps.contains(app)
    
    fun selectAllApps(): Boolean {
        if (apps.isEmpty()) return false
        var changed = false
        val actualState = getRequestState(false)
        if (actualState != STATE_NORMAL) {
            builder?.context?.let {
                builder?.callback?.onRequestLimited(it, actualState, requestsLeft, timeLeft, false)
            }
        } else {
            val notSelectedApps = apps.filterNot { selectedApps.contains(it) }
            for (app in notSelectedApps) {
                val newActualState = getRequestState(false)
                if (newActualState == STATE_NORMAL) {
                    changed = true
                    selectedApps.add(app)
                } else {
                    builder?.context?.let {
                        builder?.callback?.onRequestLimited(
                            it, newActualState, requestsLeft, timeLeft, false)
                    }
                    return changed
                }
            }
        }
        return changed
    }
    
    fun deselectAllApps(): Boolean {
        if (selectedApps.isEmpty()) return false
        selectedApps.clear()
        return true
    }
    
    @WorkerThread
    private fun postError(
        msg: String,
        baseError: Exception?,
        callback: SendRequestCallback?,
        uploading: Boolean = false
                         ) {
        BL.e("$msg -- Error: ${baseError?.message}")
        callback?.doOnError(msg, uploading)
    }
    
    fun send(sendRequestCallback: SendRequestCallback?) {
        var requestError = false
        
        if (apps.isEmpty()) {
            requestError = true
            postError("No apps were loaded from this device.", null, sendRequestCallback)
        } else if (!builder?.email.orEmpty().hasContent()) {
            requestError = true
            postError(
                "The recipient email for the request cannot be empty.", null, sendRequestCallback)
        } else if (selectedApps.size <= 0) {
            requestError = true
            builder?.context?.let { builder?.callback?.onRequestEmpty(it) }
            postError(
                "No apps have been selected for sending in the request.", null, sendRequestCallback)
        } else if (builder?.subject.orEmpty().isEmpty()) {
            builder?.subject = "Icon Request"
            requestError = false
        }
        
        if (requestError) {
            cleanFiles(true)
            return
        }
        
        val actualState = getRequestState(true)
        
        if (actualState != STATE_NORMAL) {
            builder?.context?.let {
                builder?.callback?.onRequestLimited(it, actualState, requestsLeft, timeLeft, true)
            }
            return
        }
        
        Thread {
            sendRequestCallback?.doWhenStarted()
            
            val host = builder?.apiHost.orEmpty()
            val apiKey = builder?.apiKey.orEmpty()
            val uploadToArctic = host.hasContent() && apiKey.hasContent()
            
            val emailZipFiles = ArrayList<File>()
            
            builder?.saveDir?.wipe()
            builder?.saveDir?.mkdirs()
            
            // Save app icons
            val correctList = ArrayList<Pair<String, App>>()
            val iconsNames = ArrayList<Pair<String, Int>>()
            
            for (app in selectedApps) {
                val icon = builder?.context?.let { app.getIcon(it) }
                icon ?: continue
                
                val iconName = app.name.safeDrawableName()
                var correctIconName = iconName
                
                val inList = iconsNames.find { it.first.equals(iconName, true) }
                if (inList != null) correctIconName += "_${inList.second}"
                
                val iconFile: File? = File(
                    builder?.saveDir,
                    if (uploadToArctic) "${app.pkg}.png" else "$correctIconName.png")
                
                try {
                    iconFile?.saveIcon(icon.toBitmap())
                    iconFile?.let { emailZipFiles.add(it) }
                    
                    val count =
                        (iconsNames.find { it.first.equals(iconName, true) }?.second ?: 0) + 1
                    try {
                        iconsNames.removeAt(
                            iconsNames.indexOfFirst { it.first.equals(iconName, true) }
                                           )
                    } catch (ignored: Exception) {
                    }
                    iconsNames += iconName to count
                    
                    correctList += correctIconName to app
                } catch (e: Exception) {
                    postError(
                        "Failed to save icon \'$correctIconName\' due to error: ${e.message}",
                        e, sendRequestCallback)
                    cleanFiles(true)
                    return@Thread
                }
            }
            
            // Create request files
            var xmlSb: StringBuilder? = null
            var amSb: StringBuilder? = null
            var trSb: StringBuilder? = null
            var jsonSb: StringBuilder? = null
            
            if (!uploadToArctic) {
                xmlSb = StringBuilder(
                    "<resources>\n\t<iconback img1=\"iconback\"/>\n\t<iconmask " +
                        "img1=\"iconmask\"/>\n\t<iconupon img1=\"iconupon\"/>\n\t" +
                        "<scale factor=\"1.0\"/>")
            }
            
            if (!uploadToArctic) {
                amSb = StringBuilder("<appmap>")
            }
            
            if (!uploadToArctic) {
                trSb = StringBuilder(
                    "<Theme version=\"1\">\n\t<Label value=\"${builder?.appName}\"/>\n\t" +
                        "<Wallpaper image=\"wallpaper_01\"/>\n\t<LockScreenWallpaper " +
                        "image=\"wallpaper_02\"/>\n\t<ThemePreview image=\"preview1\"/>\n\t" +
                        "<ThemePreviewWork image=\"preview1\"/>\n\t<ThemePreviewMenu " +
                        "image=\"preview1\"/>\n\t<DockMenuAppIcon selector=\"drawer\"/>")
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
                        "\t<item\n\t\tcomponent=\"ComponentInfo{${app.comp}}\"\n\t\tdrawable=\"$iconName\"/>")
                }
                
                if (amSb != null) {
                    amSb.append("\n\n")
                    amSb.append("\t<!-- ${app.name} -->\n")
                    val rightCode = app.comp.split(
                        "/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    amSb.append(
                        "\t<item\n\t\tclass=\"$rightCode\"\n\t\tname=\"$iconName\"/>")
                }
                
                if (trSb != null) {
                    trSb.append("\n\n")
                    trSb.append("\t<!-- ${app.name} -->\n")
                    trSb.append(
                        "\t<AppIcon\n\t\tname=\"${app.comp}\"\n\t\timage=\"$iconName\"/>")
                }
                
                if (jsonSb != null) {
                    if (!isFirst) jsonSb.append(",")
                    jsonSb.append("\n\t\t{\n")
                        .append("\t\t\t\"name\": \"${app.name}\",\n")
                        .append("\t\t\t\"pkg\": \"${app.pkg}\",\n")
                        .append("\t\t\t\"componentInfo\": \"${app.comp}\",\n")
                        .append("\t\t\t\"drawable\": \"$iconName\"")
                        .append("\n\t\t}")
                }
                
                if (isFirst) isFirst = false
            }
            
            val date = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            
            if (xmlSb != null) {
                xmlSb.append("\n\n</resources>")
                val appfilter = File(builder?.saveDir, "appfilter_$date.xml")
                try {
                    appfilter.saveAll(xmlSb.toString())
                    emailZipFiles.add(appfilter)
                } catch (e: Exception) {
                    BL.e("Error", e)
                    postError(
                        "Failed to save appfilter.xml file: ${e.message}", e,
                        sendRequestCallback)
                    cleanFiles(true)
                    return@Thread
                }
            }
            
            if (amSb != null) {
                amSb.append("\n\n</appmap>")
                val appmap = File(builder?.saveDir, "appmap_$date.xml")
                try {
                    appmap.saveAll(amSb.toString())
                    emailZipFiles.add(appmap)
                } catch (e: Exception) {
                    BL.e("Error", e)
                    postError(
                        "Failed to save appmap.xml file: ${e.message}", e, sendRequestCallback)
                    cleanFiles(true)
                    return@Thread
                }
            }
            
            if (trSb != null) {
                trSb.append("\n\n</Theme>")
                val themeRes = File(builder?.saveDir, "theme_resources_$date.xml")
                try {
                    themeRes.saveAll(trSb.toString())
                    emailZipFiles.add(themeRes)
                } catch (e: Exception) {
                    BL.e("Error", e)
                    postError(
                        "Failed to save theme_resources.xml file: ${e.message}", e,
                        sendRequestCallback)
                    cleanFiles(true)
                    return@Thread
                }
            }
            
            if (jsonSb != null) {
                jsonSb.append("\n\t]\n}")
            }
            
            if (emailZipFiles.isEmpty()) {
                postError(
                    "There are no files to put into the ZIP archive.", null,
                    sendRequestCallback)
                cleanFiles(true)
                return@Thread
            }
            
            if (uploadToArctic) {
                try {
                    val zipFile = buildZip(
                        date, emailZipFiles.jfilter { it.name.endsWith("png", true) })
                    cleanFiles()
                    if (zipFile != null) {
                        val rHost = if (host.endsWith("/")) host else "$host/"
                        
                        var fileType = URLConnection.guessContentTypeFromName(zipFile.name)
                        if (fileType == null || fileType.trim().isEmpty())
                            fileType = "application/octet-stream"
                        
                        Fuel.upload(
                            rHost + "v1/request",
                            parameters = listOf("apps" to jsonSb?.toString().orEmpty()))
                            // .add(FileDataPart(zipFile, "archive", contentType = fileType))
                            .header(
                                "TokenID" to apiKey,
                                "Accept" to "application/json",
                                "User-Agent" to "afollestad/icon-request"
                                   )
                            .dataParts { _, _ ->
                                listOf(DataPart(zipFile, "archive", fileType))
                            }
                            .response { _, response, result ->
                                val success = response.statusCode in 200..299
                                
                                if (success) {
                                    BL.d("Request sent!")
                                    val amount = requestsLeft - selectedApps.size
                                    saveRequestsLeft(if (amount < 0) 0 else amount)
                                    
                                    if (requestsLeft == 0) saveRequestMoment()
                                    
                                    cleanFiles(true)
                                    sendRequestCallback?.doWhenReady(true)
                                } else {
                                    val responseMsg = try {
                                        val body = String(response.data)
                                        val error = JSONObject(body).string("error")
                                        if (error.hasContent()) error else result.toString()
                                    } catch (e: Exception) {
                                        result.toString()
                                    }
                                    BL.e("Server error: $responseMsg")
                                    BL.e("Server response: $response")
                                    
                                    cleanFiles(true)
                                    postError(responseMsg, null, sendRequestCallback, true)
                                }
                            }
                    } else {
                        cleanFiles(true)
                        postError("Zip file could not be found", null, sendRequestCallback)
                    }
                } catch (e: Exception) {
                    try {
                        val errors = StringWriter()
                        e.printStackTrace(PrintWriter(errors))
                        BL.e(errors.toString())
                    } catch (ignored: Exception) {
                    }
                    postError(
                        "Failed to send icons to the backend: ${e.message}", null,
                        sendRequestCallback, true)
                }
            } else {
                val zipFile = buildZip(date, emailZipFiles)
                if (zipFile != null) {
                    sendRequestViaEmail(zipFile, sendRequestCallback)
                } else {
                    cleanFiles(true)
                    postError("Zip file could not be found", null, sendRequestCallback)
                }
            }
        }.start()
    }
    
    private fun buildZip(date: String, filesToZip: ArrayList<File>): File? {
        // Zip everything into an archive
        val zipFile = File(builder?.saveDir, "IconRequest-$date.zip")
        return try {
            zipFile.zip(filesToZip)
            zipFile
        } catch (e: Exception) {
            BL.e(e.message)
            postError("Failed to create the request ZIP file: " + e.message, e, null)
            null
        }
    }
    
    private fun sendRequestViaEmail(zipFile: File, sendRequestCallback: SendRequestCallback?) {
        try {
            cleanFiles()
            
            val zipUri = builder?.context?.let { zipFile.getUri(it) }
            val emailIntent = Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_EMAIL, arrayOf(builder?.email.orEmpty()))
                .putExtra(Intent.EXTRA_SUBJECT, builder?.subject)
                .putExtra(
                    Intent.EXTRA_TEXT,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        Html.fromHtml(body, Html.FROM_HTML_MODE_LEGACY)
                    else Html.fromHtml(body))
                .putExtra(Intent.EXTRA_STREAM, zipUri)
                .setType("application/zip")
            
            val amount = requestsLeft - selectedApps.size
            saveRequestsLeft(if (amount < 0) 0 else amount)
            
            if (requestsLeft == 0)
                saveRequestMoment()
            
            sendRequestCallback?.doWhenReady(false)
            
            (builder?.context as? Activity)?.startActivityForResult(
                Intent.createChooser(
                    emailIntent, builder?.context?.getString(R.string.send_using)),
                INTENT_CODE) ?: {
                builder?.context?.startActivity(
                    Intent.createChooser(
                        emailIntent, builder?.context?.getString(R.string.send_using)))
            }()
        } catch (e: Exception) {
            BL.e("Error", e)
            postError("Error: ${e.message}", e, sendRequestCallback)
        }
    }
    
    private fun cleanFiles(everything: Boolean = false) {
        try {
            val files = builder?.saveDir?.listFiles()
            files?.forEach {
                if (!it.isDirectory &&
                    (everything || (it.name.endsWith(".png") || it.name.endsWith(".xml")))) {
                    it.delete()
                }
            }
        } catch (e: Exception) {
            BL.e(e.message)
        }
    }
    
    @State
    private fun getRequestState(toSend: Boolean): Int {
        val requestLimit = builder?.maxCount ?: -1
        val timeLimit = builder?.timeLimit ?: -1
        if (requestLimit <= 0 || timeLimit <= 0) return STATE_NORMAL
        val extra = if (toSend) 0 else 1
        if ((selectedApps.size + extra) > requestsLeft) {
            return when {
                timeLeft > 0 -> STATE_TIME_LIMITED
                requestsLeft == 0 -> {
                    saveRequestsLeft(-1)
                    STATE_NORMAL
                }
                else -> STATE_COUNT_LIMITED
            }
        } else {
            if (timeLeft > 0) {
                return STATE_TIME_LIMITED
            }
        }
        return STATE_NORMAL
    }
    
    @SuppressLint("SimpleDateFormat")
    private fun saveRequestMoment() {
        builder?.prefs?.edit()?.putLong(KEY_SAVED_TIME_MILLIS, System.currentTimeMillis())?.apply()
    }
    
    private fun saveRequestsLeft(requestsLeft: Int) {
        builder?.prefs?.edit()?.putInt(MAX_APPS, requestsLeft)?.apply()
    }
    
    companion object {
        const val STATE_NORMAL = 0
        const val STATE_COUNT_LIMITED = 1
        const val STATE_TIME_LIMITED = 2
        
        const val INTENT_CODE = 99
        
        private const val KEY_SAVED_TIME_MILLIS = "saved_time_millis"
        private const val MAX_APPS = "apps_to_request"
        
        private var request: IconRequest? = null
        
        fun start(context: Context): Builder = Builder(context)
        
        fun get(): IconRequest? = request
        
        fun saveInstanceState(outState: Bundle?) {
            if (request == null || outState == null) return
            outState.putParcelable("butler_builder", request?.builder)
            outState.putParcelableArrayList("apps", request?.apps)
            outState.putParcelableArrayList("selected_apps", request?.selectedApps)
        }
        
        fun restoreInstanceState(context: Context, inState: Bundle?): IconRequest? {
            if (inState == null || !inState.containsKey("butler_builder"))
                return null
            request = IconRequest()
            request?.builder = inState.getParcelable("butler_builder")
            if (request?.builder != null) {
                request?.builder?.context = context
            }
            
            if (request?.apps == null)
                request?.apps?.clear()
            if (request?.selectedApps == null)
                request?.selectedApps?.clear()
            
            if (inState.containsKey("apps")) {
                request?.apps?.clear()
                request?.apps?.addAll(inState.getParcelableArrayList("apps") ?: ArrayList())
            }
            if (inState.containsKey("selected_apps")) {
                request?.selectedApps?.clear()
                request?.selectedApps?.addAll(
                    inState.getParcelableArrayList("selected_apps") ?: ArrayList())
            }
            return request
        }
        
        fun cleanup() {
            if (request == null) return
            if (request?.builder != null) {
                request?.builder?.context = null
                request?.builder = null
            }
            if (request?.apps != null) {
                request?.apps?.clear()
            }
            if (request?.selectedApps != null) {
                request?.selectedApps?.clear()
            }
            request = null
        }
    }
}
