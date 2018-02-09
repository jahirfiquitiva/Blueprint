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
import android.content.pm.PackageInfo
import android.content.res.XmlResourceParser
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import android.support.annotation.IntDef
import android.support.annotation.IntRange
import android.support.annotation.WorkerThread
import android.support.annotation.XmlRes
import android.text.Html
import android.util.Log
import ca.allanwang.kau.utils.toBitmap
import com.afollestad.bridge.Bridge
import com.afollestad.bridge.Bridge.post
import com.afollestad.bridge.MultipartForm
import jahirfiquitiva.libs.blueprint.BuildConfig
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.BPLog
import jahirfiquitiva.libs.blueprint.quest.events.EventState
import jahirfiquitiva.libs.blueprint.quest.events.OnRequestProgress
import jahirfiquitiva.libs.blueprint.quest.events.RequestsCallback
import jahirfiquitiva.libs.blueprint.quest.prm.RemoteValidator
import jahirfiquitiva.libs.blueprint.quest.utils.TimeUtils
import jahirfiquitiva.libs.blueprint.quest.utils.getInstalledApps
import jahirfiquitiva.libs.blueprint.quest.utils.safeDrawableName
import jahirfiquitiva.libs.blueprint.quest.utils.saveAll
import jahirfiquitiva.libs.blueprint.quest.utils.saveIcon
import jahirfiquitiva.libs.blueprint.quest.utils.wipe
import jahirfiquitiva.libs.blueprint.quest.utils.zip
import jahirfiquitiva.libs.kauextensions.extensions.getAppName
import jahirfiquitiva.libs.kauextensions.extensions.getUri
import jahirfiquitiva.libs.kauextensions.extensions.hasContent
import jahirfiquitiva.libs.kauextensions.extensions.readBoolean
import jahirfiquitiva.libs.kauextensions.extensions.readEnum
import jahirfiquitiva.libs.kauextensions.extensions.writeBoolean
import jahirfiquitiva.libs.kauextensions.extensions.writeEnum
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
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
    private val state = STATE_NORMAL
    
    private var builder: Builder? = null
    
    val apps: ArrayList<App> = ArrayList()
    
    val selectedApps: ArrayList<App> = ArrayList()
    
    private val invalidDrawables: StringBuilder? = null
    
    val maxSelectable: Int
        get() = builder?.maxCount ?: -1
    
    private val body: String
        get() {
            val sb = StringBuilder()
            if (builder?.header.orEmpty().hasContent()) {
                sb.append(builder?.header.orEmpty().replace("\n", "<br/>"))
                sb.append("<br/><br/>")
            }
            
            for (i in selectedApps.indices) {
                if (i > 0) sb.append("<br/><br/>")
                val app = selectedApps[i]
                sb.append("Name: <b>${app.name}</b><br/>")
                sb.append("Code: <b>${app.code}</b><br/>")
                sb.append("Link: https://play.google.com/store/apps/details?id=${app.pckg}<br/>")
            }
            
            if (builder?.includeDeviceInfo == true) {
                sb.append("<br/><br/><br/>OS Version: ").append(System.getProperty("os.version"))
                        .append("(").append(Build.VERSION.INCREMENTAL).append(")")
                sb.append("<br/>OS API Level: ").append(Build.VERSION.SDK_INT)
                sb.append("<br/>Device: ").append(Build.MODEL)
                sb.append("<br/>Manufacturer: ").append(Build.MANUFACTURER)
                sb.append("<br/>Model (and Product): ").append(Build.DEVICE).append(" (")
                        .append(Build.PRODUCT).append(")")
                val appInfo: PackageInfo?
                try {
                    appInfo = builder?.context?.packageManager?.getPackageInfo(
                            builder?.context?.packageName, 0)
                    sb.append("<br/>App Version Name: ").append(appInfo?.versionName ?: -1)
                    sb.append("<br/>App Version Code: ").append(appInfo?.versionCode ?: -1)
                } catch (e: Exception) {
                    sb.append("<br/>There was an error getting application version.")
                }
                
                if (builder?.footer != null) {
                    sb.append("<br/>")
                    sb.append(builder?.footer?.replace("\n", "<br/>"))
                }
            } else {
                sb.append("<br/><br/>")
                sb.append(builder?.footer?.replace("\n", "<br/>"))
            }
            return sb.toString()
        }
    
    val isNotEmpty: Boolean
        get() = apps.isNotEmpty()
    
    val isLoading: Boolean
        get() = builder?.isLoading ?: false
    
    private val millisToFinish: Long
        @SuppressLint("SimpleDateFormat")
        get() {
            val savedTime: Int = (builder?.prefs?.getLong(KEY_SAVED_TIME_MILLIS, -1) ?: -1).toInt()
            if (savedTime == -1) return -1
            val elapsedTime = TimeUtils.currentTimeInMillis - savedTime
            val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm:ss")
            BPLog.d {
                "Timer: [Last request was on: " + sdf.format(savedTime) + "] - [Right" +
                        " now is: " + sdf.format(Date(TimeUtils.currentTimeInMillis)) + "] - " +
                        "[Time Left: ~" +
                        ((builder?.timeLimit ?: 0) - elapsedTime) / 1000 + " secs.]"
            }
            return (builder?.timeLimit ?: 0) - elapsedTime - 500
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
    
    @IntDef(STATE_NORMAL.toLong(), STATE_LIMITED.toLong(), STATE_TIME_LIMITED.toLong())
    @Retention(AnnotationRetention.SOURCE)
    annotation class State
    
    class Builder : Parcelable {
        
        @Transient var context: Context? = null
        var saveDir: File? = null
        var filterId = -1
        var appName: String = "Default App"
        var email: String = "someone@mail.co"
        var subject: String = "Icon Request"
        var header: String = "These apps aren't themed. Thanks in advance"
        var footer: String = "Lib Version: ${BuildConfig.LIB_VERSION}"
        var apiHost: String = "http://arcticmanager.com/"
        var apiKey: String? = null
        var maxCount = 0
        var timeLimit: Long = -1
        var isLoading = false
        protected var hasMaxCount = false
        var noneSelectsAll = false
        var includeDeviceInfo = true
        var comments = true
        var generateAppFilterXml = true
        var generateAppMapXml = true
        var generateThemeResourcesXml = true
        var generateAppFilterJson = false
        private var errorOnInvalidAppFilterDrawable = true
        var debugMode = false
        var prefs: SharedPreferences? = null
        var callback: RequestsCallback? = null
        var loadingState: EventState? = EventState.DISABLED
        var loadedState: EventState? = EventState.STICKIED
        var selectionState: EventState? = EventState.DISABLED
        var requestState: EventState? = EventState.DISABLED
        
        constructor()
        
        constructor(context: Context) {
            this.context = context
            saveDir = File(Environment.getExternalStorageDirectory(), "IconRequest")
        }
        
        fun filterXmlId(@XmlRes resId: Int): Builder {
            filterId = resId
            return this
        }
        
        fun filterOff(): Builder {
            filterId = -1
            return this
        }
        
        fun saveDir(file: File): Builder {
            saveDir = file
            return this
        }
        
        fun toEmail(email: String): Builder {
            this.email = email
            return this
        }
        
        fun withAppName(appName: String, vararg args: Any): Builder {
            this.appName = if (args.isNotEmpty()) String.format(appName, args) else appName
            return this
        }
        
        fun withSubject(subject: String, vararg args: Any): Builder {
            this.subject = if (args.isNotEmpty()) String.format(subject, args) else subject
            return this
        }
        
        fun withHeader(header: String, vararg args: Any): Builder {
            this.header = if (args.isNotEmpty()) String.format(header, args) else header
            return this
        }
        
        fun withFooter(footer: String, vararg args: Any): Builder {
            this.footer = if (args.isNotEmpty()) String.format(footer, args) else footer
            return this
        }
        
        fun withAPIHost(host: String): Builder {
            if (host.hasContent()) apiHost = host
            return generateAppFilterJson(apiHost.hasContent())
        }
        
        fun withAPIKey(key: String?): Builder {
            key?.let {
                if (it.hasContent()) apiKey = it
                return generateAppFilterJson(it.hasContent())
            } ?: return generateAppFilterJson(false)
        }
        
        fun maxSelectionCount(@IntRange(from = 0) count: Int): Builder {
            maxCount = count
            hasMaxCount = maxCount > 0
            return this
        }
        
        fun withTimeLimit(minutes: Int, prefs: SharedPreferences?): Builder {
            timeLimit = TimeUnit.MINUTES.toMillis(minutes.toLong())
            this.prefs = prefs ?:
                    context?.getSharedPreferences("RequestPrefs", Context.MODE_PRIVATE)
            return this
        }
        
        fun withComments(b: Boolean): Builder {
            comments = b
            return this
        }
        
        fun noSelectionSelectsAll(b: Boolean): Builder {
            noneSelectsAll = b
            return this
        }
        
        fun includeDeviceInfo(include: Boolean): Builder {
            includeDeviceInfo = include
            return this
        }
        
        fun generateAppFilterXml(generate: Boolean): Builder {
            generateAppFilterXml = generate
            return this
        }
        
        fun generateAppMapXml(generate: Boolean): Builder {
            generateAppMapXml = generate
            return this
        }
        
        fun generateThemeResourcesXml(generate: Boolean): Builder {
            generateThemeResourcesXml = generate
            return this
        }
        
        private fun generateAppFilterJson(generate: Boolean): Builder {
            generateAppFilterJson = generate
            return this
        }
        
        fun errorOnInvalidFilterDrawable(error: Boolean): Builder {
            errorOnInvalidAppFilterDrawable = error
            return this
        }
        
        fun debugMode(debug: Boolean): Builder {
            debugMode = debug
            return this
        }
        
        fun loadingEvents(state: EventState): Builder {
            loadingState = state
            return this
        }
        
        fun loadedEvents(state: EventState): Builder {
            loadedState = state
            return this
        }
        
        fun selectionEvents(state: EventState): Builder {
            selectionState = state
            return this
        }
        
        fun requestEvents(state: EventState): Builder {
            requestState = state
            return this
        }
        
        fun setCallback(callback: RequestsCallback?): Builder {
            this.callback = callback
            return this
        }
        
        fun build(): IconRequest = IconRequest(this)
        
        override fun describeContents(): Int = 0
        
        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeSerializable(saveDir)
            dest.writeInt(filterId)
            dest.writeString(appName)
            dest.writeString(email)
            dest.writeString(subject)
            dest.writeString(header)
            dest.writeString(footer)
            dest.writeString(apiKey)
            dest.writeInt(maxCount)
            dest.writeLong(timeLimit)
            dest.writeBoolean(isLoading)
            dest.writeBoolean(hasMaxCount)
            dest.writeBoolean(noneSelectsAll)
            dest.writeBoolean(includeDeviceInfo)
            dest.writeBoolean(comments)
            dest.writeBoolean(generateAppFilterXml)
            dest.writeBoolean(generateAppMapXml)
            dest.writeBoolean(generateThemeResourcesXml)
            dest.writeBoolean(generateAppFilterJson)
            dest.writeBoolean(errorOnInvalidAppFilterDrawable)
            dest.writeBoolean(debugMode)
            dest.writeEnum(loadingState)
            dest.writeEnum(loadedState)
            dest.writeEnum(selectionState)
            dest.writeEnum(requestState)
        }
        
        protected constructor(parcel: Parcel) {
            saveDir = parcel.readSerializable() as File
            filterId = parcel.readInt()
            appName = parcel.readString()
            email = parcel.readString()
            subject = parcel.readString()
            header = parcel.readString()
            footer = parcel.readString()
            apiKey = parcel.readString()
            maxCount = parcel.readInt()
            timeLimit = parcel.readLong()
            isLoading = parcel.readBoolean()
            hasMaxCount = parcel.readBoolean()
            noneSelectsAll = parcel.readBoolean()
            includeDeviceInfo = parcel.readBoolean()
            comments = parcel.readBoolean()
            generateAppFilterXml = parcel.readBoolean()
            generateAppMapXml = parcel.readBoolean()
            generateThemeResourcesXml = parcel.readBoolean()
            generateAppFilterJson = parcel.readBoolean()
            errorOnInvalidAppFilterDrawable = parcel.readBoolean()
            debugMode = parcel.readBoolean()
            loadingState = parcel.readEnum<EventState>()
            loadedState = parcel.readEnum<EventState>()
            selectionState = parcel.readEnum<EventState>()
            requestState = parcel.readEnum<EventState>()
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
        if (builder?.filterId == -1) { //TODO add this
            return defined
        }
        var parser: XmlResourceParser? = null
        try {
            parser = builder?.context?.resources?.getXml(builder?.filterId ?: 0)
            var eventType = parser?.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val appCode: String
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tagName = parser?.name
                        if (tagName == "item") {
                            try {
                                // Read package and activity name
                                val component =
                                        parser?.getAttributeValue(null, "component").orEmpty()
                                if (component.hasContent()) {
                                    if (!component.startsWith(":")) {
                                        appCode = component.substring(14, component.length - 1)
                                        //wrapped in ComponentInfo{[Component]} TODO add checker?
                                        //TODO check for valid drawable
                                        // Add new info to our ArrayList and reset the object.
                                        defined.add(appCode)
                                    }
                                }
                            } catch (e: Exception) {
                                BPLog.d { "Error adding parsed appfilter item! Due to Exception: ${e.message}" }
                            }
                        }
                    }
                }
                eventType = parser?.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            parser?.close()
        }
        return defined
    }
    
    fun loadApps(onProgress: (progress: Int) -> Unit = {}) {
        if (builder?.isLoading == true) return
        builder?.isLoading = true
        if (apps.isNotEmpty()) {
            builder?.callback?.onAppsLoaded(apps)
            builder?.isLoading = false
            return
        }
        Thread {
            BPLog.d { "Loading unthemed installed apps..." }
            val filter = loadFilterApps() ?: return@Thread
            apps.clear()
            apps.addAll(builder?.context?.getInstalledApps(filter, onProgress).orEmpty())
            builder?.isLoading = false
            builder?.callback?.onAppsLoaded(apps)
        }.start()
    }
    
    fun loadHighResIcons() {
        if (apps.isEmpty()) {
            BPLog.d { "High res load failed; app list is empty" }
            return
        }
        Thread {
            BPLog.d { "Getting high res icons for all apps..." }
            apps.let {
                for (app in it) {
                    builder?.context?.let { app.getHighResIcon(it) }
                }
            }
            BPLog.d { "High res icon retrieval finished..." }
        }.start()
    }
    
    fun selectApp(app: App): Boolean {
        if (!selectedApps.contains(app)) {
            selectedApps.add(app)
            return true
        }
        return false
    }
    
    fun unselectApp(app: App): Boolean = selectedApps.remove(app)
    
    fun toggleAppSelected(app: App): Boolean {
        return if (isAppSelected(app)) {
            unselectApp(app)
        } else {
            val state = getRequestState(false)
            if (state != STATE_NORMAL) {
                builder?.context?.let {
                    builder?.callback?.onRequestLimited(it, state, requestsLeft, millisToFinish)
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
        
        apps.filterNot { selectedApps.contains(it) }.forEach {
            if (getRequestState(false) == STATE_NORMAL) {
                changed = true
                selectedApps.add(it)
            }
        }
        
        if (getRequestState(false) != STATE_NORMAL)
            builder?.context?.let {
                builder?.callback?.onRequestLimited(it, state, requestsLeft, millisToFinish)
            }
        return changed
    }
    
    fun unselectAllApps(): Boolean {
        if (selectedApps.isEmpty()) return false
        selectedApps.clear()
        return true
    }
    
    @WorkerThread
    private fun postError(msg: String, baseError: Exception?) {
        BPLog.e { "$msg -- Error: ${baseError?.message}" }
    }
    
    fun send(onRequestProgress: OnRequestProgress?) {
        BPLog.d { "Preparing your request to send..." }
        
        var requestError = false
        
        if (apps.isEmpty()) {
            requestError = true
            postError("No apps were loaded from this device.", null)
        } else if (!builder?.email.orEmpty().hasContent()) {
            requestError = true
            postError("The recipient email for the request cannot be empty.", null)
        } else if (selectedApps.size <= 0) {
            if (builder?.noneSelectsAll == true) {
                selectedApps.addAll(apps)
                requestError = false
            } else {
                requestError = true
                builder?.context?.let { builder?.callback?.onRequestEmpty(it) }
                postError("No apps have been selected for sending in the request.", null)
            }
        } else if (builder?.subject.orEmpty().isEmpty()) {
            builder?.subject = "Icon Request"
            requestError = false
        }
        
        if (requestError) {
            onRequestProgress?.doOnError()
            return
        }
        
        @State
        val currentState = getRequestState(true)
        
        if (currentState == STATE_NORMAL) {
            Thread {
                onRequestProgress?.doWhenStarted()
                
                val filesToZip = ArrayList<File>()
                
                builder?.saveDir?.wipe()
                builder?.saveDir?.mkdirs()
                
                // Save app icons
                BPLog.d { "Saving icons..." }
                val appNames = ArrayList<String>()
                var prevName = ""
                var count = 1
                for (app in selectedApps) {
                    var iconName = app.name.safeDrawableName()
                    if (prevName.equals(iconName, ignoreCase = true)) {
                        iconName += "_" + count.toString()
                        count += 1
                    } else {
                        count = 1
                    }
                    val icon = builder?.context?.let {
                        app.getHighResIcon(it)?.toBitmap()
                    }
                    icon ?: continue
                    
                    val file = File(builder?.saveDir, "$iconName.png")
                    appNames.add(iconName)
                    filesToZip.add(file)
                    try {
                        file.saveIcon(icon)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        postError("Failed to save icon \'$iconName\' due to error: ${e.message}", e)
                        onRequestProgress?.doOnError()
                        return@Thread
                    }
                    
                    prevName = iconName
                }
                
                // Create request files
                BPLog.d { "Creating request files..." }
                var xmlSb: StringBuilder? = null
                var amSb: StringBuilder? = null
                var trSb: StringBuilder? = null
                var jsonSb: StringBuilder? = null
                
                if (builder?.generateAppFilterXml == true) {
                    xmlSb = StringBuilder(
                            "<resources>\n" +
                                    "\t<iconback img1=\"iconback\"/>\n" +
                                    "\t<iconmask img1=\"iconmask\"/>\n" +
                                    "\t<iconupon img1=\"iconupon\"/>\n" +
                                    "\t<scale factor=\"1.0\"/>")
                }
                
                if (builder?.generateAppMapXml == true) {
                    amSb = StringBuilder("<appmap>")
                }
                
                if (builder?.generateThemeResourcesXml == true) {
                    trSb = StringBuilder(
                            "<Theme version=\"1\">\n" +
                                    "\t<Label value=\"" + builder?.appName +
                                    "\"/>\n" +
                                    "\t<Wallpaper image=\"wallpaper_01\"/>\n" +
                                    "\t<LockScreenWallpaper " +
                                    "image=\"wallpaper_02\"/>\n" +
                                    "\t<ThemePreview image=\"preview1\"/>\n" +
                                    "\t<ThemePreviewWork " +
                                    "image=\"preview1\"/>\n" +
                                    "\t<ThemePreviewMenu " +
                                    "image=\"preview1\"/>\n" +
                                    "\t<DockMenuAppIcon " +
                                    "selector=\"drawer\"/>")
                }
                
                if (builder?.generateAppFilterJson == true) {
                    jsonSb = StringBuilder("{\n\t\"components\": [")
                }
                
                var n = 1
                appNames.clear()
                for ((index, app) in selectedApps.withIndex()) {
                    val name = app.name
                    var iconName = name
                    if (appNames.contains(iconName)) {
                        iconName += n.toString()
                        n += 1
                    }
                    val drawableName = iconName.safeDrawableName()
                    if (xmlSb != null) {
                        xmlSb.append("\n\n")
                        if (builder?.comments == true) {
                            xmlSb.append("\t<!-- ")
                                    .append(name)
                                    .append(" -->\n")
                        }
                        xmlSb.append(
                                "\t<item\n\t\tcomponent=\"ComponentInfo{${app.code}}\"\n\t\tdrawable=\"$drawableName\"/>")
                    }
                    if (amSb != null) {
                        amSb.append("\n\n")
                        if (builder?.comments == true) {
                            amSb.append("\t<!-- ")
                                    .append(name)
                                    .append(" -->\n")
                        }
                        val rightCode = app.code.split(
                                "/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                        amSb.append(
                                "\t<item\n\t\tclass=\"$rightCode\"\n\t\tname=\"$drawableName\"/>")
                    }
                    if (trSb != null) {
                        trSb.append("\n\n")
                        if (builder?.comments == true) {
                            trSb.append("\t<!-- ")
                                    .append(name)
                                    .append(" -->\n")
                        }
                        trSb.append(
                                "\t<AppIcon\n\t\tname=\"${app.code}\"\n\t\timage=\"$drawableName\"/>")
                    }
                    if (jsonSb != null) {
                        if (index > 0) jsonSb.append(",")
                        jsonSb.append("\n\t\t{\n")
                                .append("\t\t\t\"name\": \"$name\",\n")
                                .append("\t\t\t\"pkg\": \"${app.pckg}\",\n")
                                .append("\t\t\t\"componentInfo\": \"${app.code}\",\n")
                                .append("\t\t\t\"drawable\": \"$drawableName\"")
                                .append("\t\t}")
                    }
                    appNames.add(iconName)
                }
                
                val date = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                
                if (xmlSb != null) {
                    xmlSb.append("\n\n</resources>")
                    val newAppFilter = File(builder?.saveDir, "appfilter_$date.xml")
                    filesToZip.add(newAppFilter)
                    try {
                        newAppFilter.saveAll(xmlSb.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        postError(
                                "Failed to write your request appfilter.xml file: ${e.message}", e)
                        onRequestProgress?.doOnError()
                        return@Thread
                    }
                    
                }
                
                if (amSb != null) {
                    amSb.append("\n\n</appmap>")
                    val newAppFilter = File(builder?.saveDir, "appmap_$date.xml")
                    filesToZip.add(newAppFilter)
                    try {
                        newAppFilter.saveAll(amSb.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        postError("Failed to write your request appmap.xml file: ${e.message}", e)
                        onRequestProgress?.doOnError()
                        return@Thread
                    }
                    
                }
                if (trSb != null) {
                    trSb.append("\n\n</Theme>")
                    val newAppFilter = File(builder?.saveDir, "theme_resources_$date.xml")
                    filesToZip.add(newAppFilter)
                    try {
                        newAppFilter.saveAll(trSb.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        postError(
                                "Failed to write your request theme_resources.xml file: ${e.message}",
                                e)
                        onRequestProgress?.doOnError()
                        return@Thread
                    }
                    
                }
                
                if (jsonSb != null) {
                    jsonSb.append("\n\t]\n}")
                    val newAppFilter = File(builder?.saveDir, "appfilter_$date.json")
                    filesToZip.add(newAppFilter)
                    try {
                        newAppFilter.saveAll(jsonSb.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        postError(
                                "Failed to write your request appfilter.json file: ${e.message}", e)
                        onRequestProgress?.doOnError()
                        return@Thread
                    }
                }
                
                if (filesToZip.size == 0) {
                    postError("There are no files to put into the ZIP archive.", null)
                    onRequestProgress?.doOnError()
                    return@Thread
                }
                
                val host = builder?.apiHost.orEmpty()
                val apiKey = builder?.apiKey.orEmpty()
                
                if (host.hasContent() && apiKey.hasContent()) {
                    Bridge.config()
                            .host(host)
                            .defaultHeader("TokenID", apiKey)
                            .defaultHeader("Accept", "application/json")
                            .defaultHeader("User-Agent", "afollestad/icon-request")
                            .validators(RemoteValidator())
                    try {
                        val zipFile = buildZip(
                                date,
                                ArrayList(filesToZip.filter { it.name.endsWith("png", true) }))
                        if (zipFile != null) {
                            val form = MultipartForm()
                            form.add("archive", zipFile)
                            form.add("apps", JSONObject(jsonSb?.toString().orEmpty()).toString())
                            post("/v1/request").throwIfNotSuccess().body(form).request()
                            BPLog.d { "Request uploaded to the server!" }
                            
                            val amount = requestsLeft - selectedApps.size
                            BPLog.d { "Request: Allowing $amount more requests." }
                            saveRequestsLeft(if (amount < 0) 0 else amount)
                            
                            if (requestsLeft == 0) saveRequestMoment()
                            
                            cleanFiles(true)
                            onRequestProgress?.doWhenReady(true)
                        } else {
                            onRequestProgress?.doOnError()
                        }
                    } catch (e: Exception) {
                        Log.e(
                                builder?.context?.getAppName() ?: "Blueprint",
                                "Failed to send icons to the backend: ${e.message}")
                        try {
                            val errors = StringWriter()
                            e.printStackTrace(PrintWriter(errors))
                            Log.e(builder?.context?.getAppName() ?: "Blueprint", errors.toString())
                        } catch (ignored: Exception) {
                        }
                        val zipFile = buildZip(date, filesToZip)
                        if (zipFile != null) {
                            sendRequestViaEmail(zipFile, onRequestProgress)
                        } else {
                            onRequestProgress?.doOnError()
                        }
                    }
                } else {
                    val zipFile = buildZip(date, filesToZip)
                    if (zipFile != null) {
                        sendRequestViaEmail(zipFile, onRequestProgress)
                    } else {
                        onRequestProgress?.doOnError()
                    }
                }
            }.start()
        } else {
            builder?.context?.let {
                builder?.callback?.onRequestLimited(it, currentState, requestsLeft, millisToFinish)
            }
        }
    }
    
    private fun buildZip(date: String, filesToZip: ArrayList<File>): File? {
        // Zip everything into an archive
        BPLog.d { "Creating ZIP..." }
        val zipFile = File(builder?.saveDir, "IconRequest-$date.zip")
        return try {
            zipFile.zip(filesToZip)
            zipFile
        } catch (e: Exception) {
            BPLog.e { e.message }
            postError("Failed to create the request ZIP file: " + e.message, e)
            null
        }
    }
    
    private fun sendRequestViaEmail(zipFile: File, onRequestProgress: OnRequestProgress?) {
        try {
            cleanFiles()
            BPLog.d { "Launching intent!" }
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
            BPLog.d { "Request: Allowing $amount more requests." }
            saveRequestsLeft(if (amount < 0) 0 else amount)
            
            if (requestsLeft == 0)
                saveRequestMoment()
            
            onRequestProgress?.doWhenReady(false)
            
            (builder?.context as? Activity)?.startActivityForResult(
                    Intent.createChooser(
                            emailIntent, builder?.context?.getString(R.string.send_using)),
                    INTENT_CODE) ?: {
                builder?.context?.startActivity(
                        Intent.createChooser(
                                emailIntent, builder?.context?.getString(R.string.send_using)))
            }()
        } catch (e: Exception) {
            e.printStackTrace()
            onRequestProgress?.doOnError()
        }
    }
    
    private fun cleanFiles(everything: Boolean = false) {
        BPLog.d { "Cleaning up files..." }
        try {
            val files = builder?.saveDir?.listFiles()
            files?.forEach {
                if (!it.isDirectory &&
                        (everything || (it.name.endsWith(".png") || it.name.endsWith(".xml")))) {
                    it.delete()
                }
            }
        } catch (e: Exception) {
            BPLog.e { e.message }
        }
    }
    
    @State
    private fun getRequestState(toSend: Boolean): Int {
        val max = builder?.maxCount ?: -1
        val limit = builder?.timeLimit ?: -1
        if (max <= 0 || limit <= 0) return STATE_NORMAL
        
        val sum = if (toSend) 0 else 1
        BPLog.d { "Selected apps: ${selectedApps.size} - Requests left: $requestsLeft" }
        
        if (selectedApps.size + sum > requestsLeft) {
            if (millisToFinish > 0) {
                BPLog.d { "RequestState: Limited by time" }
                BPLog.d {
                    "RequestState: Millis to finish: $millisToFinish - " +
                            "Request limit: ${builder?.timeLimit}"
                }
                return STATE_TIME_LIMITED
            } else if (requestsLeft == 0) {
                saveRequestsLeft(-1)
                BPLog.d { "RequestState: Restarting requests left." }
                return STATE_NORMAL
            }
            BPLog.d { "RequestState: Limited by requests - Requests left: $requestsLeft" }
            return STATE_LIMITED
        } else {
            if (millisToFinish > 0) {
                BPLog.d { "RequestState: Limited by time" }
                BPLog.d {
                    "RequestState: Millis to finish: $millisToFinish - " +
                            "Request limit: ${builder?.timeLimit}"
                }
                return STATE_TIME_LIMITED
            }
        }
        return STATE_NORMAL
    }
    
    private fun saveRequestMoment() {
        builder?.prefs?.edit()?.putLong(KEY_SAVED_TIME_MILLIS, TimeUtils.currentTimeInMillis)
                ?.apply()
    }
    
    private fun saveRequestsLeft(requestsLeft: Int) {
        builder?.prefs?.edit()?.putInt(MAX_APPS, requestsLeft)?.apply()
    }
    
    companion object {
        const val STATE_NORMAL = 0
        const val STATE_LIMITED = 1
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
                request?.apps?.addAll(inState.getParcelableArrayList("apps"))
            }
            if (inState.containsKey("selected_apps")) {
                request?.selectedApps?.clear()
                request?.selectedApps?.addAll(inState.getParcelableArrayList("selected_apps"))
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