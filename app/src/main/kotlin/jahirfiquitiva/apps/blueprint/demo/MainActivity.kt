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
package jahirfiquitiva.apps.blueprint.demo

import android.os.Bundle
import com.github.javiersantos.piracychecker.PiracyChecker
import jahirfiquitiva.libs.blueprint.ui.activities.BottomNavigationBlueprintActivity

class MainActivity : BottomNavigationBlueprintActivity() {
    /**
     * These things here have the default values. You can delete the ones you don't want to change
     * and/or modify the ones you want to.
     */
    override var donationsEnabled = true
    
    override fun amazonInstallsEnabled(): Boolean = false
    override fun checkLPF(): Boolean = true
    override fun checkStores(): Boolean = true
    
    /**
     * This is your app's license key. Get yours on Google Play Dev Console.
     * Default one isn't valid and could cause issues in your app.
     */
    override fun getLicKey(): String? = "MIIBIjANBgkqhkiGgKglYGYGihLuihUuhhuBlouBkuiuBIyvYV"
    
    /**
     * This is the license checker code. Feel free to create your own implementation or
     * leave it as it is.
     * Anyways, keep the 'destroyChecker()' as the very first line of this code block
     * Return null to disable license check
     */
    override fun getLicenseChecker(): PiracyChecker? {
        destroyChecker() // Important
        if (BuildConfig.DEBUG) return null
        return super.getLicenseChecker()
    }
    
    /**
     * This is needed by the app. Do NOT edit it. Do NOT delete it.
     */
    override fun onCreate(savedInstanceState: Bundle?) = super.onCreate(savedInstanceState)
}