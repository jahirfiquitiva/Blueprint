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

package jahirfiquitiva.libs.blueprint.activities.base

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.github.javiersantos.piracychecker.PiracyChecker
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.extensions.konfigs
import jahirfiquitiva.libs.blueprint.models.NavigationItem
import jahirfiquitiva.libs.blueprint.utils.*

abstract class BaseBlueprintActivity:ThemedActivity() {

    var picker:Int = 0
    var checker:PiracyChecker? = null

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        picker = getPickerKey()
    }

    internal fun changeFragment(f:Fragment, tag:String? = null, cleanStack:Boolean = false) {
        val manager = supportFragmentManager.beginTransaction()
        if (cleanStack) clearBackStack()
        if (konfigs.animationsEnabled)
            manager.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out,
                                        R.anim.abc_popup_enter, R.anim.abc_popup_exit)
        if (tag != null) manager.replace(R.id.fragments_container, f, tag)
        else manager.replace(R.id.fragments_container, f)
        manager.addToBackStack(null)
        manager.commit()
    }

    internal fun clearBackStack() {
        val manager = supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            val first = manager.getBackStackEntryAt(0)
            manager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun onBackPressed() {
        val manager = supportFragmentManager
        if (manager.backStackEntryCount > 1) manager.popBackStack()
        else super.onBackPressed()
    }

    internal fun startLicenseCheck() {
        checker = getLicenseChecker()
        checker?.start()
    }

    internal fun getShortcut():String {
        if (intent != null && intent.dataString != null && intent.dataString.contains(
                "_shortcut")) {
            return intent.dataString
        }
        return ""
    }

    internal fun getPickerKey():Int {
        if (intent != null && intent.action != null) {
            when (intent.action) {
                APPLY_ACTION -> return ICONS_APPLIER
                ADW_ACTION, TURBO_ACTION, NOVA_ACTION, Intent.ACTION_PICK, Intent.ACTION_GET_CONTENT -> return IMAGE_PICKER
                Intent.ACTION_SET_WALLPAPER -> return WALLS_PICKER
                else -> return 0
            }
        }
        return 0
    }

    abstract fun donationsEnabled():Boolean
    abstract fun amazonInstallsEnabled():Boolean
    abstract fun getLicKey():String?

    // Not really needed to override
    abstract fun getLicenseChecker():PiracyChecker?

    abstract fun getNavigationItems():Array<NavigationItem>

    override fun onDestroy() {
        super.onDestroy()
        checker?.destroy()
        checker = null
        clearBackStack()
    }

}