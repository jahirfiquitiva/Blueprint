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
package jahirfiquitiva.libs.blueprint.providers.viewmodels

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.os.Environment
import jahirfiquitiva.libs.archhelpers.tasks.Async
import jahirfiquitiva.libs.archhelpers.tasks.EasyAsync
import jahirfiquitiva.libs.blueprint.BuildConfig
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.extensions.bpKonfigs
import jahirfiquitiva.libs.kauextensions.extensions.getInteger
import jahirfiquitiva.libs.quest.App
import jahirfiquitiva.libs.quest.IconRequest
import jahirfiquitiva.libs.quest.events.RequestsCallback
import java.io.File
import java.lang.ref.WeakReference

class RequestsViewModel : ViewModel() {
    
    fun getData(): MutableList<App>? = data.value
    
    private var taskStarted = false
    private val data = MutableLiveData<MutableList<App>>()
    private var task: EasyAsync<Context, Unit>? = null
    
    fun loadData(
            parameter: Context,
            onEmpty: () -> Unit,
            onLimited: (reason: Int, appsLeft: Int, millis: Long) -> Unit,
            forceLoad: Boolean = false,
            onProgress: (progress: Int) -> Unit = {}
                ) {
        if (!taskStarted || forceLoad) {
            cancelTask(true)
            task = EasyAsync<Context, Unit>(
                    WeakReference(parameter),
                    object : Async.Callback<Context, Unit>() {
                        override fun doLoad(param: Context): Unit? =
                                safeInternalLoad(
                                        param, object : RequestsCallback() {
                                    override fun onAppsLoaded(apps: ArrayList<App>) {
                                        postResult(apps)
                                    }
                                    
                                    override fun onRequestEmpty(context: Context) =
                                            onEmpty()
                                    
                                    override fun onRequestLimited(
                                            context: Context,
                                            reason: Int,
                                            requestsLeft: Int,
                                            millis: Long
                                                                 ) =
                                            onLimited(reason, requestsLeft, millis)
                                }, forceLoad, onProgress)
                        
                        override fun onSuccess(result: Unit) {}
                    })
            task?.execute()
            taskStarted = true
        }
    }
    
    private fun cancelTask(interrupt: Boolean = false) {
        task?.cancel(interrupt)
        task = null
        taskStarted = false
    }
    
    fun destroy(owner: LifecycleOwner, interrupt: Boolean = true) {
        cancelTask(interrupt)
        data.removeObservers(owner)
    }
    
    private fun safeInternalLoad(
            param: Context,
            callback: RequestsCallback,
            forceLoad: Boolean = false,
            onProgress: (progress: Int) -> Unit = {}
                                ) {
        if (forceLoad) {
            internalLoad(param, callback, forceLoad, onProgress)
        } else {
            if ((getData()?.size ?: 0) > 0) {
                val list = ArrayList<App>()
                getData()?.let { list.addAll(it.distinct()) }
                postResult(list)
            } else {
                internalLoad(param, callback, forceLoad, onProgress)
            }
        }
    }
    
    fun postResult(result: MutableList<App>) {
        data.postValue(result)
        taskStarted = false
    }
    
    fun observe(owner: LifecycleOwner, onUpdated: (MutableList<App>) -> Unit) {
        destroy(owner, true)
        data.observe(owner, Observer<MutableList<App>> { r -> r?.let { onUpdated(it) } })
    }
    
    private fun internalLoad(
            param: Context,
            callback: RequestsCallback,
            forceLoad: Boolean = false,
            onProgress: (progress: Int) -> Unit = {}
                            ) {
        if (IconRequest.get() != null && !forceLoad) {
            postResult(ArrayList(IconRequest.get()?.apps.orEmpty()))
            return
        }
        initAndLoadRequestApps(param, callback, onProgress)
    }
    
    companion object {
        internal fun initAndLoadRequestApps(
                context: Context,
                callback: RequestsCallback? = null,
                onProgress: (progress: Int) -> Unit = {}
                                           ) {
            IconRequest.start(context)
                    .withAppName(context.getString(R.string.app_name))
                    .withFooter("Blueprint version: ${BuildConfig.VERSION_NAME}")
                    .withSubject(context.getString(R.string.request_title))
                    .toEmail(context.getString(R.string.email))
                    .saveDir(
                            File(
                                    context.getString(
                                            R.string.request_save_location,
                                            Environment.getExternalStorageDirectory())))
                    .generateAppFilterJson(false)
                    .debugMode(BuildConfig.DEBUG)
                    .filterXmlId(R.xml.appfilter)
                    .withTimeLimit(
                            context.getInteger(R.integer.time_limit_in_minutes),
                            context.bpKonfigs.prefs)
                    .maxSelectionCount(context.getInteger(R.integer.max_apps_to_request))
                    .setCallback(callback)
                    .build().loadApps(onProgress)
        }
    }
}