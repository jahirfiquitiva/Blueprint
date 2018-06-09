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
package jahirfiquitiva.libs.blueprint.quest.utils

import android.content.Context
import android.os.Build

internal fun Context.getLocalizedName(packageName: String, defaultName: String): String {
    var appName: String? = null
    try {
        val appInfo = packageManager.getApplicationInfo(
            packageName, android.content.pm.PackageManager.GET_META_DATA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                val res = packageManager.getResourcesForApplication(packageName)
                val altCntxt =
                    createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)
                val configuration = res.configuration
                configuration.setLocale(java.util.Locale("en-US"))
                appName =
                    altCntxt.createConfigurationContext(configuration).getString(appInfo.labelRes)
            } catch (e: Exception) {
                // Do nothing
            }
        }
        if (appName == null)
            appName = packageManager.getApplicationLabel(appInfo).toString()
    } catch (e: Exception) {
        // Do nothing
    }
    return appName ?: defaultName
}