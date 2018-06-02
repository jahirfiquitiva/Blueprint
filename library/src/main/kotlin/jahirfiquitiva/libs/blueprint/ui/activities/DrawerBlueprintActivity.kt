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

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.TextViewCompat
import android.view.Gravity
import android.view.View
import android.widget.TextView
import ca.allanwang.kau.utils.gone
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.NavigationItem
import jahirfiquitiva.libs.blueprint.helpers.utils.BL
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_CREDITS_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_HELP_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_HOME_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_SETTINGS_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_TEMPLATES_SECTION_ID
import jahirfiquitiva.libs.blueprint.ui.activities.base.BaseBlueprintActivity
import jahirfiquitiva.libs.frames.helpers.extensions.tilesColor
import jahirfiquitiva.libs.kext.extensions.accentColor
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.boolean
import jahirfiquitiva.libs.kext.extensions.drawable
import jahirfiquitiva.libs.kext.extensions.getAppName
import jahirfiquitiva.libs.kext.extensions.getAppVersion

abstract class DrawerBlueprintActivity : BaseBlueprintActivity() {
    
    private var drawer: Drawer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDrawer(savedInstanceState)
    }
    
    override fun onSaveInstanceState(outState: Bundle?) {
        val nOutState = drawer?.saveInstanceState(outState)
        super.onSaveInstanceState(nOutState)
    }
    
    private fun initDrawer(savedInstance: Bundle?) {
        val v: View? by bind(R.id.bottom_navigation)
        v?.gone()
        val accountHeaderBuilder = AccountHeaderBuilder().withActivity(this)
        val header: Drawable? = drawable("drawer_header")
        if (header != null) {
            accountHeaderBuilder.withHeaderBackground(header)
        } else {
            accountHeaderBuilder.withHeaderBackground(accentColor)
        }
        if (boolean(R.bool.with_drawer_texts)) {
            accountHeaderBuilder.withSelectionFirstLine(getAppName())
            accountHeaderBuilder.withSelectionSecondLine("v " + getAppVersion())
        }
        accountHeaderBuilder.withProfileImagesClickable(false)
            .withResetDrawerOnProfileListClick(false)
            .withSelectionListEnabled(false)
            .withSelectionListEnabledForSingleProfile(false)
        
        if (savedInstance != null)
            accountHeaderBuilder.withSavedInstance(savedInstance)
        
        val accountHeader = accountHeaderBuilder.build()
        
        val drawerTitle: TextView? by accountHeader.view.bind(
            R.id.material_drawer_account_header_name)
        val drawerSubtitle: TextView? by accountHeader.view.bind(
            R.id.material_drawer_account_header_email)
        
        drawerTitle?.let { TextViewCompat.setTextAppearance(it, R.style.DrawerTextsWithShadow) }
        drawerSubtitle?.let { TextViewCompat.setTextAppearance(it, R.style.DrawerTextsWithShadow) }
        
        val drawerBuilder = DrawerBuilder().withActivity(this)
        toolbar?.let { drawerBuilder.withToolbar(it) }
        
        drawerBuilder.withAccountHeader(accountHeader)
            .withDelayOnDrawerClose(-1)
            .withActionBarDrawerToggle(!isIconsPicker)
        
        drawerBuilder.withOnDrawerItemClickListener { _, _, drawerItem ->
            try {
                when (drawerItem.identifier) {
                    DEFAULT_TEMPLATES_SECTION_ID.toLong() -> {
                        drawer?.closeDrawer()
                        launchKuperActivity()
                    }
                    DEFAULT_HELP_SECTION_ID.toLong() -> {
                        drawer?.closeDrawer()
                        launchHelpActivity()
                    }
                    DEFAULT_CREDITS_SECTION_ID.toLong() -> {
                        drawer?.closeDrawer()
                        startActivity(Intent(this, CreditsActivity::class.java))
                    }
                    DEFAULT_SETTINGS_SECTION_ID.toLong() -> {
                        drawer?.closeDrawer()
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                    else -> {
                        drawer?.closeDrawer()
                        return@withOnDrawerItemClickListener navigateToItem(
                            getNavigationItemWithId(drawerItem.identifier.toInt()), true)
                    }
                }
            } catch (e: Exception) {
                BL.e("Error", e)
            }
            return@withOnDrawerItemClickListener true
        }
        
        getNavigationItems().forEach {
            drawerBuilder.addDrawerItems(
                PrimaryDrawerItem().withIdentifier(it.id.toLong())
                    .withName(it.title)
                    .withIcon(drawable(it.icon))
                    .withIconTintingEnabled(true)
                    .withSelectedColor(tilesColor)
                    .withSelectedBackgroundAnimated(false))
        }
        
        if (hasTemplates) {
            drawerBuilder.addDrawerItems(
                PrimaryDrawerItem()
                    .withIdentifier(DEFAULT_TEMPLATES_SECTION_ID.toLong())
                    .withName(R.string.templates)
                    .withIcon(drawable(R.drawable.ic_widgets))
                    .withIconTintingEnabled(true)
                    .withSelectable(false))
        }
        
        drawerBuilder.addDrawerItems(DividerDrawerItem())
        
        drawerBuilder.addDrawerItems(
            PrimaryDrawerItem()
                .withIdentifier(DEFAULT_CREDITS_SECTION_ID.toLong())
                .withName(R.string.section_about)
                .withIcon(drawable(R.drawable.ic_info))
                .withIconTintingEnabled(true)
                .withSelectable(false))
        
        drawerBuilder.addDrawerItems(
            PrimaryDrawerItem()
                .withIdentifier(DEFAULT_SETTINGS_SECTION_ID.toLong())
                .withName(R.string.settings)
                .withIcon(drawable(R.drawable.ic_settings))
                .withIconTintingEnabled(true)
                .withSelectable(false))
        
        drawerBuilder.addDrawerItems(
            PrimaryDrawerItem()
                .withIdentifier(DEFAULT_HELP_SECTION_ID.toLong())
                .withName(R.string.section_help)
                .withIcon(drawable(R.drawable.ic_help))
                .withIconTintingEnabled(true)
                .withSelectable(false))
        
        drawerBuilder.withHasStableIds(true)
            .withFireOnInitialOnClick(false)
            .withDrawerGravity(Gravity.START)
            .withSavedInstance(savedInstance)
        
        drawer = drawerBuilder.build()
        if (isIconsPicker) lockDrawer()
    }
    
    override fun onBackPressed() {
        val isDrawerOpen = drawer?.isDrawerOpen ?: false
        if (!isIconsPicker) {
            when {
                isDrawerOpen -> drawer?.closeDrawer()
                currentSectionId != DEFAULT_HOME_SECTION_ID -> {
                    drawer?.setSelection(DEFAULT_HOME_SECTION_ID.toLong(), true)
                }
                else -> super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
    
    override fun navigateToItem(item: NavigationItem, fromClick: Boolean, force: Boolean): Boolean {
        if (!fromClick) drawer?.setSelection(item.id.toLong(), false)
        if (isIconsPicker) lockDrawer()
        return super.navigateToItem(item, fromClick, force)
    }
    
    private fun lockDrawer() {
        drawer?.closeDrawer()
        drawer?.drawerLayout?.setDrawerLockMode(
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.START)
        drawer?.actionBarDrawerToggle = null
    }
    
    @SuppressLint("MissingSuperCall")
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawer?.actionBarDrawerToggle?.syncState()
    }
    
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawer?.actionBarDrawerToggle?.onConfigurationChanged(newConfig)
    }
    
    override fun hasBottomNavigation(): Boolean = false
}