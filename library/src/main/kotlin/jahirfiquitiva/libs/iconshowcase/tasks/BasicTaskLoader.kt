/*
 * Copyright (c) 2017.  Jahir Fiquitiva
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
 *
 * Special thanks to the project contributors and collaborators
 *   https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.tasks

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.support.v4.content.AsyncTaskLoader

@Suppress("LeakingThis")
abstract class BasicTaskLoader<T>(context:Context,
                                  val listener:TaskListener? = null):
        AsyncTaskLoader<T>(context) {

    val lastConfig = InterestingConfigChanges()
    var data:T? = null

    init {
        onContentChanged()
    }

    /**
     * Called when there is new data to deliver to the client. The super class will take care of
     * delivering it. The implementation here just adds a little more logic.
     */
    override fun deliverResult(newData:T) {
        // An async query came in while the loader is stopped. We don't need the result.
        if (isReset && newData != null) onReleaseResources(newData)
        val oldData = data
        data = newData
        if (isStarted) {
            // If the Loader is currently started, we can immediately deliver its results
            super.deliverResult(data)
        }
        // At this point we can release the resources associated with 'oldData' if needed.
        // Now that the new result is delivered, we know that it is no longer in use.
        if (oldData != null) onReleaseResources(oldData)
    }

    /**
     * Handles a request to start the loader
     */
    override fun onStartLoading() {
        // Call listener method when task has started
        listener?.onTaskStarted()
        // If we currently have a result available, deliver it immediately
        if (data != null) deliverResult(data as T)
        // Has something interesting in the configuration changed since we last built the data?
        val configChange = lastConfig.applyNewConfig(context.resources)
        // If the data has changed since the last time it was loaded or is not currently available,
        // start a load
        if (takeContentChanged() || data == null || configChange) forceLoad()
    }

    /**
     * Handles a request to stop the loader
     */
    override fun onStopLoading() {
        cancelLoad()
    }

    /**
     * Handles a request to cancel a load
     */
    override fun onCanceled(data:T) {
        super.onCanceled(data)
        // At this point we can release the resources associated with 'data' if needed
        onReleaseResources(data)
    }

    /**
     * Handles a request to completely reset the loader
     */
    override fun onReset() {
        super.onReset()
        // Ensure the loader is stopped
        onStopLoading()
        // At this point we can release the resources associated with 'data' if needed
        if (data != null) {
            onReleaseResources(data as T)
            data = null
        }
    }

    /**
     * Helper function to take care of releasing resources associated with an actively loaded
     * data set.
     */
    open fun onReleaseResources(data:T) {
        // For a simple List<> there would be nothing to do here.
        // For something like a Cursor, you should close it here.
    }

    interface TaskListener {
        fun onTaskStarted()
    }

    class InterestingConfigChanges {
        val lastConfiguration = Configuration()
        var lastDensity:Int = 0

        fun applyNewConfig(res:Resources):Boolean {
            val configChanges = lastConfiguration.updateFrom(res.configuration)
            val densityChanged = lastDensity != res.displayMetrics.densityDpi
            if (densityChanged || (configChanges and
                    (ActivityInfo.CONFIG_LOCALE or ActivityInfo.CONFIG_UI_MODE
                            or ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
                lastDensity = res.displayMetrics.densityDpi
                return true
            }
            return false
        }
    }

}