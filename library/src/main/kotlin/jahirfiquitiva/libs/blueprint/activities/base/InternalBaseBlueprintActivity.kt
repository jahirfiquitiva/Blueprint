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
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/Blueprint#special-thanks
 */

package jahirfiquitiva.libs.blueprint.activities.base

import android.app.WallpaperManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.widget.ImageView
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.extensions.*
import jahirfiquitiva.libs.blueprint.fragments.EmptyFragment
import jahirfiquitiva.libs.blueprint.fragments.HomeFragment
import jahirfiquitiva.libs.blueprint.fragments.IconsFragment
import jahirfiquitiva.libs.blueprint.holders.FilterCheckBoxHolder
import jahirfiquitiva.libs.blueprint.holders.items.FilterDrawerItem
import jahirfiquitiva.libs.blueprint.holders.items.FilterTitleDrawerItem
import jahirfiquitiva.libs.blueprint.models.NavigationItem
import jahirfiquitiva.libs.blueprint.ui.layouts.CustomCoordinatorLayout
import jahirfiquitiva.libs.blueprint.ui.layouts.FixedElevationAppBarLayout
import jahirfiquitiva.libs.blueprint.ui.views.CounterFab
import jahirfiquitiva.libs.blueprint.ui.views.callbacks.CollapsingToolbarCallback
import jahirfiquitiva.libs.blueprint.utils.*
import jahirfiquitiva.libs.fabsmenu.DimensionUtils
import jahirfiquitiva.libs.fabsmenu.FABsMenu
import jahirfiquitiva.libs.fabsmenu.FABsMenuLayout
import jahirfiquitiva.libs.fabsmenu.TitleFAB

abstract class InternalBaseBlueprintActivity:BaseBlueprintActivity() {

    private lateinit var coordinatorLayout:CustomCoordinatorLayout
    private lateinit var appBarLayout:FixedElevationAppBarLayout
    private lateinit var collapsingToolbar:CollapsingToolbarLayout
    private lateinit var toolbar:Toolbar
    private lateinit var menu:Menu
    private lateinit var fab:CounterFab
    private lateinit var fabsMenu:FABsMenu
    private lateinit var filtersDrawer:Drawer

    private var iconsFilters:ArrayList<String> = ArrayList()
    internal var currentItemId:Int = -1

    internal var filtersListener:FiltersListener? = null

    abstract fun hasBottomBar():Boolean

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusBarStyle(true, getPrimaryDarkColor().isColorLight())
        setContentView(R.layout.activity_blueprint)
        initMainComponents(savedInstanceState)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState:Bundle?) {
        outState?.putString("toolbarTitle", collapsingToolbar.title.toString())
        outState?.putInt("currentItemId", currentItemId)
        outState?.putInt("pickerKey", picker)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState:Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        collapsingToolbar.title = savedInstanceState?.getString("toolbarTitle", getAppName())
        picker = savedInstanceState?.getInt("pickerKey") ?: 0
        navigateToItem(getNavigationItems()[savedInstanceState?.getInt("currentItemId") ?: 0])
    }

    private fun initMainComponents(savedInstance:Bundle?) {
        initToolbar()
        initCollapsingToolbar()
        initFAB()
        initFABsMenu()
        initFiltersDrawer(savedInstance)
    }

    private fun initFAB() {
        fab = findViewById(R.id.fab)
        fab.setImageDrawable("ic_send".getDrawable(this).tintWithColor(
                getActiveIconsColorFor(getAccentColor())))
        fab.updateBottomMargin(getDimensionPixelSize(
                if (hasBottomBar()) R.dimen.fab_with_bottom_bar_margin else R.dimen.fab_margin))
        fab.setOnClickListener { startRequestsProcess() }
    }

    private fun initFABsMenu() {
        val fabsMenuOverlay:FABsMenuLayout = findViewById(R.id.fabs_menu_overlay)
        fabsMenuOverlay.overlayColor = getOverlayColor()

        fabsMenu = findViewById(R.id.fabs_menu)
        if (hasBottomBar()) {
            fabsMenu.menuBottomMargin = DimensionUtils.convertDpToPixel(72F, this).toInt()
        }
        fabsMenu.menuButtonIcon = "ic_plus".getDrawable(this).tintWithColor(
                getActiveIconsColorFor(getAccentColor()))
        fabsMenu.menuButtonRippleColor = getRippleColor()
        fabsMenu.menuUpdateListener = object:FABsMenu.OnFABsMenuUpdateListener {
            override fun onMenuClicked() {
                fabsMenu.toggle()
            }

            override fun onMenuCollapsed() {
                // Do nothing
            }

            override fun onMenuExpanded() {
                // Do nothing
            }
        }

        val rateFab:TitleFAB = findViewById(R.id.rate_fab)
        rateFab.setImageDrawable(
                "ic_rate".getDrawable(this).tintWithColor(getActiveIconsColor()))
        rateFab.titleTextColor = getPrimaryTextColor()
        rateFab.rippleColor = getRippleColor()
        rateFab.setOnClickListener { openLink(PLAY_STORE_LINK_PREFIX + packageName ) }

        val shareFab:TitleFAB = findViewById(R.id.share_fab)
        shareFab.setImageDrawable(
                "ic_share".getDrawable(this).tintWithColor(getActiveIconsColor()))
        shareFab.titleTextColor = getPrimaryTextColor()
        shareFab.rippleColor = getRippleColor()
        shareFab.setOnClickListener {
            //TODO: Share intent 
        }

        val donateFab:TitleFAB = findViewById(R.id.donate_fab)
        if (donationsEnabled()) {
            donateFab.setImageDrawable(
                    "ic_donate".getDrawable(this).tintWithColor(getActiveIconsColor()))
            donateFab.titleTextColor = getPrimaryTextColor()
            donateFab.rippleColor = getRippleColor()
            donateFab.setOnClickListener {
                // TODO: Init donations
            }
        } else {
            fabsMenu.removeButton(donateFab)
        }

        val helpFab:TitleFAB = findViewById(R.id.help_fab)
        helpFab.setImageDrawable(
                "ic_help".getDrawable(this).tintWithColor(getActiveIconsColor()))
        helpFab.titleTextColor = getPrimaryTextColor()
        helpFab.rippleColor = getRippleColor()
        helpFab.setOnClickListener {
            // TODO: Open help section
        }
    }

    open fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        menu = toolbar.menu
        menuInflater.inflate(R.menu.menu_main, menu)
        tintToolbar(toolbar, getActiveIconsColorFor(getPrimaryColor()))
        toolbar.setOnMenuItemClickListener(
                Toolbar.OnMenuItemClickListener { item ->
                    val i = item.itemId
                    if (i == R.id.filters) {
                        filtersDrawer.openDrawer()
                        return@OnMenuItemClickListener true
                    } else if (i == R.id.switch_theme) {
                        // switchTheme()
                        return@OnMenuItemClickListener true
                    }
                    return@OnMenuItemClickListener false
                })
    }

    private fun initCollapsingToolbar() {
        coordinatorLayout = findViewById(R.id.mainCoordinatorLayout)
        appBarLayout = findViewById(R.id.appBar)
        collapsingToolbar = findViewById(R.id.collapsingToolbar)
        collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT)
        collapsingToolbar.setCollapsedTitleTextColor(getPrimaryTextColor())
        appBarLayout.addOnOffsetChangedListener(object:CollapsingToolbarCallback() {
            override fun onVerticalOffsetChanged(appBar:AppBarLayout?, verticalOffset:Int) {
                updateToolbarColorsHere(verticalOffset)
            }
        })
        val wallpaper:ImageView? = findViewById(R.id.toolbarHeader)
        val wallManager = WallpaperManager.getInstance(this)
        if (picker == 0 && getShortcut().isEmpty()) {
            var drawable:Drawable? = null
            if (konfigs.wallpaperAsToolbarHeaderEnabled) {
                drawable = wallManager?.fastDrawable
            } else {
                val picName = getString(R.string.toolbar_picture)
                if (picName.isNotEmpty()) {
                    try {
                        drawable = picName.getDrawable(this)
                    } catch (ignored:Exception) {
                    }
                }
            }
            wallpaper?.alpha = .95f
            wallpaper?.setImageDrawable(drawable)
            wallpaper?.makeVisible()
            if (wallpaper == null) {
                val gradient:ImageView? = findViewById(R.id.toolbarGradient)
                gradient?.makeGone()
            }
        }
    }

    fun initFiltersDrawer(savedInstance:Bundle?) {
        val filtersDrawerBuilder = DrawerBuilder().withActivity(this)
        filtersDrawerBuilder.addDrawerItems(
                FilterTitleDrawerItem().withButtonListener(
                        object:FilterTitleDrawerItem.ButtonListener {
                            override fun onButtonPressed() {
                                filtersDrawer.drawerItems?.forEach {
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
        val colors = getStringArray(R.array.filters_colors)
        getIconsFiltersNames().forEach {
            if (colorIndex >= colors.size) colorIndex = 0
            filtersDrawerBuilder.addDrawerItems(
                    FilterDrawerItem().withName(it.formatCorrectly().blueprintFormat())
                            .withColor(Color.parseColor(colors[colorIndex]))
                            .withListener(object:FilterCheckBoxHolder.StateChangeListener {
                                override fun onStateChanged(checked:Boolean, title:String,
                                                            fireFiltersListener:Boolean) {
                                    if (iconsFilters.contains(title)) {
                                        if (!checked) {
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
            updateToolbarMenuItems(item)
            fabsMenu.collapse()
            if (fabsMenu.menuButton.isShown) fabsMenu.menuButton.hideIf(id != DEFAULT_HOME_POSITION)
            fabsMenu.makeGoneIf(id != DEFAULT_HOME_POSITION)
            if (fabsMenu.menuButton.isHidden)
                fabsMenu.menuButton.showIf(id == DEFAULT_HOME_POSITION)
            fab.showIf(id == DEFAULT_REQUEST_POSITION)
            appBarLayout.setExpanded(id == DEFAULT_HOME_POSITION, konfigs.animationsEnabled)
            collapsingToolbar.title = getString(
                    if (id == DEFAULT_HOME_POSITION) R.string.app_name else item.title)
            coordinatorLayout.allowScroll = id == DEFAULT_HOME_POSITION
            val rightItem = getNavigationItems()[id]
            changeFragment(getFragmentForNavigationItem(id), rightItem.tag)
            lockFiltersDrawer(id != DEFAULT_PREVIEWS_POSITION)
            return true
        } catch(ignored:Exception) {
            ignored.printStackTrace()
        }
        return false
    }

    private fun updateToolbarMenuItems(item:NavigationItem) {
        menu.changeOptionVisibility(R.id.search,
                                    item.id == DEFAULT_PREVIEWS_POSITION)
        menu.changeOptionVisibility(R.id.filters,
                                    item.id == DEFAULT_PREVIEWS_POSITION)
        menu.changeOptionVisibility(R.id.columns,
                                    item.id == DEFAULT_WALLPAPERS_POSITION)
        menu.changeOptionVisibility(R.id.refresh,
                                    item.id == DEFAULT_WALLPAPERS_POSITION)
        menu.changeOptionVisibility(R.id.select_all,
                                    item.id == DEFAULT_REQUEST_POSITION)
        tintToolbarMenu(toolbar, menu, getActiveIconsColorFor(getPrimaryColor()))
    }

    private fun lockFiltersDrawer(lock:Boolean) {
        val drawerLayout = filtersDrawer.drawerLayout
        drawerLayout?.setDrawerLockMode(
                if (lock) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.END)
    }

    open fun getFragmentForNavigationItem(id:Int):Fragment {
        val frag:Fragment
        when (id) {
            DEFAULT_HOME_POSITION -> frag = HomeFragment(this)
            DEFAULT_PREVIEWS_POSITION -> frag = IconsFragment(this)
            else -> frag = EmptyFragment()
        }
        return frag
    }

    private fun getIconsFiltersNames():Array<String> {
        return getStringArray(R.array.icon_filters)
    }

    override fun getNavigationItems():Array<NavigationItem> {
        return arrayOf(
                NavigationItem("Home", DEFAULT_HOME_POSITION, R.string.section_home,
                               R.drawable.ic_home),
                NavigationItem("Previews", DEFAULT_PREVIEWS_POSITION, R.string.section_icons,
                               R.drawable.ic_icons_preview),
                NavigationItem("Wallpapers", DEFAULT_WALLPAPERS_POSITION,
                               R.string.section_wallpapers, R.drawable.ic_wallpapers),
                NavigationItem("Apply", DEFAULT_APPLY_POSITION, R.string.section_apply,
                               R.drawable.ic_apply),
                NavigationItem("Requests", DEFAULT_REQUEST_POSITION, R.string.section_icon_request,
                               R.drawable.ic_request)
                      )
    }

    fun getToolbar():Toolbar? = toolbar

    fun updateToolbarColorsHere(offset:Int) = updateToolbarColors(toolbar, offset)

    fun startRequestsProcess() = showToast("Creating request")

    interface FiltersListener {
        fun onFiltersUpdated(filters:ArrayList<String>)
    }
}