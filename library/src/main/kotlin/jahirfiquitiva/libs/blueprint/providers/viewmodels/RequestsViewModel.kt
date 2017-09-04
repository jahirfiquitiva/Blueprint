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

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.os.Environment
import com.pitchedapps.butler.iconrequest.App
import com.pitchedapps.butler.iconrequest.IconRequest
import com.pitchedapps.butler.iconrequest.events.RequestsCallback
import jahirfiquitiva.libs.blueprint.BuildConfig
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.extensions.bpKonfigs
import jahirfiquitiva.libs.frames.helpers.utils.AsyncTaskManager
import jahirfiquitiva.libs.kauextensions.extensions.getInteger
import java.io.File

class RequestsViewModel:ViewModel() {
    
    val items = MutableLiveData<ArrayList<App>>()
    private var task:AsyncTaskManager<Unit, Context>? = null
    
    fun loadData(parameter:Context, onEmpty:() -> Unit,
                 onLimited:(reason:Int, appsLeft:Int, millis:Long) -> Unit,
                 forceLoad:Boolean = false) {
        stopTask(true)
        task = AsyncTaskManager(parameter, {},
                                {
                                    internalLoad(parameter, object:RequestsCallback() {
                                        override fun onAppsLoaded(p0:java.util.ArrayList<App>?) {
                                            p0?.let { postResult(it) }
                                        }
                
                                        override fun onRequestEmpty(p0:Context?) {
                                            onEmpty()
                                        }
                
                                        override fun onRequestLimited(p0:Context?, p1:Int, p2:Int,
                                                                      p3:Long) {
                                            onLimited(p1, p2, p3)
                                        }
                                    }, forceLoad)
                                }, {})
        task?.execute()
    }
    
    private fun loadItems(param:Context, callback:RequestsCallback) {
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
    
    fun stopTask(interrupt:Boolean = false) {
        task?.cancelTask(interrupt)
    }
    
    private fun internalLoad(param:Context, callback:RequestsCallback, forceLoad:Boolean = false) {
        if (forceLoad) {
            loadItems(param, callback)
        } else {
            if (items.value != null && (items.value?.size ?: 0) > 0) {
                val list = ArrayList<App>()
                items.value?.let { list.addAll(it.distinct()) }
                postResult(list)
            } else {
                loadItems(param, callback)
            }
        }
    }
    
    internal fun postResult(data:ArrayList<App>) {
        items.postValue(ArrayList(data.distinct()))
    }
}