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
 */

package jahirfiquitiva.apps.blueprint.demo

import android.os.Bundle
import com.github.javiersantos.piracychecker.PiracyChecker
import jahirfiquitiva.libs.blueprint.activities.DrawerBlueprintActivity

class MainActivity:DrawerBlueprintActivity() {
    /**
     * These things here have the default values. You can delete the ones you don't want to change
     * and/or modify the ones you want to.
     */
    override var donationsEnabled = true
    
    override fun amazonInstallsEnabled():Boolean = false
    override fun checkLPF():Boolean = true
    override fun checkStores():Boolean = true
    
    /**
     * This is your app's license key. Get yours on Google Play Dev Console
     */
    override fun getLicKey():String? = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr5D+w15edXAHbMnGOTDYmfRxSN8qpQNPH/9mkpxN3BI9/EkayopID/54Xgkny3CYKwKBDQ89s0USHTNV1uFTKpYgez55Og6JlFTeb6M3St4ZxKDcyOW46tRJZIhpIFLxUXP/v8bl9pm08ArD3aUZagTio+/4Q6HPJ96VimkIbwjTUTYqkEIJYC4SOGabaGvPhH+Tq1LiQmxdjdLirZ7hxDJIJ1QQXbdu4Phiuc++58B5ENwHzl1Y8K3ZzOCc7xeB8AhjCmscux0Y+aCjCI67Ak+SL2cLtaxnDNboNVYeLiLDuhufxihYMZEgZoDwbMVL1hw/rN4k6ucTU3mVn/pGCwIDAQAB"
    
    /**
     * This is the license checker code. Feel free to create your own implementation or
     * leave it as it is.
     * Anyways, keep the 'destroyChecker()' as the very first line of this code block
     * Return null to disable license check
     */
    override fun getLicenseChecker():PiracyChecker? {
        destroyChecker() // Important
        if (BuildConfig.DEBUG) return null
        return super.getLicenseChecker()
    }
    
    /**
     * This is needed by the app. Do NOT edit it. Do NOT delete it.
     */
    override fun onCreate(savedInstanceState:Bundle?) = super.onCreate(savedInstanceState)
}