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

package jahirfiquitiva.libs.iconshowcase.activities;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import jahirfiquitiva.libs.iconshowcase.utils.CoreUtils;
import jahirfiquitiva.libs.iconshowcase.utils.preferences.Preferences;

import jahirfiquitiva.libs.iconshowcase.R;

public class LauncherIconRestorerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences mPrefs = new Preferences(this);
        PackageManager p = getPackageManager();
        Class<?> className = null;
        final String packageName = CoreUtils.getAppPackageName(this);
        String componentNameString = packageName + "." +
                getResources().getString(R.string.main_activity_name);
        try {
            className = Class.forName(componentNameString);
        } catch (ClassNotFoundException e) {
            componentNameString = getResources().getString(R.string.main_activity_fullname);
            try {
                className = Class.forName(componentNameString);
            } catch (ClassNotFoundException ex) {
                //Do nothing
            }
        }
        if (className != null) {
            ComponentName componentName = new ComponentName(packageName, componentNameString);
            if (!mPrefs.getLauncherIconShown()) {
                mPrefs.setIconShown(true);
                p.setComponentEnabledSetting(componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
                String toastContent = getResources().getString(R.string.launcher_icon_restored,
                        getResources().getString(R.string.app_name));
                Toast.makeText(this, toastContent, Toast.LENGTH_LONG).show();
            } else {
                String newToastContent = getResources().getString(R.string
                                .launcher_icon_no_restored,
                        getResources().getString(R.string.app_name));
                Toast.makeText(this, newToastContent, Toast.LENGTH_LONG).show();
            }
        } else {
            String errorToastContent = getResources().getString(R.string
                            .launcher_icon_restorer_error,
                    getResources().getString(R.string.app_name));
            Toast.makeText(this, errorToastContent, Toast.LENGTH_LONG).show();
        }
        finish();
    }

}
