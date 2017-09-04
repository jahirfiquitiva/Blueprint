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
package jahirfiquitiva.libs.blueprint.ui.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.extensions.millisToText
import jahirfiquitiva.libs.frames.helpers.extensions.buildMaterialDialog
import jahirfiquitiva.libs.kauextensions.extensions.getInteger
import java.util.concurrent.TimeUnit


class RequestLimitDialog:BasicDialogFragment() {
    
    private var isTimeLimit:Boolean = false
    private var millis:Long = 0
    private var appsLeft:Int = 0
    
    companion object {
        private val IS_TIME_LIMIT = "is_time_limit"
        private val MILLIS = "millis"
        private val APPS_LEFT = "apps_left"
        val TAG = "request_limit_dialog"
        
        fun invoke(isTimeLimit:Boolean, millis:Long, appsLeft:Int):RequestLimitDialog {
            return RequestLimitDialog().apply {
                this.isTimeLimit = isTimeLimit
                this.millis = millis
                this.appsLeft = appsLeft
            }
        }
    }
    
    fun show(context:FragmentActivity, millis:Long) {
        dismiss(context, TAG)
        RequestLimitDialog.invoke(true, millis, 0).show(context.supportFragmentManager, TAG)
    }
    
    fun show(context:FragmentActivity, appsLeft:Int) {
        dismiss(context, TAG)
        RequestLimitDialog.invoke(false, 0, appsLeft).show(context.supportFragmentManager, TAG)
    }
    
    override fun onCreateDialog(savedInstanceState:Bundle?):Dialog {
        val content = if (isTimeLimit) {
            val preContent = context.getString(R.string.apps_limit_dialog_day,
                                               context.millisToText(
                                                       TimeUnit.MINUTES.toMillis(
                                                               context.getInteger(
                                                                       R.integer.time_limit_in_minutes).toLong())))
            
            val contentExtra = when {
                TimeUnit.MILLISECONDS.toSeconds(millis) >= 60 ->
                    context.getString(R.string.apps_limit_dialog_day_extra,
                                      context.millisToText(millis))
                else -> context.getString(R.string.apps_limit_dialog_day_extra_sec)
            }
            preContent + " " + contentExtra
        } else {
            when (appsLeft) {
                context.getInteger(R.integer.max_apps_to_request) ->
                    context.getString(R.string.apps_limit_dialog, appsLeft.toString())
                else -> context.getString(R.string.apps_limit_dialog_more, appsLeft.toString())
            }
        }
        
        return activity.buildMaterialDialog {
            title(R.string.section_icon_request)
            content(content)
            positiveText(android.R.string.ok)
        }
    }
    
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        this.isTimeLimit = arguments.getBoolean(IS_TIME_LIMIT)
        this.millis = arguments.getLong(MILLIS)
        this.appsLeft = arguments.getInt(APPS_LEFT)
    }
    
    override fun onActivityCreated(savedInstanceState:Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let {
            isTimeLimit = it.getBoolean(IS_TIME_LIMIT)
            millis = it.getLong(MILLIS)
            appsLeft = it.getInt(APPS_LEFT)
        }
    }
    
    override fun onSaveInstanceState(outState:Bundle?) {
        outState?.let {
            it.putBoolean(IS_TIME_LIMIT, isTimeLimit)
            it.putLong(MILLIS, millis)
            it.putInt(APPS_LEFT, appsLeft)
        }
        super.onSaveInstanceState(outState)
    }
}