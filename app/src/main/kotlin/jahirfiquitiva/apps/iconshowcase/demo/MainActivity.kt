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
 *   https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.apps.iconshowcase.demo

import android.os.Bundle
import jahirfiquitiva.libs.iconshowcase.activities.BottomBarShowcaseActivity

class MainActivity:BottomBarShowcaseActivity() {
    override fun donationsEnabled():Boolean = false
    override fun getLicKey():String? = null
    override fun amazonInstallsEnabled():Boolean = false

    // Things that must stay in this file but you don't really need to edit:
    override fun onCreate(savedInstanceState:Bundle?) = super.onCreate(savedInstanceState)
}