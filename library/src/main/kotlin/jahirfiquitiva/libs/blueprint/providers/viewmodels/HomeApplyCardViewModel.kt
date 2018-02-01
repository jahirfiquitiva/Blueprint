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
package jahirfiquitiva.libs.blueprint.providers.viewmodels

import android.content.Context
import jahirfiquitiva.libs.archhelpers.viewmodels.BasicViewModel
import jahirfiquitiva.libs.blueprint.helpers.extensions.bpKonfigs
import jahirfiquitiva.libs.blueprint.helpers.extensions.defaultLauncher

class HomeApplyCardViewModel : BasicViewModel<Context, Boolean>() {
    override fun internalLoad(param: Context): Boolean {
        val initCard = param.defaultLauncher?.isActuallySupported == true
        return if (initCard) {
            !param.bpKonfigs.isApplyCardDismissed
        } else {
            false
        }
    }
    
    override fun isOldDataValid(): Boolean = false
}