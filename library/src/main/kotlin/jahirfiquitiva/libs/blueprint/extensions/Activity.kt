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
 * 	https://github.com/jahirfiquitiva/Blueprint#special-thanks
 */

package jahirfiquitiva.libs.blueprint.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import jahirfiquitiva.libs.blueprint.activities.base.BaseShowcaseActivity

fun Activity.setupStatusBar(transparent:Boolean = false) {
    if (Build.VERSION.SDK_INT >= 21) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
    if (Build.VERSION.SDK_INT >= 19) {
        val params:WindowManager.LayoutParams = window.attributes
        if (transparent) {
            params.flags = params.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        } else {
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        }
        window.attributes = params
    }
    if (Build.VERSION.SDK_INT >= 21) {
        window.statusBarColor = Color.TRANSPARENT
        setStatusBarMode()
    }
}

fun Activity.setStatusBarMode(lightMode:Boolean = false) {
    val view = window.decorView
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = view.systemUiVisibility
        if (lightMode) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        view.systemUiVisibility = flags
    }
}

fun Activity.setNavbarColor(@ColorInt color:Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
    window.navigationBarColor = color
}

fun Activity.setNavbarColorFromRes(@ColorRes colorRes:Int) {
    setNavbarColor(ContextCompat.getColor(this, colorRes))
}

fun Activity.restart() {
    val intent = this.intent
    intent.removeCategory(Intent.CATEGORY_LAUNCHER)
    startActivity(intent)
    finish()
}

fun Activity.showToast(@StringRes textRes:Int, duration:Int = Toast.LENGTH_SHORT) {
    if (isOnMainThread()) {
        Toast.makeText(this, textRes, duration).show()
    } else {
        runOnUiThread { Toast.makeText(this, textRes, duration).show() }
    }
}

fun Activity.showToast(text:String, duration:Int = Toast.LENGTH_SHORT) {
    if (isOnMainThread()) {
        Toast.makeText(this, text, duration).show()
    } else {
        runOnUiThread { Toast.makeText(this, text, duration).show() }
    }
}

fun Activity.showKeyboard(et:EditText) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
    et.requestFocus()
}

fun Activity.showKeyboard(et:TextInputEditText) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
    et.requestFocus()
}

fun Activity.hideKeyboard() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow((currentFocus ?: View(this)).windowToken, 0)
    window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    currentFocus?.clearFocus()
}

/*
fun ReleasesActivity.showChangelog(currVersion:Int, xmlRes:Int,
                                   callback:OnChangelogNeutralButtonClick? = null) {
    if (isFirstRunEver()) {
        konfig.lastVersion = currVersion
        return
    }
    if (konfig.lastVersion != currVersion) {
        ChangelogDialog.show(this, xmlRes, callback)
    }
    konfig.lastVersion = currVersion
}
*/

fun BaseShowcaseActivity.isFirstRunEver():Boolean {
    try {
        val firstInstallTime = packageManager.getPackageInfo(packageName, 0).firstInstallTime
        val lastUpdateTime = packageManager.getPackageInfo(packageName, 0).lastUpdateTime
        return firstInstallTime == lastUpdateTime
    } catch (ignored:Exception) {
    }
    return false
}