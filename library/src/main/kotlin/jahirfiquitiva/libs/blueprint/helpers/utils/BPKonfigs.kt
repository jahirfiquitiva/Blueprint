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

package jahirfiquitiva.libs.blueprint.helpers.utils

import android.content.Context
import jahirfiquitiva.libs.frames.helpers.utils.FramesKonfigs

class BPKonfigs(nm:String, cntxt:Context):FramesKonfigs(nm, cntxt) {
    companion object {
        fun newInstance(name:String, context:Context) = BPKonfigs(name, context)
    }
    
    var launcherIconShown:Boolean
        get() = prefs.getBoolean(LAUNCHER_ICON_SHOWN, true)
        set(shown) = prefsEditor.putBoolean(LAUNCHER_ICON_SHOWN, shown).apply()
    
    var isApplyCardDismissed:Boolean
        get() = prefs.getBoolean(APPLY_CARD_DISMISSED, false)
        set(dismissed) = prefsEditor.putBoolean(APPLY_CARD_DISMISSED, dismissed).apply()
    
    var wallpaperAsToolbarHeaderEnabled:Boolean
        get() = prefs.getBoolean(WALLPAPER_AS_TOOLBAR_HEADER, true)
        set(enabled) = prefsEditor.putBoolean(WALLPAPER_AS_TOOLBAR_HEADER, enabled).apply()
    
}