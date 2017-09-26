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

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import biz.kasual.materialnumberpicker.MaterialNumberPicker
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.goneIf
import ca.allanwang.kau.utils.hideIf
import ca.allanwang.kau.utils.isHidden
import ca.allanwang.kau.utils.setMarginBottom
import ca.allanwang.kau.utils.setMarginRight
import ca.allanwang.kau.utils.shareText
import ca.allanwang.kau.utils.showIf
import ca.allanwang.kau.utils.snackbar
import ca.allanwang.kau.utils.statusBarLight
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.visible
import ca.allanwang.kau.xml.showChangelog
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
import jahirfiquitiva.libs.blueprint.ui.activities.CreditsActivity
import jahirfiquitiva.libs.blueprint.ui.activities.HelpActivity
import jahirfiquitiva.libs.blueprint.ui.activities.SettingsActivity
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
import jahirfiquitiva.libs.frames.helpers.extensions.PermissionRequestListener
import jahirfiquitiva.libs.frames.helpers.extensions.buildMaterialDialog
import jahirfiquitiva.libs.frames.helpers.extensions.checkPermission
import jahirfiquitiva.libs.frames.helpers.extensions.framesKonfigs
import jahirfiquitiva.libs.frames.helpers.extensions.requestPermissions
import jahirfiquitiva.libs.frames.helpers.utils.PLAY_STORE_LINK_PREFIX
import jahirfiquitiva.libs.frames.ui.activities.base.BaseFramesActivity
import jahirfiquitiva.libs.frames.ui.widgets.CustomToolbar
import jahirfiquitiva.libs.frames.ui.widgets.SearchView
import jahirfiquitiva.libs.frames.ui.widgets.bindSearchView
import jahirfiquitiva.libs.kauextensions.extensions.accentColor
import jahirfiquitiva.libs.kauextensions.extensions.activeIconsColor
import jahirfiquitiva.libs.kauextensions.extensions.bind
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
import jahirfiquitiva.libs.kauextensions.extensions.showToast
import jahirfiquitiva.libs.kauextensions.extensions.tint
import jahirfiquitiva.libs.kauextensions.extensions.tintMenu
import jahirfiquitiva.libs.kauextensions.ui.decorations.GridSpacingItemDecoration
import jahirfiquitiva.libs.kauextensions.ui.layouts.CustomCoordinatorLayout
import jahirfiquitiva.libs.kauextensions.ui.layouts.FixedElevationAppBarLayout
import jahirfiquitiva.libs.kauextensions.ui.views.callbacks.CollapsingToolbarCallback
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseBlueprintActivity:BaseFramesActivity() {
    
    abstract fun hasBottomNavigation():Boolean
    
    private val coordinatorLayout:CustomCoordinatorLayout by bind(R.id.mainCoordinatorLayout)
    private val appbarLayout:FixedElevationAppBarLayout by bind(R.id.appbar)
    private val collapsingToolbar:CollapsingToolbarLayout by bind(R.id.collapsingToolbar)
    private val iconsPreviewRV:RecyclerView by bind(R.id.toolbar_icons_grid)
    
    private lateinit var filtersDrawer:Drawer
    private lateinit var iconsPreviewAdapter:IconsAdapter
    
    internal val toolbar:CustomToolbar by bind(R.id.toolbar)
    internal val fabsMenu:FABsMenu by bind(R.id.fabs_menu)
    internal val fab:CounterFab by bind(R.id.fab)
    
    var drawer:Drawer? = null
    var searchView:SearchView? = null
    private var currentFragment:Fragment? = null
    
    private var iconsFilters:ArrayList<String> = ArrayList()
    internal var currentItemId:Int = -1
    
    override fun fragmentsContainer():Int = R.id.fragments_container
    
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        enableTranslucentStatusBar()
        statusBarLight = primaryDarkColor.isColorLight(0.6F)
        setContentView(R.layout.activity_blueprint)
        toolbar.bindToActivity(this, false)
        initMainComponents(savedInstanceState)
    }
    
    override fun onBackPressed() {
        invalidateOptionsMenu()
        val isOpen = searchView?.isOpen ?: false
        if (isOpen) {
            doSearch()
            searchView?.revealClose()
        } else {
            if (!hasBottomNavigation()) {
                if (currentItemId != DEFAULT_HOME_POSITION) {
                    navigateToItem(getNavigationItemWithId(DEFAULT_HOME_POSITION))
                } else {
                    supportFinishAfterTransition()
                }
            } else {
                supportFinishAfterTransition()
            }
        }
    }
    
    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState:Bundle?) {
        outState?.putString("toolbarTitle", collapsingToolbar.title.toString())
        outState?.putInt("currentItemId", currentItemId)
        super.onSaveInstanceState(outState)
    }
    
    override fun onRestoreInstanceState(savedInstanceState:Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        collapsingToolbar.title = savedInstanceState?.getString("toolbarTitle", getAppName())
        navigateToItem(getNavigationItemWithId(
                savedInstanceState?.getInt("currentItemId") ?: DEFAULT_HOME_POSITION))
    }
    
    override fun onResume() {
        super.onResume()
        initWallpaperInToolbar()
    }
    
    private fun initMainComponents(savedInstance:Bundle?) {
        // initToolbar()
        initCollapsingToolbar()
        initIconsPreview()
        initFAB()
        initFABsMenu()
        initFiltersDrawer(savedInstance)
    }
    
    private fun initFAB() {
        fab.setImageDrawable("ic_send".getDrawable(this).tint(getActiveIconsColorFor(accentColor)))
        fab.setMarginRight(16F.dpToPx.toInt())
        fab.setMarginBottom((if (hasBottomNavigation()) 72F else 16F).dpToPx.toInt())
        fab.setOnClickListener { startRequestsProcess() }
    }
    
    private fun initFABsMenu() {
        val fabsMenuOverlay:FABsMenuLayout = findViewById(R.id.fabs_menu_overlay)
        fabsMenuOverlay.overlayColor = overlayColor
        
        if (hasBottomNavigation()) {
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
        helpFab.setOnClickListener { launchHelpActivity() }
    }
    
    override fun onCreateOptionsMenu(menu:Menu?):Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.let {
            val donationItem = it.findItem(R.id.donate)
            donationItem?.isVisible = donationsEnabled
            
            updateToolbarMenuItems(getNavigationItemWithId(currentItemId), it)
            
            searchView = bindSearchView(it, R.id.search, true)
            
            searchView?.listener = object:SearchView.SearchListener {
                override fun onQueryChanged(query:String) {
                    doSearch(query)
                }
                
                override fun onQuerySubmit(query:String) {
                    doSearch(query)
                }
                
                override fun onSearchOpened(searchView:SearchView) {
                    // Do nothing
                }
                
                override fun onSearchClosed(searchView:SearchView) {
                    doSearch()
                }
            }
            searchView?.hintText = when (currentItemId) {
                DEFAULT_ICONS_POSITION -> getString(R.string.search_icons)
                DEFAULT_WALLPAPERS_POSITION -> getString(R.string.search_wallpapers)
                DEFAULT_APPLY_POSITION -> getString(R.string.search_launchers)
                DEFAULT_REQUEST_POSITION -> getString(R.string.search_apps)
                else -> ""
            }
        }
        toolbar.tint(getPrimaryTextColorFor(primaryColor, 0.6F),
                     getSecondaryTextColorFor(primaryColor, 0.6F),
                     getActiveIconsColorFor(primaryColor, 0.6F))
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item:MenuItem?):Boolean {
        item?.let {
            when (it.itemId) {
                R.id.filters -> filtersDrawer.openDrawer()
                R.id.columns -> showWallpapersColumnsDialog()
                R.id.refresh -> refreshWallpapers()
                R.id.changelog -> showChangelog(R.xml.changelog, secondaryTextColor)
                R.id.select_all -> toggleSelectAll()
                R.id.help -> launchHelpActivity()
                R.id.about -> startActivity(Intent(this, CreditsActivity::class.java))
                R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun initCollapsingToolbar() {
        collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT)
        collapsingToolbar.setCollapsedTitleTextColor(primaryTextColor)
        appbarLayout.addOnOffsetChangedListener(object:CollapsingToolbarCallback() {
            override fun onVerticalOffsetChanged(appBar:AppBarLayout, verticalOffset:Int) =
                    updateToolbarColorsHere(verticalOffset)
        })
        initWallpaperInToolbar()
    }
    
    private fun initWallpaperInToolbar() {
        val wallpaper:ImageView? = findViewById(R.id.toolbarHeader)
        val wallManager = WallpaperManager.getInstance(this)
        if (picker == 0 && getShortcut().isEmpty()) {
            val drawable:Drawable? = if (bpKonfigs.wallpaperAsToolbarHeaderEnabled) {
                wallManager?.fastDrawable
            } else {
                val picName = getString(R.string.toolbar_picture)
                if (picName.isNotEmpty()) {
                    try {
                        picName.getDrawable(this)
                    } catch (ignored:Exception) {
                        null
                    }
                } else null
            }
            wallpaper?.alpha = .95f
            wallpaper?.setImageDrawable(drawable)
            wallpaper?.visible()
        }
    }
    
    fun initIconsPreview() {
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
                for (i in 0 until getInteger(R.integer.icons_columns)) {
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
    
    internal fun navigateToItem(item:NavigationItem):Boolean {
        val id = item.id
        if (currentItemId == id) return false
        try {
            currentItemId = id
            return internalNavigateToItem(item)
        } catch (e:Exception) {
            e.printStackTrace()
        }
        return false
    }
    
    internal open fun internalNavigateToItem(item:NavigationItem):Boolean {
        try {
            val isOpen = searchView?.isOpen ?: false
            if (isOpen) {
                doSearch()
                searchView?.revealClose()
            }
            
            invalidateOptionsMenu()
            
            fabsMenu.collapse()
            if (fabsMenu.menuButton.isShown) fabsMenu.menuButton.hideIf(
                    item.id != DEFAULT_HOME_POSITION)
            fabsMenu.goneIf(item.id != DEFAULT_HOME_POSITION)
            if (fabsMenu.menuButton.isHidden)
                fabsMenu.menuButton.showIf(item.id == DEFAULT_HOME_POSITION)
            fab.showIf(item.id == DEFAULT_REQUEST_POSITION)
            
            val shouldExpand = item.id == DEFAULT_HOME_POSITION
            
            statusBarLight = !shouldExpand && primaryDarkColor.isColorLight(0.6F)
            
            val isExpanded = appbarLayout.isExpandedNow
            
            if (isExpanded != shouldExpand) {
                appbarLayout.setExpanded(shouldExpand, bpKonfigs.animationsEnabled)
                appbarLayout.scrollAllowed = shouldExpand
                coordinatorLayout.scrollAllowed = shouldExpand
            }
            
            collapsingToolbar.title = getString(
                    if (item.id == DEFAULT_HOME_POSITION) R.string.app_name else item.title)
            
            val rightItem = getNavigationItemWithId(item.id)
            changeFragment(getFragmentForNavigationItem(item.id), rightItem.tag)
            lockFiltersDrawer(item.id != DEFAULT_ICONS_POSITION ||
                                      getStringArray(R.array.icon_filters).size <= 1)
            return true
        } catch (e:Exception) {
            e.printStackTrace()
        }
        return false
    }
    
    private fun updateToolbarMenuItems(item:NavigationItem, menu:Menu) {
        menu.changeOptionVisibility(R.id.search, item.id != DEFAULT_HOME_POSITION)
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
    
    open fun getNavigationItems():Array<NavigationItem> =
            arrayOf(NavigationItem.HOME,
                    NavigationItem.ICONS,
                    NavigationItem.WALLPAPERS,
                    NavigationItem.APPLY,
                    NavigationItem.REQUESTS)
    
    internal fun getNavigationItemWithId(id:Int):NavigationItem {
        getNavigationItems().forEach { if (it.id == id) return it }
        return NavigationItem.HOME
    }
    
    internal fun updateToolbarColorsHere(offset:Int) =
            updateToolbarColors(toolbar, drawer, offset, 0.6F)
    
    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<out String>,
                                            grantResults:IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 41) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doSendRequest()
            } else {
                snackbar(R.string.permission_denied)
            }
        }
    }
    
    fun startRequestsProcess() {
        IconRequest.get()?.let {
            if (it.selectedApps.size > 0) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                object:PermissionRequestListener {
                                    override fun onPermissionRequest(permission:String) =
                                            requestPermissions(41, permission)
                    
                                    override fun showPermissionInformation(permission:String) =
                                            showPermissionInformation()
                    
                                    override fun onPermissionCompletelyDenied() {
                                        snackbar(R.string.permission_denied_completely)
                                    }
                    
                                    override fun onPermissionGranted() = doSendRequest()
                                })
            } else {
                runOnUiThread {
                    destroyDialog()
                    dialog = buildMaterialDialog {
                        title(R.string.no_selected_apps_title)
                        content(R.string.no_selected_apps_content)
                        positiveText(android.R.string.ok)
                    }
                    dialog?.show()
                }
            }
        }
    }
    
    private fun doSendRequest() {
        destroyDialog()
        dialog = buildMaterialDialog {
            content(R.string.building_request_dialog)
            progress(true, 0)
            cancelable(false)
        }
        val ir = IconRequest.get()
        if (ir != null) {
            ir.send(object:OnRequestProgress() {
                override fun doWhenStarted() {
                    runOnUiThread { dialog?.show() }
                }
                
                override fun doOnError() {
                    runOnUiThread {
                        destroyDialog()
                        dialog = buildMaterialDialog {
                            title(R.string.error_title)
                            content(R.string.requests_error)
                            positiveText(android.R.string.ok)
                        }
                        dialog?.show()
                    }
                }
                
                override fun doWhenReady() {
                    runOnUiThread {
                        destroyDialog()
                        unselectAll()
                    }
                }
            })
        } else {
            destroyDialog()
            dialog = buildMaterialDialog {
                title(R.string.error_title)
                content(R.string.requests_error)
                positiveText(android.R.string.ok)
            }
            dialog?.show()
        }
    }
    
    private fun showPermissionInformation() {
        snackbar(getString(R.string.permission_request, getAppName()), Snackbar.LENGTH_SHORT, {
            setAction(R.string.allow, {
                dismiss()
                doSendRequest()
            })
        })
    }
    
    internal fun onFiltersUpdated(filters:ArrayList<String>) {
        if (currentFragment is IconsFragment) {
            (currentFragment as IconsFragment).applyFilters(filters)
        }
    }
    
    internal fun doSearch(search:String = "") {
        when (currentFragment) {
            is IconsFragment -> (currentFragment as IconsFragment).doSearch(search)
            is WallpapersFragment -> (currentFragment as WallpapersFragment).applyFilter(search)
            is ApplyFragment -> (currentFragment as ApplyFragment).applyFilter(search)
            is RequestsFragment -> (currentFragment as RequestsFragment).applyFilter(search)
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
    
    internal fun toggleSelectAll() {
        if (currentFragment is RequestsFragment) {
            (currentFragment as RequestsFragment).toggleSelectAll()
        }
    }
    
    internal fun unselectAll() {
        if (currentFragment is RequestsFragment) {
            (currentFragment as RequestsFragment).unselectAll()
        }
    }
    
    internal fun launchHelpActivity() {
        startActivity(Intent(this, HelpActivity::class.java))
    }
    
    internal fun launchKuperActivity() {
        // TODO
        showToast(R.string.coming_soon)
    }
}