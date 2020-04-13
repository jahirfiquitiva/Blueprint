package dev.jahir.blueprint.data.viewmodels

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.blueprint.extensions.InstalledAppsComparator
import dev.jahir.blueprint.extensions.blueprintFormat
import dev.jahir.blueprint.extensions.clean
import dev.jahir.blueprint.extensions.drawableRes
import dev.jahir.blueprint.extensions.getLocalizedName
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.withXml
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.resources.nextOrNull
import dev.jahir.frames.extensions.utils.lazyMutableLiveData
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser

class RequestsViewModel : ViewModel() {

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
        context: Context?,
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
                            val res = context?.drawableRes(drawable) ?: 0
                            if (res == 0)
                                Log.w(
                                    context?.getAppName() ?: "Blueprint",
                                    "Drawable \"$drawable\" NOT found for component: \"$actualComponent\""
                                )
                        } else {
                            Log.w(
                                context?.getAppName() ?: "Blueprint",
                                "No drawable found for component: \"$actualComponent\""
                            )
                        }
                    }
                    onSuccess?.invoke(actualComponent)
                } else {
                    if (debug)
                        Log.w(
                            context?.getAppName() ?: "Blueprint",
                            "Found invalid component: \"$actualComponent\""
                        )
                }
            }
        } catch (e: Exception) {
            if (debug)
                Log.e(
                    context?.getAppName() ?: "Blueprint",
                    "Error adding parsed appfilter item! Due to Exception: ${e.message}"
                )
        }
    }

    private suspend fun loadThemedComponents(
        context: Context?,
        debug: Boolean = false
    ): ArrayList<String> {
        context ?: return arrayListOf()
        return withContext(IO) {
            val themedComponents = ArrayList<String>()
            val componentsCount = ArrayList<Pair<String, Int>>()
            context.withXml(R.xml.appfilter) { parser ->
                var eventType: Int? = parser.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.name == "item") {
                            getComponentInAppFilter(context, parser, debug) { component ->
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

    private suspend fun loadAppsToRequest(context: Context?): ArrayList<RequestApp> {
        context ?: return arrayListOf()
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

            Log.d(
                "Blueprint",
                "Loaded ${installedApps.size} total app(s), filtered out $filtered app(s)."
            )

            ArrayList(installedApps.distinctBy { it.packageName }.sortedBy { it.name })
        }
    }

    fun loadApps(context: Context?, debug: Boolean = false) {
        context ?: return
        viewModelScope.launch {
            val themedComponents = loadThemedComponents(context, debug)
            themedComponentsData.postValue(themedComponents)
            delay(10)
            val appsToRequest = loadAppsToRequest(context)
            appsToRequestData.postValue(appsToRequest)
        }
    }

    internal fun toggleSelectAll() {
        if (selectedApps.size >= appsToRequest.size) deselectAll()
        else selectAll()
    }

    private fun selectAll() {
        selectedAppsData.postValue(ArrayList(appsToRequest))
    }

    private fun deselectAll() {
        selectedAppsData.postValue(ArrayList())
    }

    internal fun selectApp(app: RequestApp) {
        if (selectedApps.size >= appsToRequest.size) return
        val index = try {
            appsToRequest.indexOf(app)
        } catch (e: Exception) {
            -1
        }
        if (index < 0) return
        if (selectedApps.contains(app)) return
        selectedAppsData.postValue(ArrayList(selectedApps.apply { add(appsToRequest[index]) }))
    }

    internal fun deselectApp(app: RequestApp) {
        if (selectedApps.isNullOrEmpty()) return
        if (selectedApps.contains(app))
            selectedAppsData.postValue(ArrayList(selectedApps.apply { remove(app) }))
    }

    fun observeAppsToRequest(owner: LifecycleOwner, onUpdated: (ArrayList<RequestApp>) -> Unit) {
        appsToRequestData.observe(owner, Observer(onUpdated))
    }

    fun observeSelectedApps(owner: LifecycleOwner, onUpdated: (ArrayList<RequestApp>) -> Unit) {
        selectedAppsData.observe(owner, Observer(onUpdated))
    }

    fun destroy(owner: LifecycleOwner) {
        themedComponentsData.removeObservers(owner)
        appsToRequestData.removeObservers(owner)
        selectedAppsData.removeObservers(owner)
    }
}