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

import android.content.Context
import android.os.Environment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import jahirfiquitiva.libs.archhelpers.tasks.QAsync
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.BPKonfigs
import jahirfiquitiva.libs.blueprint.quest.App
import jahirfiquitiva.libs.blueprint.quest.IconRequest
import jahirfiquitiva.libs.blueprint.quest.events.RequestsCallback
import jahirfiquitiva.libs.kext.extensions.int
import java.io.File
import java.lang.ref.WeakReference

class RequestsViewModel : ViewModel() {
    
    fun getData(): MutableList<App>? = data.value
    
    private var taskStarted = false
    private val data = MutableLiveData<MutableList<App>>()
    private var task: QAsync<Context, Unit>? = null
    
    internal var callback: RequestsCallback? = null
    
    fun loadData(
        parameter: Context,
        debug: Boolean,
        host: String? = null,
        apiKey: String? = null,
        forceLoad: Boolean = false
                ) {
        if (!taskStarted || forceLoad) {
            cancelTask(true)
            task = QAsync(
                WeakReference(parameter),
                object : QAsync.Callback<Context, Unit>() {
                    override fun doLoad(param: Context): Unit? =
                        safeInternalLoad(param, debug, host, apiKey, forceLoad)
                    
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
        debug: Boolean,
        host: String? = null,
        apiKey: String? = null,
        forceLoad: Boolean = false
                                ) {
        if (forceLoad) {
            internalLoad(param, debug, host, apiKey, forceLoad)
        } else {
            if ((getData()?.size ?: 0) > 0) {
                val list = ArrayList<App>()
                getData()?.let { list.addAll(it.distinct()) }
                postResult(list)
            } else {
                internalLoad(param, debug, host, apiKey, forceLoad)
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
        context: Context,
        debug: Boolean,
        host: String? = null,
        apiKey: String? = null,
        forceLoad: Boolean = false
                            ) {
        val list = IconRequest.get()?.apps.orEmpty()
        if (list.isEmpty() || forceLoad) {
            IconRequest.start(context)
                .enableDebug(debug)
                .withAppName(context.getString(R.string.app_name))
                .withSubject(context.getString(R.string.request_title))
                .toEmail(context.getString(R.string.email))
                .withAPIHost(host.orEmpty())
                .withAPIKey(apiKey)
                .saveDir(
                    File(
                        context.getString(
                            R.string.request_save_location,
                            Environment.getExternalStorageDirectory())))
                .filterXml(R.xml.appfilter)
                .withTimeLimit(
                    context.int(R.integer.time_limit_in_minutes), BPKonfigs(context).prefs)
                .maxSelectionCount(context.int(R.integer.max_apps_to_request))
                .setCallback(callback)
                .build()
                .loadApps()
        } else {
            postResult(ArrayList(list))
        }
    }
}
