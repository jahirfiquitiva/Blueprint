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
package jahirfiquitiva.libs.blueprint.ui.activities

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.BPKonfigs

class LauncherIconRestorerActivity : AppCompatActivity() {
    
    private val configs: BPKonfigs by lazy { BPKonfigs(this) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pm = packageManager
        var className: Class<*>? = null
        
        var componentName = packageName + "." + getString(R.string.main_activity_name)
        
        try {
            className = Class.forName(componentName)
        } catch (e: Exception) {
            componentName = getString(R.string.main_activity_fullname)
            try {
                className = Class.forName(componentName)
            } catch (ignored: Exception) {
            }
        }
        
        val content: String
        if (className != null) {
            val component = ComponentName(packageName, componentName)
            if (!configs.launcherIconShown) {
                configs.launcherIconShown = true
                pm.setComponentEnabledSetting(
                    component,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP)
                content = getString(R.string.launcher_icon_restored, getString(R.string.app_name))
            } else {
                content = getString(
                    R.string.launcher_icon_not_restored,
                    getString(R.string.app_name))
            }
        } else {
            content = getString(
                R.string.launcher_icon_restorer_error,
                getString(R.string.app_name))
        }
        if (content.isNotEmpty()) Toast.makeText(this, content, Toast.LENGTH_LONG).show()
        finish()
    }
    
}