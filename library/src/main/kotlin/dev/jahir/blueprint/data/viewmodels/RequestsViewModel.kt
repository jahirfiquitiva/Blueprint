package dev.jahir.blueprint.data.viewmodels

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.blueprint.data.requests.RequestCallback
import dev.jahir.blueprint.data.requests.RequestState
import dev.jahir.blueprint.data.requests.RequestStateManager.getRequestState
import dev.jahir.blueprint.extensions.InstalledAppsComparator
import dev.jahir.blueprint.extensions.blueprintFormat
import dev.jahir.blueprint.extensions.clean
import dev.jahir.blueprint.extensions.drawableRes
import dev.jahir.blueprint.extensions.getLocalizedName
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.integer
import dev.jahir.frames.extensions.context.withXml
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.resources.nextOrNull
import dev.jahir.frames.extensions.utils.context
import dev.jahir.frames.extensions.utils.lazyMutableLiveData
import dev.jahir.frames.extensions.utils.tryToObserve
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser

class RequestsViewModel(application: Application) : AndroidViewModel(application) {

    var requestsCallback: RequestCallback? = null

    private val themedComponentsData: MutableLiveData<ArrayList<String>> by lazyMutableLiveData()
    private val themedComponents: ArrayList<String>
        get() = ArrayList(themedComponentsData.value.orEmpty())

    private val appsToRequestData: MutableLiveData<ArrayList<RequestApp>> by lazyMutableLiveData()
    internal val appsToRequest: ArrayList<RequestApp>
        get() = ArrayList(appsToRequestData.value.orEmpty())

    private val selectedAppsData: MutableLiveData<ArrayList<RequestApp>> by lazyMutableLiveData()
    internal val selectedApps: ArrayList<RequestApp>
        get() = ArrayList(selectedAppsData.value.orEmpty())

    private fun getComponentInAppFilter(
        parser: XmlPullParser?,
        debug: Boolean = false,
        onSuccess: ((String) -> Unit)? = null
    ) {
        parser ?: return
        try {
            val component = parser.getAttributeValue(null, "component").orEmpty()
            val drawable = parser.getAttributeValue(null, "drawable").orEmpty()

            if (component.hasContent() && !component.startsWith(":")) {
                val actualComponent = component.substring(14, component.length - 1)
                if (actualComponent.hasContent() && !actualComponent.startsWith("/")
                    && !actualComponent.endsWith("/")) {
                    if (debug) {
                        if (drawable.hasContent()) {
                            val res = context.drawableRes(drawable)
                            if (res == 0)
                                Log.w(
                                    context.getAppName(),
                                    "Drawable \"$drawable\" NOT found for component: \"$actualComponent\""
                                )
                        } else {
                            Log.w(
                                context.getAppName(),
                                "No drawable found for component: \"$actualComponent\""
                            )
                        }
                    }
                    onSuccess?.invoke(actualComponent)
                } else {
                    if (debug)
                        Log.w(
                            context.getAppName(),
                            "Found invalid component: \"$actualComponent\""
                        )
                }
            }
        } catch (e: Exception) {
            if (debug)
                Log.e(
                    context.getAppName(),
                    "Error adding parsed appfilter item! Due to Exception: ${e.message}"
                )
        }
    }

    private suspend fun loadThemedComponents(debug: Boolean = false): ArrayList<String> {
        if (themedComponents.isNotEmpty()) return themedComponents
        return withContext(IO) {
            val themedComponents = ArrayList<String>()
            val componentsCount = ArrayList<Pair<String, Int>>()
            context.withXml(R.xml.appfilter) { parser ->
                var eventType: Int? = parser.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.name == "item") {
                            getComponentInAppFilter(parser, debug) { component ->
                                themedComponents.add(component)
                                val count =
                                    (componentsCount.find { it.first == component }?.second
                                        ?: 0) + 1
                                try {
                                    componentsCount.removeAt(
                                        componentsCount.indexOfFirst { it.first == component })
                                } catch (ignored: Exception) {
                                }
                                componentsCount.add(Pair(component, count))
                            }
                        }
                    }
                    eventType = parser.nextOrNull()
                }
            }
            if (debug) {
                componentsCount.forEach {
                    if (it.second > 1)
                        Log.w(
                            context.getAppName(),
                            "Component \"${it.first}\" is duplicated ${it.second} time(s)"
                        )
                }
            }
            ArrayList(themedComponents.distinct())
        }
    }

    private suspend fun loadAppsToRequest(debug: Boolean = true): ArrayList<RequestApp> {
        if (appsToRequest.isNotEmpty()) return appsToRequest
        return withContext(IO) {
            val installedApps = ArrayList<RequestApp>()

            val packagesList = try {
                context.packageManager.queryIntentActivities(
                    Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER"),
                    PackageManager.GET_RESOLVED_FILTER
                )
            } catch (e: Exception) {
                ArrayList<ResolveInfo>()
            }

            var loaded = 0
            var filtered = 0

            if (packagesList.isNotEmpty()) {
                val list = ArrayList(packagesList.distinct())
                list.sortWith(InstalledAppsComparator(context.packageManager))

                for (ri in packagesList) {
                    val riPkg = ri.activityInfo.packageName
                    val component = riPkg + "/" + ri.activityInfo.name

                    if (themedComponents.contains(component) || context.packageName == riPkg) {
                        filtered += 1
                        continue
                    }

                    var name: CharSequence? = try {
                        ri.loadLabel(context.packageManager)
                    } catch (e: Exception) {
                        null
                    }
                    if (name == null) name = riPkg

                    val app = RequestApp(
                        context.getLocalizedName(riPkg, name.toString()).clean().blueprintFormat(),
                        riPkg,
                        component
                    )
                    app.loadIcon(context)
                    installedApps.add(app)
                    loaded += 1
                }
            }

            if (debug)
                Log.d(
                    "Blueprint",
                    "Apps (Installed: ${installedApps.size}, Themed: $filtered, Missing: ${installedApps.size - filtered})"
                )

            ArrayList(installedApps.distinctBy { it.packageName }.sortedBy { it.name })
        }
    }

    fun loadApps(debug: Boolean = false) {
        viewModelScope.launch {
            val themedComponents = loadThemedComponents(debug)
            themedComponentsData.postValue(themedComponents)
            delay(10)
            val appsToRequest = loadAppsToRequest(debug)
            appsToRequestData.postValue(appsToRequest)
        }
    }

    internal fun toggleSelectAll(): Boolean {
        val requestLimit = context.integer(R.integer.max_apps_to_request, -1)
        return if (selectedApps.size >= appsToRequest.size) deselectAll()
        else if (requestLimit > 0 && selectedApps.size >= requestLimit) deselectAll()
        else selectAll()
    }

    private fun selectAll(): Boolean {
        val requestLimit = context.integer(R.integer.max_apps_to_request, -1)
        if (requestLimit == 0) return false
        if (requestLimit < 0) {
            selectedAppsData.postValue(ArrayList(appsToRequest))
            return true
        }
        if (requestLimit > 0 && selectedApps.size >= requestLimit) {
            viewModelScope.launch {
                delay(50)
                requestsCallback?.onRequestLimited(RequestState.COUNT_LIMITED())
            }
            return false
        }
        val notSelectedApps = appsToRequest.filterNot { selectedApps.contains(it) }
        val requestsLeft = requestLimit - selectedApps.size
        val maxAppsToAdd =
            if (requestsLeft >= notSelectedApps.size) notSelectedApps.size else requestsLeft
        val appsToAdd = notSelectedApps.subList(0, maxAppsToAdd)
        selectedAppsData.postValue(ArrayList(selectedApps).apply { addAll(appsToAdd) })
        viewModelScope.launch {
            delay(50)
            requestsCallback?.onRequestLimited(RequestState.COUNT_LIMITED())
        }
        return true
    }

    internal fun deselectAll(): Boolean {
        selectedAppsData.postValue(ArrayList())
        return true
    }

    internal fun selectApp(app: RequestApp?): Boolean {
        app ?: return false
        if (selectedApps.size >= appsToRequest.size) return false
        val requestLimit = context.integer(R.integer.max_apps_to_request, -1)
        if (requestLimit == 0) return false
        if (requestLimit > 0 && selectedApps.size >= requestLimit) {
            viewModelScope.launch {
                delay(50)
                requestsCallback?.onRequestLimited(RequestState.COUNT_LIMITED())
            }
            return false
        }
        val index = try {
            appsToRequest.indexOf(app)
        } catch (e: Exception) {
            -1
        }
        if (index < 0) return false
        if (selectedApps.contains(app)) return false
        val currentState = getRequestState(context, selectedApps)
        if (currentState.state == RequestState.State.NORMAL) {
            selectedAppsData.postValue(ArrayList(selectedApps.apply { add(appsToRequest[index]) }))
            return true
        } else {
            viewModelScope.launch {
                delay(50)
                requestsCallback?.onRequestLimited(currentState)
            }
        }
        return false
    }

    internal fun deselectApp(app: RequestApp): Boolean {
        if (selectedApps.isNullOrEmpty()) return false
        if (selectedApps.contains(app))
            selectedAppsData.postValue(ArrayList(selectedApps.apply { remove(app) }))
        return true
    }

    fun observeAppsToRequest(owner: LifecycleOwner, onUpdated: (ArrayList<RequestApp>) -> Unit) {
        appsToRequestData.tryToObserve(owner, onUpdated)
    }

    fun observeSelectedApps(owner: LifecycleOwner, onUpdated: (ArrayList<RequestApp>) -> Unit) {
        selectedAppsData.tryToObserve(owner, onUpdated)
    }

    fun destroy(owner: LifecycleOwner) {
        themedComponentsData.removeObservers(owner)
        appsToRequestData.removeObservers(owner)
        selectedAppsData.removeObservers(owner)
    }
}