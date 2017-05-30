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

package jahirfiquitiva.libs.iconshowcase.activities

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.widget.TextViewCompat
import android.widget.TextView
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.activities.base.InternalBaseShowcaseActivity
import jahirfiquitiva.libs.iconshowcase.models.NavigationItem
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils
import jahirfiquitiva.libs.iconshowcase.utils.CoreUtils
import jahirfiquitiva.libs.iconshowcase.utils.IconUtils
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils
import jahirfiquitiva.libs.iconshowcase.utils.themes.ThemeUtils

open class DrawerShowcaseActivity:InternalBaseShowcaseActivity() {

    var drawer:Drawer? = null

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusBar(true)
        setContentView(R.layout.activity_drawer_showcase)
        initMainComponents(savedInstanceState)
        initDrawer(savedInstanceState)
    }

    fun initDrawer(savedInstance:Bundle?) {
        val accountHeaderBuilder = AccountHeaderBuilder().withActivity(this)
        val header:Drawable? = IconUtils.getDrawableWithName(this, "drawer_header")
        if (header != null) {
            accountHeaderBuilder.withHeaderBackground(header)
        } else {
            accountHeaderBuilder.withHeaderBackground(
                    ColorUtils.getAccentColor(ThemeUtils.isDarkTheme()))
        }
        if (ResourceUtils.getBoolean(this, R.bool.with_drawer_texts)) {
            accountHeaderBuilder.withSelectionFirstLine(
                    ResourceUtils.getString(this, R.string.app_long_name))
            accountHeaderBuilder.withSelectionSecondLine("v " + CoreUtils.getAppVersion(this))
        }
        accountHeaderBuilder.withProfileImagesClickable(false)
                .withResetDrawerOnProfileListClick(false)
                .withSelectionListEnabled(false)
                .withSelectionListEnabledForSingleProfile(false)

        if (savedInstance != null)
            accountHeaderBuilder.withSavedInstance(savedInstance)

        val accountHeader = accountHeaderBuilder.build()

        val drawerTitle = accountHeader.view.findViewById(
                R.id.material_drawer_account_header_name) as TextView
        val drawerSubtitle = accountHeader.view.findViewById(
                R.id.material_drawer_account_header_email) as TextView

        TextViewCompat.setTextAppearance(drawerTitle, R.style.DrawerTextsWithShadow)
        TextViewCompat.setTextAppearance(drawerSubtitle, R.style.DrawerTextsWithShadow)


        val drawerBuilder = DrawerBuilder().withActivity(this)
        if (getToolbar() != null) drawerBuilder.withToolbar(getToolbar()!!)
        drawerBuilder.withAccountHeader(accountHeader)
                .withDelayOnDrawerClose(-1)
                .withShowDrawerOnFirstLaunch(true)

        drawerBuilder.withOnDrawerItemClickListener { _, _, drawerItem ->
            return@withOnDrawerItemClickListener navigateToItem(
                    getNavigationItems()[drawerItem.identifier.toInt()])
        }

        getNavigationItems().forEach {
            drawerBuilder.addDrawerItems(
                    PrimaryDrawerItem().withIdentifier(it.id.toLong())
                            .withName(it.title)
                            .withIcon(ResourceUtils.getDrawable(this, it.icon))
                            .withIconTintingEnabled(true))
        }

        drawerBuilder.withHasStableIds(true)
                .withShowDrawerUntilDraggedOpened(true)
                .withFireOnInitialOnClick(true)

        if (savedInstance != null)
            drawerBuilder.withSavedInstance(savedInstance)

        drawer = drawerBuilder.build()
    }

    override fun getNavigationItems():Array<NavigationItem> {
        return arrayOf(
                NavigationItem("Home", NavigationItem.DEFAULT_HOME_POSITION, R.string.section_home,
                        R.drawable.ic_home),
                NavigationItem("Previews", NavigationItem.DEFAULT_PREVIEWS_POSITION,
                        R.string.section_icons, R.drawable.ic_previews),
                NavigationItem("Wallpapers", NavigationItem.DEFAULT_WALLPAPERS_POSITION,
                        R.string.section_wallpapers, R.drawable.ic_wallpapers),
                NavigationItem("Apply", NavigationItem.DEFAULT_APPLY_POSITION,
                        R.string.section_apply, R.drawable.ic_apply),
                NavigationItem("Requests", NavigationItem.DEFAULT_REQUEST_POSITION,
                        R.string.section_icon_request, R.drawable.ic_request)
                      )
    }

    override fun onBackPressed() {
        if (currentItemId != 0) navigateToItem(getNavigationItems()[0])
        else if (drawer?.isDrawerOpen!!) drawer?.closeDrawer()
        else super.onBackPressed()
    }

}