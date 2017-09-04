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

package jahirfiquitiva.libs.blueprint.ui.activities.base

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import biz.kasual.materialnumberpicker.MaterialNumberPicker
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.goneIf
import ca.allanwang.kau.utils.hideIf
import ca.allanwang.kau.utils.isHidden
import ca.allanwang.kau.utils.setMarginBottom
import ca.allanwang.kau.utils.shareText
import ca.allanwang.kau.utils.showIf
import ca.allanwang.kau.utils.statusBarLight
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.visible
import com.andremion.counterfab.CounterFab
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.pitchedapps.butler.iconrequest.IconRequest
import com.pitchedapps.butler.iconrequest.events.OnRequestProgress
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.Icon
import jahirfiquitiva.libs.blueprint.data.models.NavigationItem
import jahirfiquitiva.libs.blueprint.helpers.extensions.blueprintFormat
import jahirfiquitiva.libs.blueprint.helpers.extensions.bpKonfigs
import jahirfiquitiva.libs.blueprint.helpers.extensions.updateToolbarColors
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_APPLY_POSITION
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_HOME_POSITION
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_ICONS_POSITION
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_REQUEST_POSITION
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_WALLPAPERS_POSITION
import jahirfiquitiva.libs.blueprint.ui.activities.BpCreditsActivity
import jahirfiquitiva.libs.blueprint.ui.activities.BpSettingsActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.IconsAdapter
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.FilterCheckBoxHolder
import jahirfiquitiva.libs.blueprint.ui.fragments.ApplyFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.EmptyFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.HomeFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.IconsFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.RequestsFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.WallpapersFragment
import jahirfiquitiva.libs.blueprint.ui.items.FilterDrawerItem
import jahirfiquitiva.libs.blueprint.ui.items.FilterTitleDrawerItem
import jahirfiquitiva.libs.fabsmenu.FABsMenu
import jahirfiquitiva.libs.fabsmenu.FABsMenuLayout
import jahirfiquitiva.libs.fabsmenu.TitleFAB
import jahirfiquitiva.libs.frames.helpers.extensions.buildMaterialDialog
import jahirfiquitiva.libs.frames.helpers.extensions.framesKonfigs
import jahirfiquitiva.libs.frames.helpers.utils.PLAY_STORE_LINK_PREFIX
import jahirfiquitiva.libs.kauextensions.extensions.accentColor
import jahirfiquitiva.libs.kauextensions.extensions.activeIconsColor
import jahirfiquitiva.libs.kauextensions.extensions.cardBackgroundColor
import jahirfiquitiva.libs.kauextensions.extensions.changeOptionVisibility
import jahirfiquitiva.libs.kauextensions.extensions.enableTranslucentStatusBar
import jahirfiquitiva.libs.kauextensions.extensions.formatCorrectly
import jahirfiquitiva.libs.kauextensions.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getAppName
import jahirfiquitiva.libs.kauextensions.extensions.getDimensionPixelSize
import jahirfiquitiva.libs.kauextensions.extensions.getDrawable
import jahirfiquitiva.libs.kauextensions.extensions.getIconResource
import jahirfiquitiva.libs.kauextensions.extensions.getInteger
import jahirfiquitiva.libs.kauextensions.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getSecondaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getStringArray
import jahirfiquitiva.libs.kauextensions.extensions.isColorLight
import jahirfiquitiva.libs.kauextensions.extensions.openLink
import jahirfiquitiva.libs.kauextensions.extensions.overlayColor
import jahirfiquitiva.libs.kauextensions.extensions.primaryColor
import jahirfiquitiva.libs.kauextensions.extensions.primaryDarkColor
import jahirfiquitiva.libs.kauextensions.extensions.primaryTextColor
import jahirfiquitiva.libs.kauextensions.extensions.rippleColor
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor
import jahirfiquitiva.libs.kauextensions.extensions.tint
import jahirfiquitiva.libs.kauextensions.extensions.tintMenu
import jahirfiquitiva.libs.kauextensions.ui.decorations.GridSpacingItemDecoration
import jahirfiquitiva.libs.kauextensions.ui.layouts.CustomCoordinatorLayout
import jahirfiquitiva.libs.kauextensions.ui.layouts.FixedElevationAppBarLayout
import jahirfiquitiva.libs.kauextensions.ui.views.callbacks.CollapsingToolbarCallback
import java.util.*
import kotlin.collections.ArrayList

abstract class InternalBaseBlueprintActivity:BaseBlueprintActivity() {
    
    private lateinit var coordinatorLayout:CustomCoordinatorLayout
    private lateinit var appBarLayout:FixedElevationAppBarLayout
    private lateinit var collapsingToolbar:CollapsingToolbarLayout
    private lateinit var toolbar:Toolbar
    private lateinit var menu:Menu
    private lateinit var fabsMenu:FABsMenu
    private lateinit var filtersDrawer:Drawer
    
    private lateinit var iconsPreviewRV:RecyclerView
    private lateinit var iconsPreviewAdapter:IconsAdapter
    
    internal lateinit var fab:CounterFab
    
    var drawer:Drawer? = null
    var currentFragment:Fragment? = null
    var searchView:SearchView? = null
    
    private var iconsFilters:ArrayList<String> = ArrayList()
    internal var currentItemId:Int = -1
    
    override fun fragmentsContainer():Int = R.id.fragments_container
    
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        enableTranslucentStatusBar()
        statusBarLight = primaryDarkColor.isColorLight(0.6F)
        setContentView(R.layout.activity_blueprint)
        initMainComponents(savedInstanceState)
    }
    
    override fun onBackPressed() {
        invalidateOptionsMenu()
        val isClosed = searchView?.isIconified == true
        if (!isClosed) {
            doSearch()
            searchView?.isIconified = true
            searchView?.onActionViewCollapsed()
            menu.findItem(R.id.search)?.collapseActionView()
        } else if (currentItemId == DEFAULT_HOME_POSITION) {
            super.clearBackStack()
        } else {
            super.onBackPressed()
        }
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
        initIconsPreview()
        initFAB()
        initFABsMenu()
        initFiltersDrawer(savedInstance)
    }
    
    private fun initFAB() {
        fab = findViewById(R.id.fab)
        fab.setImageDrawable("ic_send".getDrawable(this).tint(getActiveIconsColorFor(accentColor)))
        fab.setMarginBottom(getDimensionPixelSize(
                if (hasBottomBar()) R.dimen.fab_with_bottom_bar_margin else R.dimen.fabs_margin))
        fab.setOnClickListener { startRequestsProcess() }
    }
    
    private fun initFABsMenu() {
        val fabsMenuOverlay:FABsMenuLayout = findViewById(R.id.fabs_menu_overlay)
        fabsMenuOverlay.overlayColor = overlayColor
        
        fabsMenu = findViewById(R.id.fabs_menu)
        if (hasBottomBar()) {
            fabsMenu.menuBottomMargin = 72F.dpToPx.toInt()
        }
        fabsMenu.menuButtonIcon = "ic_plus".getDrawable(this).tint(
                getActiveIconsColorFor(accentColor))
        fabsMenu.menuButtonRippleColor = rippleColor
        
        val rateFab:TitleFAB = findViewById(R.id.rate_fab)
        rateFab.setImageDrawable("ic_rate".getDrawable(this).tint(activeIconsColor))
        rateFab.titleTextColor = primaryTextColor
        rateFab.rippleColor = rippleColor
        rateFab.setOnClickListener { openLink(PLAY_STORE_LINK_PREFIX + packageName) }
        
        val shareFab:TitleFAB = findViewById(R.id.share_fab)
        shareFab.setImageDrawable("ic_share".getDrawable(this).tint(activeIconsColor))
        shareFab.titleTextColor = primaryTextColor
        shareFab.rippleColor = rippleColor
        shareFab.setOnClickListener {
            shareText(getString(R.string.share_this_app, getAppName(),
                                PLAY_STORE_LINK_PREFIX + packageName))
        }
        
        val donateFab:TitleFAB = findViewById(R.id.donate_fab)
        if (donationsEnabled) {
            donateFab.setImageDrawable("ic_donate".getDrawable(this).tint(activeIconsColor))
            donateFab.titleTextColor = primaryTextColor
            donateFab.rippleColor = rippleColor
            donateFab.setOnClickListener {
                doDonation()
            }
        } else {
            fabsMenu.removeButton(donateFab)
        }
        
        val helpFab:TitleFAB = findViewById(R.id.help_fab)
        helpFab.setImageDrawable("ic_help".getDrawable(this).tint(activeIconsColor))
        helpFab.titleTextColor = primaryTextColor
        helpFab.rippleColor = rippleColor
        helpFab.setOnClickListener {
            // TODO: Open help section
        }
    }
    
    open fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        menu = toolbar.menu
        menuInflater.inflate(R.menu.menu_main, menu)
        
        menu.let {
            val donationItem = it.findItem(R.id.donate)
            donationItem?.isVisible = donationsEnabled
            
            val searchItem = it.findItem(R.id.search)
            searchView = searchItem.actionView as SearchView
            searchView?.let {
                with(it) {
                    queryHint = getString(R.string.search_icons)
                    setOnQueryTextListener(object:SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query:String?):Boolean {
                            query?.let {
                                doSearch(it.trim())
                            }
                            return false
                        }
                        
                        override fun onQueryTextChange(newText:String?):Boolean {
                            newText?.let {
                                doSearch(it.trim())
                            }
                            return false
                        }
                    })
                    imeOptions = EditorInfo.IME_ACTION_DONE
                }
            }
            searchItem.setOnActionExpandListener(object:MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item:MenuItem?):Boolean = true
                
                override fun onMenuItemActionCollapse(item:MenuItem?):Boolean {
                    searchView?.setQuery("", true)
                    doSearch()
                    return true
                }
            })
        }
        
        toolbar.tint(getPrimaryTextColorFor(primaryColor, 0.6F),
                     getSecondaryTextColorFor(primaryColor, 0.6F),
                     getActiveIconsColorFor(primaryColor, 0.6F))
        toolbar.setOnMenuItemClickListener(
                Toolbar.OnMenuItemClickListener { item ->
                    val id = item.itemId
                    when (id) {
                        R.id.filters -> {
                            filtersDrawer.openDrawer()
                        }
                        R.id.columns -> {
                            showWallpapersColumnsDialog()
                        }
                        R.id.refresh -> {
                            refreshWallpapers()
                        }
                        R.id.select_all -> {
                            selectAllApps()
                        }
                        R.id.about -> {
                            startActivity(Intent(this, BpCreditsActivity::class.java))
                        }
                        R.id.settings -> {
                            startActivity(Intent(this, BpSettingsActivity::class.java))
                        }
                    // TODO: Manage other items
                    }
                    return@OnMenuItemClickListener true
                })
    }
    
    private fun initCollapsingToolbar() {
        coordinatorLayout = findViewById(R.id.mainCoordinatorLayout)
        appBarLayout = findViewById(R.id.appBar)
        collapsingToolbar = findViewById(R.id.collapsingToolbar)
        collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT)
        collapsingToolbar.setCollapsedTitleTextColor(primaryTextColor)
        appBarLayout.addOnOffsetChangedListener(object:CollapsingToolbarCallback() {
            override fun onVerticalOffsetChanged(appBar:AppBarLayout, verticalOffset:Int) =
                    updateToolbarColorsHere(verticalOffset)
        })
        val wallpaper:ImageView? = findViewById(R.id.toolbarHeader)
        val wallManager = WallpaperManager.getInstance(this)
        if (picker == 0 && getShortcut().isEmpty()) {
            var drawable:Drawable? = null
            if (bpKonfigs.wallpaperAsToolbarHeaderEnabled) {
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
            wallpaper?.visible()
            if (wallpaper == null) {
                val gradient:ImageView? = findViewById(R.id.toolbarGradient)
                gradient?.gone()
            }
        }
    }
    
    fun initIconsPreview() {
        iconsPreviewRV = findViewById(R.id.toolbar_icons_grid)
        iconsPreviewRV.layoutManager =
                object:GridLayoutManager(this, getInteger(R.integer.icons_columns)) {
                    override fun canScrollVertically():Boolean = false
                    override fun canScrollHorizontally():Boolean = false
                    override fun requestChildRectangleOnScreen(parent:RecyclerView?, child:View?,
                                                               rect:Rect?,
                                                               immediate:Boolean):Boolean = false
                    
                    override fun requestChildRectangleOnScreen(parent:RecyclerView?, child:View?,
                                                               rect:Rect?, immediate:Boolean,
                                                               focusedChildVisible:Boolean):Boolean = false
                }
        iconsPreviewRV.addItemDecoration(
                GridSpacingItemDecoration(getInteger(R.integer.icons_columns),
                                          getDimensionPixelSize(R.dimen.cards_margin)))
        findViewById<LinearLayout>(
                R.id.toolbar_icons_container).setOnClickListener { loadIconsIntoAdapter() }
        loadIconsIntoAdapter()
    }
    
    private fun loadIconsIntoAdapter() {
        try {
            iconsPreviewAdapter = IconsAdapter(true)
            val icons = ArrayList<Icon>()
            val list = getStringArray(R.array.icons_preview)
            list.forEach {
                icons.add(Icon(it, it.getIconResource(this)))
            }
            if (icons.isNotEmpty()) {
                icons.distinct().sorted()
                Collections.shuffle(icons)
                val correctList = ArrayList<Icon>()
                for (i in 0..(getInteger(R.integer.icons_columns) - 1)) {
                    try {
                        correctList.add(icons[i])
                    } catch (ignored:Exception) {
                    }
                }
                iconsPreviewRV.adapter = iconsPreviewAdapter
                iconsPreviewAdapter.setItems(correctList)
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }
    
    fun initFiltersDrawer(savedInstance:Bundle?) {
        val filtersDrawerBuilder = DrawerBuilder().withActivity(this)
        filtersDrawerBuilder.addDrawerItems(
                FilterTitleDrawerItem().withButtonListener(
                        object:FilterTitleDrawerItem.ButtonListener {
                            override fun onButtonPressed() {
                                val hadFilters = iconsFilters.isNotEmpty()
                                filtersDrawer.drawerItems?.forEach {
                                    (it as? FilterDrawerItem)?.checkBoxHolder?.apply(false, false)
                                }
                                if (hadFilters) {
                                    iconsFilters.clear()
                                    onFiltersUpdated(iconsFilters)
                                }
                            }
                        }))
        var index = 0
        var colorIndex = 0
        val colors = getStringArray(R.array.filters_colors)
        val filters = getStringArray(R.array.icon_filters)
        if (filters.size > 1) {
            filters.forEach {
                if (colorIndex >= colors.size) colorIndex = 0
                val name = it.formatCorrectly().blueprintFormat()
                if (!(name.equals("all", true))) {
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
                                                        onFiltersUpdated(iconsFilters)
                                                }
                                            } else {
                                                if (checked) {
                                                    iconsFilters.add(title)
                                                    if (fireFiltersListener)
                                                        onFiltersUpdated(iconsFilters)
                                                }
                                            }
                                        }
                                    })
                                    .withDivider(index < (filters.size - 1)))
                    index += 1
                    colorIndex += 1
                }
            }
        }
        filtersDrawerBuilder.withDrawerGravity(Gravity.END)
        if (savedInstance != null) filtersDrawerBuilder.withSavedInstance(savedInstance)
        filtersDrawer = filtersDrawerBuilder.build()
    }
    
    protected open fun navigateToItem(item:NavigationItem):Boolean {
        val id = item.id
        if (currentItemId == id) return false
        try {
            currentItemId = id
            
            searchView?.let {
                when (id) {
                    DEFAULT_ICONS_POSITION -> it.queryHint = getString(R.string.search_icons)
                    DEFAULT_WALLPAPERS_POSITION -> it.queryHint = getString(
                            R.string.search_wallpapers)
                    else -> it.queryHint = ""
                }
            }
            val isClosed = searchView?.isIconified == true
            if (!isClosed) {
                doSearch()
                searchView?.isIconified = true
                searchView?.onActionViewCollapsed()
                menu.findItem(R.id.search)?.collapseActionView()
            }
            invalidateOptionsMenu()
            
            updateToolbarMenuItems(item)
            
            fabsMenu.collapse()
            if (fabsMenu.menuButton.isShown) fabsMenu.menuButton.hideIf(id != DEFAULT_HOME_POSITION)
            fabsMenu.goneIf(id != DEFAULT_HOME_POSITION)
            if (fabsMenu.menuButton.isHidden)
                fabsMenu.menuButton.showIf(id == DEFAULT_HOME_POSITION)
            fab.showIf(id == DEFAULT_REQUEST_POSITION)
            
            val shouldExpand = id == DEFAULT_HOME_POSITION
            
            statusBarLight = !shouldExpand && primaryDarkColor.isColorLight(0.6F)
            
            val isExpanded = appBarLayout.isExpandedNow
            
            if (isExpanded != shouldExpand) {
                appBarLayout.setExpanded(shouldExpand, bpKonfigs.animationsEnabled)
                appBarLayout.scrollAllowed = shouldExpand
                coordinatorLayout.scrollAllowed = shouldExpand
            }
            
            collapsingToolbar.title = getString(
                    if (id == DEFAULT_HOME_POSITION) R.string.app_name else item.title)
            
            val rightItem = getNavigationItems()[id]
            changeFragment(getFragmentForNavigationItem(id), rightItem.tag)
            lockFiltersDrawer(id != DEFAULT_ICONS_POSITION ||
                                      getStringArray(R.array.icon_filters).size <= 1)
            return true
        } catch (e:Exception) {
            e.printStackTrace()
        }
        return false
    }
    
    private fun updateToolbarMenuItems(item:NavigationItem) {
        menu.changeOptionVisibility(R.id.search,
                                    item.id == DEFAULT_ICONS_POSITION ||
                                            item.id == DEFAULT_WALLPAPERS_POSITION)
        menu.changeOptionVisibility(R.id.filters,
                                    item.id == DEFAULT_ICONS_POSITION &&
                                            getStringArray(R.array.icon_filters).size > 1)
        menu.changeOptionVisibility(R.id.columns,
                                    item.id == DEFAULT_WALLPAPERS_POSITION)
        menu.changeOptionVisibility(R.id.refresh,
                                    item.id == DEFAULT_WALLPAPERS_POSITION)
        menu.changeOptionVisibility(R.id.select_all,
                                    item.id == DEFAULT_REQUEST_POSITION)
        toolbar.tintMenu(getActiveIconsColorFor(primaryColor))
    }
    
    private fun lockFiltersDrawer(lock:Boolean) {
        val drawerLayout = filtersDrawer.drawerLayout
        drawerLayout?.setDrawerLockMode(
                if (lock) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.END)
    }
    
    open fun getFragmentForNavigationItem(id:Int):Fragment {
        val frag:Fragment = when (id) {
            DEFAULT_HOME_POSITION -> HomeFragment()
            DEFAULT_ICONS_POSITION -> IconsFragment()
            DEFAULT_WALLPAPERS_POSITION -> WallpapersFragment()
            DEFAULT_APPLY_POSITION -> ApplyFragment()
            DEFAULT_REQUEST_POSITION -> RequestsFragment()
            else -> EmptyFragment()
        }
        currentFragment = frag
        return frag
    }
    
    override fun getNavigationItems():Array<NavigationItem> =
            arrayOf(NavigationItem.HOME,
                    NavigationItem.ICONS,
                    NavigationItem.WALLPAPERS,
                    NavigationItem.APPLY,
                    NavigationItem.REQUESTS)
    
    fun getToolbar():Toolbar? = toolbar
    
    internal fun updateToolbarColorsHere(offset:Int) =
            updateToolbarColors(toolbar, drawer, offset, 0.6F)
    
    fun startRequestsProcess() {
        // TODO: Request permissions
        IconRequest.get()?.send(object:OnRequestProgress() {
            override fun doWhenReady() {
                // TODO
            }
            
            override fun doWhenStarted() {
                // TODO
            }
            
            override fun doOnError() {
                // TODO
            }
        })
    }
    
    internal fun onFiltersUpdated(filters:ArrayList<String>) {
        if (currentFragment is IconsFragment) {
            (currentFragment as IconsFragment).applyFilters(filters)
        }
    }
    
    internal fun doSearch(search:String = "") {
        if (currentFragment is IconsFragment) {
            (currentFragment as IconsFragment).doSearch(search)
        } else if (currentFragment is WallpapersFragment) {
            (currentFragment as WallpapersFragment).applyFilter(search)
        }
    }
    
    internal fun showWallpapersColumnsDialog() {
        if (currentFragment is WallpapersFragment) {
            destroyDialog()
            
            val currentColumns = framesKonfigs.columns
            
            val numberPicker = MaterialNumberPicker.Builder(this)
                    .minValue(1)
                    .maxValue(6)
                    .defaultValue(currentColumns)
                    .backgroundColor(cardBackgroundColor)
                    .separatorColor(Color.TRANSPARENT)
                    .textColor(secondaryTextColor)
                    .enableFocusability(false)
                    .wrapSelectorWheel(true)
                    .build()
            
            dialog = buildMaterialDialog {
                title(R.string.wallpapers_columns_setting_title)
                customView(numberPicker, false)
                positiveText(android.R.string.ok)
                onPositive { dialog, _ ->
                    try {
                        val newColumns = numberPicker.value
                        if (currentColumns != newColumns) {
                            framesKonfigs.columns = newColumns
                            (currentFragment as WallpapersFragment).configureRVColumns()
                        }
                    } catch (ignored:Exception) {
                    }
                    dialog.dismiss()
                }
            }
            dialog?.show()
        }
    }
    
    internal fun refreshWallpapers() {
        if (currentFragment is WallpapersFragment) {
            (currentFragment as WallpapersFragment).reloadData(1)
        }
    }
    
    internal fun selectAllApps() {
        if (currentFragment is RequestsFragment) {
            (currentFragment as RequestsFragment).toggleSelectAll()
        }
    }
}