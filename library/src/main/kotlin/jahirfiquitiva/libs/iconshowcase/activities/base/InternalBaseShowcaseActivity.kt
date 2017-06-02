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

package jahirfiquitiva.libs.iconshowcase.activities.base

import android.app.WallpaperManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.konifar.fab_transformation.FabTransformation
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.ui.views.callbacks.CollapsingToolbarCallback
import jahirfiquitiva.libs.iconshowcase.fragments.EmptyFragment
import jahirfiquitiva.libs.iconshowcase.fragments.HomeFragment
import jahirfiquitiva.libs.iconshowcase.fragments.IconsFragment
import jahirfiquitiva.libs.iconshowcase.models.holders.FilterCheckBoxHolder
import jahirfiquitiva.libs.iconshowcase.models.NavigationItem
import jahirfiquitiva.libs.iconshowcase.ui.layouts.CustomCoordinatorLayout
import jahirfiquitiva.libs.iconshowcase.ui.layouts.FixedElevationAppBarLayout
import jahirfiquitiva.libs.iconshowcase.ui.views.CounterFab
import jahirfiquitiva.libs.iconshowcase.ui.views.FilterDrawerItem
import jahirfiquitiva.libs.iconshowcase.ui.views.FilterTitleDrawerItem
import jahirfiquitiva.libs.iconshowcase.utils.*
import jahirfiquitiva.libs.iconshowcase.utils.themes.AttributeExtractor
import jahirfiquitiva.libs.iconshowcase.utils.themes.ThemeUtils
import jahirfiquitiva.libs.iconshowcase.utils.themes.TintUtils
import jahirfiquitiva.libs.iconshowcase.utils.themes.ToolbarThemer

open class InternalBaseShowcaseActivity:BaseShowcaseActivity() {

    private var coordinatorLayout:CustomCoordinatorLayout? = null
    private var appBarLayout:FixedElevationAppBarLayout? = null
    private var collapsingToolbar:CollapsingToolbarLayout? = null
    private var tabs:TabLayout? = null
    private var prefs:Preferences? = null
    private var toolbar:Toolbar? = null
    private var menu:Menu? = null
    private var fab:CounterFab? = null
    private var overlay:View? = null
    private var sheet:View? = null
    private var filtersDrawer:Drawer? = null
    private var iconsFilters:ArrayList<String> = ArrayList()
    internal var currentItemId = - 1

    var filtersListener:FiltersListener? = null

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Preferences(this)
    }

    override fun onBackPressed() {
        if (currentItemId == 0) {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet)
        } else {
            super.onBackPressed()
        }
    }

    fun initMainComponents(savedInstance:Bundle?) {
        initToolbar()
        initCollapsingToolbar()
        initFAB()
        initFiltersDrawer(savedInstance)
    }

    private fun initFAB() {
        fab = findViewById(R.id.fab) as CounterFab
        overlay = findViewById(R.id.overlay)
        overlay?.background = ColorDrawable(ColorUtils.getOverlayColor(ThemeUtils.isDarkTheme()))
        overlay?.setOnClickListener { _ ->
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
        ToolbarThemer.tintToolbar(toolbar !!, ColorUtils.getMaterialActiveIconsColor(
                ColorUtils.isDarkColor(AttributeExtractor.getPrimaryColorFrom(this))))
        toolbar?.setOnMenuItemClickListener(
                Toolbar.OnMenuItemClickListener { item ->
                    val i = item.itemId
                    if (i == R.id.filters) {
                        filtersDrawer?.openDrawer()
                        return@OnMenuItemClickListener true
                    } else if (i == R.id.switch_theme) {
                        // switchTheme()
                        return@OnMenuItemClickListener true
                    }
                    return@OnMenuItemClickListener false
                })
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
                ToolbarThemer.updateToolbarColors(context, toolbar !!, verticalOffset)
            }
        })
        val wallpaper:ImageView? = findViewById(R.id.toolbarHeader) as ImageView
        val wallManager = WallpaperManager.getInstance(this)
        if (getPickerKey() == 0 && getShortcut().isEmpty()) {
            var drawable:Drawable? = null
            if (prefs != null && prefs !!.getWallpaperAsToolbarHeaderEnabled()) {
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

    fun initFiltersDrawer(savedInstance:Bundle?) {
        val filtersDrawerBuilder = DrawerBuilder().withActivity(this)
        filtersDrawerBuilder.addDrawerItems(
                FilterTitleDrawerItem().withButtonListener(
                        object:FilterTitleDrawerItem.ButtonListener {
                            override fun onButtonPressed() {
                                filtersDrawer?.drawerItems?.forEach {
                                    if (it is FilterDrawerItem) {
                                        it.checkBoxHolder.apply(false, false)
                                    }
                                }
                                iconsFilters.clear()
                            }
                        }))
        val listSize = getIconsFiltersNames().size
        var index = 0
        var colorIndex = 0
        val colors = ResourceUtils.getStringArray(this, R.array.filters_colors)
        getIconsFiltersNames().forEach {
            if (colorIndex >= colors.size) colorIndex = 0
            filtersDrawerBuilder.addDrawerItems(
                    FilterDrawerItem().withName(IconUtils.formatText(it))
                            .withColor(Color.parseColor(colors[colorIndex]))
                            .withListener(object:FilterCheckBoxHolder.StateChangeListener {
                                override fun onStateChanged(checked:Boolean, title:String,
                                                            fireFiltersListener:Boolean) {
                                    if (iconsFilters.contains(title)) {
                                        if (! checked) {
                                            iconsFilters.remove(title)
                                            if (fireFiltersListener)
                                                filtersListener?.onFiltersUpdated(iconsFilters)
                                        }
                                    } else {
                                        if (checked) {
                                            iconsFilters.add(title)
                                            if (fireFiltersListener)
                                                filtersListener?.onFiltersUpdated(iconsFilters)
                                        }
                                    }
                                }
                            })
                            .withDivider(index < (listSize - 1)))
            index += 1
            colorIndex += 1
        }
        filtersDrawerBuilder.withDrawerGravity(Gravity.END)
        if (savedInstance != null) filtersDrawerBuilder.withSavedInstance(savedInstance)
        filtersDrawer = filtersDrawerBuilder.build()
    }

    fun navigateToItem(item:NavigationItem):Boolean {
        val id = item.id
        if (currentItemId == id) return false
        try {
            currentItemId = id
            updateToolbarMenuItems(getNavigationItems()[id])
            changeFABVisibility(
                    id == NavigationItem.DEFAULT_HOME_POSITION || id == NavigationItem.DEFAULT_REQUEST_POSITION)
            changeFABAction(id == NavigationItem.DEFAULT_HOME_POSITION)
            appBarLayout?.setExpanded(id == NavigationItem.DEFAULT_HOME_POSITION,
                    prefs !!.getAnimationsEnabled())
            collapsingToolbar?.title = ResourceUtils.getString(this,
                    if (id == NavigationItem.DEFAULT_HOME_POSITION) R.string.app_name else item.title)
            coordinatorLayout?.allowScroll = id == NavigationItem.DEFAULT_HOME_POSITION
            tabs?.visibility = if (id == NavigationItem.DEFAULT_PREVIEWS_POSITION) View.VISIBLE else View.GONE
            changeFragment(getFragmentForNavigationItem(id))
            lockFiltersDrawer(id != NavigationItem.DEFAULT_PREVIEWS_POSITION)
            return true
        } catch(ignored:Exception) {
        }
        return false
    }

    private fun updateToolbarMenuItems(item:NavigationItem) {
        menu?.changeOptionVisibility(R.id.search,
                item.id == NavigationItem.DEFAULT_PREVIEWS_POSITION)
        menu?.changeOptionVisibility(R.id.filters,
                item.id == NavigationItem.DEFAULT_PREVIEWS_POSITION)
        menu?.changeOptionVisibility(R.id.columns,
                item.id == NavigationItem.DEFAULT_WALLPAPERS_POSITION)
        menu?.changeOptionVisibility(R.id.refresh,
                item.id == NavigationItem.DEFAULT_WALLPAPERS_POSITION)
        menu?.changeOptionVisibility(R.id.select_all,
                item.id == NavigationItem.DEFAULT_REQUEST_POSITION)
        ToolbarThemer.tintToolbarMenu(toolbar, menu,
                ColorUtils.getMaterialActiveIconsColor(
                        ColorUtils.isDarkColor(AttributeExtractor.getPrimaryColorFrom(this))))
    }

    fun changeFABVisibility(visible:Boolean) = if (visible) fab?.show() else fab?.hide()

    private fun lockFiltersDrawer(lock:Boolean) {
        val drawerLayout = filtersDrawer?.drawerLayout
        drawerLayout?.setDrawerLockMode(
                if (lock) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.END)
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
            NavigationItem.DEFAULT_PREVIEWS_POSITION -> return IconsFragment()
            else -> return EmptyFragment()
        }
    }

    private fun getIconsFiltersNames():Array<String> {
        return ResourceUtils.getStringArray(this, R.array.icon_filters)
    }

    fun getToolbar():Toolbar? = toolbar
    fun getPrefs():Preferences? = prefs

    interface FiltersListener {
        fun onFiltersUpdated(filters:ArrayList<String>)
    }
}