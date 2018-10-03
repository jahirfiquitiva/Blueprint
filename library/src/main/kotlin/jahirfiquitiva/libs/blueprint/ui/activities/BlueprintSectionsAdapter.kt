/*
 * Copyright (c) 2018.
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

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_APPLY_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_HOME_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_ICONS_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_REQUEST_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_WALLPAPERS_SECTION_ID
import jahirfiquitiva.libs.blueprint.models.NavigationItem
import jahirfiquitiva.libs.blueprint.ui.fragments.ApplyFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.EmptyFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.HomeFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.IconsFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.RequestsFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.WallpapersFragment
import jahirfiquitiva.libs.frames.helpers.utils.ICONS_APPLIER
import jahirfiquitiva.libs.frames.helpers.utils.ICONS_PICKER
import jahirfiquitiva.libs.frames.helpers.utils.IMAGE_PICKER
import jahirfiquitiva.libs.kext.ui.fragments.adapters.DynamicFragmentsPagerAdapter

internal class BlueprintSectionsAdapter(
    manager: FragmentManager,
    private val pickerKey: Int,
    private val withChecker: Boolean,
    private val withDebug: Boolean,
    private val navItems: Array<NavigationItem>
                                       ) :
    DynamicFragmentsPagerAdapter(manager) {
    
    private val isIconsPicker: Boolean by lazy {
        (pickerKey == ICONS_PICKER || pickerKey == IMAGE_PICKER || pickerKey == ICONS_APPLIER)
    }
    
    override fun createItem(position: Int): Fragment {
        val defFragment = EmptyFragment()
        val idForPosition = try {
            navItems[position].id
        } catch (e: Exception) {
            DEFAULT_HOME_SECTION_ID
        }
        return when (idForPosition) {
            DEFAULT_HOME_SECTION_ID -> if (isIconsPicker) defFragment else HomeFragment()
            DEFAULT_ICONS_SECTION_ID -> IconsFragment.create(pickerKey)
            DEFAULT_WALLPAPERS_SECTION_ID ->
                if (isIconsPicker) defFragment else WallpapersFragment.create(withChecker)
            DEFAULT_APPLY_SECTION_ID -> if (isIconsPicker) defFragment else ApplyFragment()
            DEFAULT_REQUEST_SECTION_ID ->
                if (isIconsPicker) defFragment else RequestsFragment.create(withDebug)
            else -> defFragment
        }
    }
    
    override fun getCount(): Int = navItems.size
}