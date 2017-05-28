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
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.activities.base

import android.app.WallpaperManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.konifar.fab_transformation.FabTransformation
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.callbacks.CollapsingToolbarCallback
import jahirfiquitiva.libs.iconshowcase.fragments.EmptyFragment
import jahirfiquitiva.libs.iconshowcase.fragments.HomeFragment
import jahirfiquitiva.libs.iconshowcase.models.NavigationItem
import jahirfiquitiva.libs.iconshowcase.ui.layouts.CustomCoordinatorLayout
import jahirfiquitiva.libs.iconshowcase.ui.layouts.FixedElevationAppBarLayout
import jahirfiquitiva.libs.iconshowcase.ui.views.CounterFab
import jahirfiquitiva.libs.iconshowcase.utils.*
import jahirfiquitiva.libs.iconshowcase.utils.preferences.Preferences
import jahirfiquitiva.libs.iconshowcase.utils.themes.AttributeExtractor
import jahirfiquitiva.libs.iconshowcase.utils.themes.ThemeUtils
import jahirfiquitiva.libs.iconshowcase.utils.themes.TintUtils
import jahirfiquitiva.libs.iconshowcase.utils.themes.ToolbarThemer

open class InternalBaseShowcaseActivity:BaseShowcaseActivity() {

    var coordinatorLayout:CustomCoordinatorLayout? = null
    var appBarLayout:FixedElevationAppBarLayout? = null
    var collapsingToolbar:CollapsingToolbarLayout? = null
    var tabs:TabLayout? = null
    var prefs:Preferences? = null
    var toolbar:Toolbar? = null
    var menu:Menu? = null
    var fab:CounterFab? = null
    var overlay:View? = null
    var sheet:View? = null
    var currentItemId = - 1

    override fun onBackPressed() {
        if (currentItemId == 0)
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet)
        else
            super.onBackPressed()
    }

    fun initMainComponents() {
        initToolbar()
        initCollapsingToolbar()
        initFAB()
    }

    private fun initFAB() {
        fab = findViewById(R.id.fab) as CounterFab
        overlay = findViewById(R.id.overlay)
        overlay?.background = ColorDrawable(ColorUtils.getOverlayColor(ThemeUtils.isDarkTheme()))
        overlay?.setOnClickListener { view ->
            if (currentItemId == 0)
                FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet)
        }
        sheet = findViewById(R.id.sheet)
        val rateText = findViewById(R.id.action_rate) as TextView
        rateText.setCompoundDrawables(
                TintUtils.createTintedDrawable(
                        IconUtils.getDrawableWithName(this, "ic_rate"),
                        ColorUtils.getMaterialActiveIconsColor(ThemeUtils.isDarkTheme())),
                null, null, null)
        rateText.setOnClickListener {
            NetworkUtils.openLink(this,
                    NetworkUtils.PLAY_STORE_LINK_PREFIX + CoreUtils.getAppPackageName(this))
        }
        val shareText = findViewById(R.id.action_share) as TextView
        shareText.setCompoundDrawables(
                TintUtils.createTintedDrawable(
                        IconUtils.getDrawableWithName(this, "ic_rate"),
                        ColorUtils.getMaterialActiveIconsColor(ThemeUtils.isDarkTheme())),
                null, null, null)
        val donateText = findViewById(R.id.action_donate) as TextView
        if (donationsEnabled()) {
            donateText.setCompoundDrawables(
                    TintUtils.createTintedDrawable(
                            IconUtils.getDrawableWithName(this, "ic_rate"),
                            ColorUtils.getMaterialActiveIconsColor(ThemeUtils.isDarkTheme())),
                    null, null, null)
        } else {
            donateText.visibility = View.GONE
        }
        val helpText = findViewById(R.id.action_help) as TextView
        helpText.setCompoundDrawables(
                TintUtils.createTintedDrawable(
                        IconUtils.getDrawableWithName(this, "ic_questions"),
                        ColorUtils.getMaterialActiveIconsColor(ThemeUtils.isDarkTheme())),
                null, null, null)
    }

    open fun initToolbar() {
        toolbar = findViewById(R.id.toolbar) as Toolbar
        menu = toolbar?.menu
        menuInflater.inflate(R.menu.menu_main, menu)
        ToolbarThemer.tintToolbar(toolbar, ColorUtils.getMaterialActiveIconsColor(
                ColorUtils.isDarkColor(AttributeExtractor.getPrimaryColorFrom(this))))
        // TODO: Add menu items click listener
    }

    private fun initCollapsingToolbar() {
        coordinatorLayout = findViewById(R.id.mainCoordinatorLayout) as CustomCoordinatorLayout
        appBarLayout = findViewById(R.id.appBar) as FixedElevationAppBarLayout
        collapsingToolbar = findViewById(R.id.collapsingToolbar) as CollapsingToolbarLayout
        collapsingToolbar?.setExpandedTitleColor(Color.TRANSPARENT)
        collapsingToolbar?.setCollapsedTitleTextColor(
                ColorUtils.getMaterialPrimaryTextColor(ThemeUtils.isDarkTheme()))
        val context = this
        appBarLayout?.addOnOffsetChangedListener(object:CollapsingToolbarCallback() {
            override fun onVerticalOffsetChanged(appBar:AppBarLayout?, verticalOffset:Int) {
                ToolbarThemer.updateToolbarColors(context, toolbar, verticalOffset)
            }
        })
        val wallpaper:ImageView? = findViewById(R.id.toolbarHeader) as ImageView
        val wallManager = WallpaperManager.getInstance(this)
        if (getPickerKey() == 0 && getShortcut().isEmpty()) {
            var drawable:Drawable? = null
            if (prefs != null && prefs !!.wallpaperAsToolbarHeaderEnabled) {
                drawable = wallManager?.fastDrawable
            } else {
                val picName = ResourceUtils.getString(this, R.string.toolbar_picture)
                if (picName.isNotEmpty()) {
                    try {
                        drawable = IconUtils.getDrawableWithName(this, picName)
                    } catch (ignored:Exception) {
                    }
                }
            }
            wallpaper?.alpha = .95f
            wallpaper?.setImageDrawable(drawable)
            wallpaper?.visibility = View.VISIBLE
            if (wallpaper == null) {
                val gradient:ImageView? = findViewById(R.id.toolbarGradient) as ImageView
                gradient?.visibility = View.GONE
            }
        }
    }

    fun navigateToItem(item:NavigationItem):Boolean {
        val id = item.id
        if (currentItemId == id) return false
        try {
            currentItemId = id
            updateToolbarMenuItems(getNavigationItems()?.get(id) as NavigationItem)
            changeFABVisibility(
                    id == NavigationItem.DEFAULT_HOME_POSITION || id == NavigationItem.DEFAULT_REQUEST_POSITION)
            changeFABAction(id == NavigationItem.DEFAULT_HOME_POSITION)
            appBarLayout?.setExpanded(id == NavigationItem.DEFAULT_HOME_POSITION,
                    prefs !!.animationsEnabled)
            collapsingToolbar?.title = ResourceUtils.getString(this,
                    if (id == NavigationItem.DEFAULT_HOME_POSITION) R.string.app_name else item.title)
            coordinatorLayout?.allowScroll = id == NavigationItem.DEFAULT_HOME_POSITION
            tabs?.visibility = if (id == NavigationItem.DEFAULT_PREVIEWS_POSITION) View.VISIBLE else View.GONE
            changeFragment(getFragmentForNavigationItem(id))
            return true
        } catch(ignored:Exception) {
        }
        return false
    }

    private fun updateToolbarMenuItems(item:NavigationItem) {
        if (toolbar == null || menu == null) return
        MenuUtils.changeOptionVisibility(menu, R.id.search,
                item.id == NavigationItem.DEFAULT_PREVIEWS_POSITION)
        MenuUtils.changeOptionVisibility(menu, R.id.columns,
                item.id == NavigationItem.DEFAULT_WALLPAPERS_POSITION)
        MenuUtils.changeOptionVisibility(menu, R.id.refresh,
                item.id == NavigationItem.DEFAULT_WALLPAPERS_POSITION)
        MenuUtils.changeOptionVisibility(menu, R.id.select_all,
                item.id == NavigationItem.DEFAULT_REQUEST_POSITION)
        ToolbarThemer.tintToolbarMenu(toolbar, menu,
                ColorUtils.getMaterialActiveIconsColor(
                        ColorUtils.isDarkColor(AttributeExtractor.getPrimaryColorFrom(this))))
    }

    fun changeFABVisibility(visible:Boolean) {
        if (visible)
            fab?.show()
        else
            fab?.hide()
    }

    private fun changeFABAction(home:Boolean) {
        fab?.setImageDrawable(TintUtils.createTintedDrawable(
                IconUtils.getDrawableWithName(this, if (home) "ic_plus" else "ic_send"),
                ColorUtils.getMaterialActiveIconsColor(
                        ColorUtils.isDarkColor(
                                ContextCompat.getColor(this,
                                        ThemeUtils.darkOrLight(
                                                R.color.dark_theme_accent,
                                                R.color.light_theme_accent))))))
        if (home) {
            fab?.count = 0
            fab?.setOnClickListener({
                FabTransformation.with(fab).setOverlay(overlay).transformTo(sheet)
            })
        } else {
            fab?.setOnClickListener({ view ->
                Toast.makeText(view.context, "Creating request", Toast.LENGTH_SHORT)
                        .show()
            })
        }
    }

    open fun getFragmentForNavigationItem(id:Int):Fragment {
        when (id) {
            NavigationItem.DEFAULT_HOME_POSITION -> return HomeFragment()
            else -> return EmptyFragment()
        }
    }

}