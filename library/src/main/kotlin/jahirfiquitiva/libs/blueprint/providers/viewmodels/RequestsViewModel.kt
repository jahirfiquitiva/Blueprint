/*
 * Copyright (c) 2017. Jahir Fiquitiva
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
import com.pitchedapps.butler.iconrequest.App
import com.pitchedapps.butler.iconrequest.IconRequest
import com.pitchedapps.butler.iconrequest.events.RequestsCallback
import jahirfiquitiva.libs.blueprint.BuildConfig
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.extensions.bpKonfigs
import jahirfiquitiva.libs.frames.helpers.utils.SimpleAsyncTask
import jahirfiquitiva.libs.kauextensions.extensions.getInteger
import java.io.File
import java.lang.ref.WeakReference

class RequestsViewModel:ViewModel() {
    
    fun getData():MutableList<App>? = data.value
    
    private var taskStarted = false
    private val data = MutableLiveData<MutableList<App>>()
    private var task:SimpleAsyncTask<Context, Unit>? = null
    
    fun loadData(parameter:Context,
                 onEmpty:() -> Unit, onLimited:(reason:Int, appsLeft:Int, millis:Long) -> Unit,
                 forceLoad:Boolean = false) {
        if (!taskStarted || forceLoad) {
            cancelTask(true)
            task = SimpleAsyncTask<Context, Unit>(
                    WeakReference(parameter),
                    object:SimpleAsyncTask.AsyncTaskCallback<Context, Unit>() {
                        override fun doLoad(param:Context):Unit? =
                                safeInternalLoad(param, forceLoad, object:RequestsCallback() {
                                    override fun onAppsLoaded(list:ArrayList<App>?) {
                                        list?.let { postResult(it) }
                                    }
                                    
                                    override fun onRequestEmpty(p0:Context?) =
                                            onEmpty()
                                    
                                    override fun onRequestLimited(p0:Context?,
                                                                  p1:Int, p2:Int, p3:Long) =
                                            onLimited(p1, p2, p3)
                                })
                        
                        override fun onSuccess(result:Unit) {}
                    })
            task?.execute()
            taskStarted = true
        }
    }
    
    private fun cancelTask(interrupt:Boolean = false) {
        task?.cancel(interrupt)
        task = null
        taskStarted = false
    }
    
    fun destroy(owner:LifecycleOwner, interrupt:Boolean = true) {
        cancelTask(interrupt)
        data.removeObservers(owner)
    }
    
    private fun safeInternalLoad(param:Context, forceLoad:Boolean = false,
                                 callback:RequestsCallback) {
        if (forceLoad) {
            internalLoad(param, callback)
        } else {
            if ((getData()?.size ?: 0) > 0) {
                val list = ArrayList<App>()
                getData()?.let { list.addAll(it.distinct()) }
                postResult(list)
            } else {
                internalLoad(param, callback)
            }
        }
    }
    
    fun postResult(result:MutableList<App>) {
        data.postValue(result)
        taskStarted = false
    }
    
    fun observe(owner:LifecycleOwner, onUpdated:(MutableList<App>) -> Unit) {
        destroy(owner, true)
        data.observe(owner, Observer<MutableList<App>> { r -> r?.let { onUpdated(it) } })
    }
    
    private fun internalLoad(param:Context, callback:RequestsCallback) {
        if (IconRequest.get() != null) {
            postResult(ArrayList(IconRequest.get().apps))
            return
        }
        IconRequest.start(param)
                .withAppName(param.getString(R.string.app_name))
                .withFooter("Blueprint version: %s", BuildConfig.VERSION_NAME)
                .withSubject(param.getString(R.string.request_title))
                .toEmail(param.getString(R.string.email))
                .saveDir(File(param.getString(R.string.request_save_location,
                                              Environment.getExternalStorageDirectory())))
                .generateAppFilterJson(false)
                .debugMode(BuildConfig.DEBUG)
                .filterXmlId(R.xml.appfilter)
                .withTimeLimit(param.getInteger(R.integer.time_limit_in_minutes),
                               param.bpKonfigs.prefs)
                .maxSelectionCount(param.getInteger(R.integer.max_apps_to_request))
                .setCallback(callback)
                .build().loadApps()
    }
}