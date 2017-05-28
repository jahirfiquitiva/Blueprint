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

package jahirfiquitiva.libs.iconshowcase.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.activities.LauncherIconRestorerActivity
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils

class IconRestorerWidget:AppWidgetProvider() {
    override fun onUpdate(context:Context?, appWidgetManager:AppWidgetManager?,
                          appWidgetIds:IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds?.forEach {
            try {
                val intent:Intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.component = ComponentName(context?.packageName,
                        "LauncherIconRestorerActivity.class")
                val rViews:RemoteViews = RemoteViews(context?.packageName,
                        R.layout.widget_icon_restorer)
                rViews.setOnClickPendingIntent(R.id.appWidget,
                        PendingIntent.getActivity(context, 0, intent, 0))
                appWidgetManager?.updateAppWidget(it, rViews)
            } catch (e:Exception) {
                Toast.makeText(context,
                        ResourceUtils.getString(context!!, R.string.launcher_icon_restorer_error,
                                ResourceUtils.getString(context, R.string.app_name)),
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onReceive(context:Context?, intent:Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == action) {
            val rViews = RemoteViews(context?.packageName, R.layout.widget_icon_restorer)
            val restore:Intent = Intent(context, LauncherIconRestorerActivity::class.java)
            rViews.setOnClickPendingIntent(R.id.appWidget,
                    PendingIntent.getActivity(context, 0, restore, 0))
            AppWidgetManager.getInstance(context)
                    .updateAppWidget(
                            intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS), rViews)
        }
    }
}