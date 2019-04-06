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
package jahirfiquitiva.libs.blueprint.ui.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import jahirfiquitiva.libs.archhelpers.extensions.mdDialog
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.extensions.millisToText
import jahirfiquitiva.libs.kext.extensions.actv
import jahirfiquitiva.libs.kext.extensions.ctxt
import jahirfiquitiva.libs.kext.extensions.int
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class RequestLimitDialog : BasicDialogFragment() {
    
    private var isTimeLimit: Boolean = false
    private var timeLeft: Long = 0
    private var requestsLeft: Int = 0
    
    companion object {
        private const val IS_TIME_LIMIT = "is_time_limit"
        private const val TIME_LEFT = "time_left"
        private const val REQUESTS_LEFT = "apps_left"
        const val TAG = "request_limit_dialog"
        
        fun invoke(isTimeLimit: Boolean, timeLeft: Long, requestsLeft: Int): RequestLimitDialog {
            return RequestLimitDialog().apply {
                this.isTimeLimit = isTimeLimit
                this.timeLeft = timeLeft
                this.requestsLeft = requestsLeft
            }
        }
    }
    
    fun show(activity: FragmentActivity) {
        show(activity.supportFragmentManager, TAG)
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val content = if (isTimeLimit) {
            val preContent = ctxt.getString(
                R.string.apps_limit_dialog_day,
                ctxt.millisToText(
                    TimeUnit.MINUTES.toMillis(
                        ctxt.int(R.integer.time_limit_in_minutes).toLong())))
            
            val contentExtra = when {
                TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= 60 ->
                    ctxt.getString(
                        R.string.apps_limit_dialog_day_extra, ctxt.millisToText(timeLeft))
                else -> ctxt.getString(R.string.apps_limit_dialog_day_extra_sec)
            }
            "$preContent $contentExtra"
        } else {
            when (requestsLeft) {
                ctxt.int(R.integer.max_apps_to_request) ->
                    ctxt.getString(R.string.apps_limit_dialog, requestsLeft.toString())
                else -> ctxt.getString(R.string.apps_limit_dialog_more, requestsLeft.toString())
            }
        }
        
        return actv.mdDialog {
            title(R.string.section_icon_request)
            message(text = content)
            positiveButton(android.R.string.ok)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            this.isTimeLimit = it.getBoolean(IS_TIME_LIMIT, false)
            this.timeLeft = it.getLong(TIME_LEFT, 0)
            this.requestsLeft = it.getInt(REQUESTS_LEFT, 0)
        }
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let {
            isTimeLimit = it.getBoolean(IS_TIME_LIMIT, false)
            timeLeft = it.getLong(TIME_LEFT, 0)
            requestsLeft = it.getInt(REQUESTS_LEFT, 0)
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        with(outState) {
            putBoolean(IS_TIME_LIMIT, isTimeLimit)
            putLong(TIME_LEFT, timeLeft)
            putInt(REQUESTS_LEFT, requestsLeft)
        }
        super.onSaveInstanceState(outState)
    }
}
