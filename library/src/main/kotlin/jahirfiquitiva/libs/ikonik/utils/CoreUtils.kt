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
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IkoniK#special-thanks
 */

package jahirfiquitiva.libs.ikonik.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.TypedValue
import java.math.BigDecimal
import java.math.RoundingMode

object CoreUtils {
    val LOG_TAG = "IconShowcase"

    fun getAppVersion(context:Context):String {
        try {
            return context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e:Exception) {
            return "Unknown"
        }
    }

    fun getAppPackageName(context:Context):String = context.packageName

    fun isAppInstalled(context:Context, packageName:String):Boolean {
        val pm = context.packageManager
        var installed = false
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            installed = true
        } catch (ignored:Exception) {
        }
        return installed
    }

    fun convertDpToPx(context:Context, dp:Float):Int = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            context.resources.displayMetrics).toInt()

    fun round(value:Double, places:Int):Double {
        if (places < 0) throw IllegalArgumentException()
        var bd = BigDecimal(value)
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }
}