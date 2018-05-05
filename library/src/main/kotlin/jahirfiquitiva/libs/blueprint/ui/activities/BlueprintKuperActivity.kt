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

import com.github.javiersantos.piracychecker.PiracyChecker
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.BPKonfigs
import jahirfiquitiva.libs.kuper.ui.activities.KuperActivity

class BlueprintKuperActivity : KuperActivity() {
    override val configs: BPKonfigs by lazy { BPKonfigs(this) }
    override fun getLicKey(): String? = ""
    override fun getLicenseChecker(): PiracyChecker? = null
    override fun amazonInstallsEnabled(): Boolean = false
    override fun checkLPF(): Boolean = false
    override fun checkStores(): Boolean = false
    override var donationsEnabled: Boolean = false
    override fun getActivityTitle(): String = getString(R.string.templates)
}