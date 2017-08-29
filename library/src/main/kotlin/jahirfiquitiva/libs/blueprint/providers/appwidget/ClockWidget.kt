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

package jahirfiquitiva.libs.blueprint.providers.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import ca.allanwang.kau.utils.isAppInstalled
import jahirfiquitiva.libs.blueprint.R

class ClockWidget:AppWidgetProvider() {
    @Suppress("NAME_SHADOWING")
    override fun onReceive(context:Context?, intent:Intent?) {
        super.onReceive(context, intent)
        val packages:Array<String> = arrayOf("com.android.alarmclock",
                                             "com.android.deskclock",
                                             "com.google.android.deskclock",
                                             "com.asus.alarmclock",
                                             "com.asus.deskclock",
                                             "com.htc.android.worldclock",
                                             "com.lge.clock",
                                             "com.motorola.blur.alarmclock",
                                             "com.sec.android.app.clockpackage",
                                             "com.sonyericsson.alarm",
                                             "com.sonyericsson.organizer")
        val action = intent?.action
        var foundApp = false
        val pm = context?.packageManager
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == action) {
            val rViews = RemoteViews(context?.packageName, R.layout.widget_clock)
            var intent:Intent? = Intent()
            packages.forEach breaker@ {
                val installed = context?.isAppInstalled(it) == true
                if (installed) {
                    intent = pm?.getLaunchIntentForPackage(it)
                    if (intent != null) {
                        foundApp = true
                        if (foundApp) return@breaker
                    }
                }
            }
            if (foundApp) {
                rViews.setOnClickPendingIntent(R.id.clockWidget,
                                               PendingIntent.getActivity(context, 0, intent, 0))
            }
            val ids = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(ComponentName(context, ClockWidget::class.java))
            ids.forEach { AppWidgetManager.getInstance(context).updateAppWidget(it, rViews) }
        }
    }
}