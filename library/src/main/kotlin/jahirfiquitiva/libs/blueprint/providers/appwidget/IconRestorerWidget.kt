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
package jahirfiquitiva.libs.blueprint.providers.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import ca.allanwang.kau.utils.toast
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.ui.activities.LauncherIconRestorerActivity

class IconRestorerWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context?, appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
                         ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        context?.let {
            appWidgetIds?.forEach {
                try {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.component = ComponentName(
                        context.packageName,
                        "LauncherIconRestorerActivity.class")
                    val rViews = RemoteViews(context.packageName, R.layout.widget_icon_restorer)
                    rViews.setOnClickPendingIntent(
                        R.id.appWidget,
                        PendingIntent.getActivity(context, 0, intent, 0))
                    appWidgetManager?.updateAppWidget(it, rViews)
                } catch (e: Exception) {
                    context.toast(
                        context.getString(
                            R.string.launcher_icon_restorer_error,
                            context.getString(R.string.app_name)))
                }
            }
        }
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        context?.let {
            val action = intent?.action
            if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == action) {
                val rViews = RemoteViews(context.packageName, R.layout.widget_icon_restorer)
                val restore = Intent(context, LauncherIconRestorerActivity::class.java)
                rViews.setOnClickPendingIntent(
                    R.id.appWidget,
                    PendingIntent.getActivity(context, 0, restore, 0))
                AppWidgetManager.getInstance(context).updateAppWidget(
                    intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS),
                    rViews)
            }
        }
    }
}