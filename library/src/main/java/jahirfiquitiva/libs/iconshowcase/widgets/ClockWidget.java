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
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.RemoteViews;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.utils.CoreUtils;

public class ClockWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        boolean foundApp = false;

        String[] packages = {
                "com.android.alarmclock",
                "com.android.deskclock",
                "com.google.android.deskclock",
                "com.asus.alarmclock",
                "com.asus.deskclock",
                "com.htc.android.worldclock",
                "com.lge.clock",
                "com.motorola.blur.alarmclock",
                "com.sec.android.app.clockpackage",
                "com.sonyericsson.alarm",
                "com.sonyericsson.organizer"};

        String action = intent.getAction();

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_clock);

            Intent clockAppIntent = new Intent();

            for (String packageName : packages) {
                if (CoreUtils.isAppInstalled(context, packageName)) {
                    clockAppIntent = packageManager.getLaunchIntentForPackage(packageName);
                    if (clockAppIntent != null) {
                        foundApp = true;
                        break;
                    }
                }
            }

            if (foundApp) {
                views.setOnClickPendingIntent(R.id.clockWidget,
                        PendingIntent.getActivity(context, 0, clockAppIntent, 0));
            }

            int[] ids = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(new ComponentName(context, ClockWidget.class));

            for (int id : ids) {
                AppWidgetManager.getInstance(context).updateAppWidget(id, views);
            }
        }
    }
}