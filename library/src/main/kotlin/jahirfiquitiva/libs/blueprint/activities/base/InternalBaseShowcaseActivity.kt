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
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.konifar.fab_transformation.FabTransformation
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.extensions.*
import jahirfiquitiva.libs.blueprint.fragments.EmptyFragment
import jahirfiquitiva.libs.blueprint.fragments.HomeFragment
import jahirfiquitiva.libs.blueprint.fragments.IconsFragment
import jahirfiquitiva.libs.blueprint.holders.FilterCheckBoxHolder
import jahirfiquitiva.libs.blueprint.models.NavigationItem
import jahirfiquitiva.libs.blueprint.ui.layouts.CustomCoordinatorLayout
import jahirfiquitiva.libs.blueprint.ui.layouts.FixedElevationAppBarLayout
import jahirfiquitiva.libs.blueprint.ui.views.CounterFab
import jahirfiquitiva.libs.blueprint.ui.views.FilterDrawerItem
import jahirfiquitiva.libs.blueprint.ui.views.FilterTitleDrawerItem
import jahirfiquitiva.libs.blueprint.ui.views.callbacks.CollapsingToolbarCallback
import jahirfiquitiva.libs.blueprint.utils.CoreUtils
import jahirfiquitiva.libs.blueprint.utils.IconUtils
import jahirfiquitiva.libs.blueprint.utils.NetworkUtils
import jahirfiquitiva.libs.blueprint.utils.ResourceUtils
import jahirfiquitiva.libs.blueprint.utils.changeOptionVisibility

open class InternalBaseShowcaseActivity:BaseShowcaseActivity() {

    private var coordinatorLayout:CustomCoordinatorLayout? = null
    private var appBarLayout:FixedElevationAppBarLayout? = null
    private var collapsingToolbar:CollapsingToolbarLayout? = null
    private var tabs:TabLayout? = null
    private var toolbar:Toolbar? = null
    private var menu:Menu? = null
    private var fab:CounterFab? = null
    private var overlay:View? = null
    private var sheet:View? = null
    private var filtersDrawer:Drawer? = null
    private var iconsFilters:ArrayList<String> = ArrayList()
    internal var currentItemId = -1

    var filtersListener:FiltersListener? = null

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
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
        fab = findViewById(R.id.fab)
        overlay = findViewById(R.id.overlay)
        overlay?.background = ColorDrawable(getOverlayColor(usesDarkTheme))
        overlay?.setOnClickListener { _ ->
            if (currentItemId == 0)
                FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet)
        }
        sheet = findViewById(R.id.sheet)
        val rateText:TextView = findViewById(R.id.action_rate)
        rateText.setCompoundDrawables(
                "ic_rate".getDrawable(this).tintWithColor(getActiveIconsColor(usesDarkTheme)),
                null, null, null)
        rateText.setOnClickListener {
            NetworkUtils.openLink(this,
                                  NetworkUtils.PLAY_STORE_LINK_PREFIX + CoreUtils.getAppPackageName(
                                          this))
        }
        val shareText:TextView = findViewById(R.id.action_share)
        shareText.setCompoundDrawables(
                "ic_rate".getDrawable(this).tintWithColor(getActiveIconsColor(usesDarkTheme)),
                null, null, null)
        val donateText:TextView = findViewById(R.id.action_donate)
        if (donationsEnabled()) {
            donateText.setCompoundDrawables(
                    "ic_rate".getDrawable(this).tintWithColor(getActiveIconsColor(usesDarkTheme)),
                    null, null, null)
        } else {
            donateText.visibility = View.GONE
        }
        val helpText:TextView = findViewById(R.id.action_help)
        helpText.setCompoundDrawables(
                "ic_questions".getDrawable(this).tintWithColor(getActiveIconsColor(usesDarkTheme)),
                null, null, null)
    }

    open fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        menu = toolbar?.menu
        menuInflater.inflate(R.menu.menu_main, menu)
        toolbar?.let {
            tintToolbar(it, getActiveIconsColorFor(getPrimaryColor(usesDarkTheme)))
        }
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
        coordinatorLayout = findViewById(R.id.mainCoordinatorLayout)
        appBarLayout = findViewById(R.id.appBar)
        collapsingToolbar = findViewById(R.id.collapsingToolbar)
        collapsingToolbar?.setExpandedTitleColor(Color.TRANSPARENT)
        collapsingToolbar?.setCollapsedTitleTextColor(getPrimaryTextColor(usesDarkTheme))
        appBarLayout?.addOnOffsetChangedListener(object:CollapsingToolbarCallback() {
            override fun onVerticalOffsetChanged(appBar:AppBarLayout?, verticalOffset:Int) {
                toolbar?.let { updateToolbarColorsHere(it, verticalOffset) }
            }
        })
        val wallpaper:ImageView? = findViewById(R.id.toolbarHeader)
        val wallManager = WallpaperManager.getInstance(this)
        if (getPickerKey() == 0 && getShortcut().isEmpty()) {
            var drawable:Drawable? = null
            if (konfigs.wallpaperAsToolbarHeaderEnabled) {
                drawable = wallManager?.fastDrawable
            } else {
                val picName = ResourceUtils.getString(this, R.string.toolbar_picture)
                if (picName.isNotEmpty()) {
                    try {
                        drawable = picName.getDrawable(this)
                    } catch (ignored:Exception) {
                    }
                }
            }
            wallpaper?.alpha = .95f
            wallpaper?.setImageDrawable(drawable)
            wallpaper?.visibility = View.VISIBLE
            if (wallpaper == null) {
                val gradient:ImageView? = findViewById(R.id.toolbarGradient)
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
            changeFABVisibility(
                    id == NavigationItem.DEFAULT_HOME_POSITION || id == NavigationItem.DEFAULT_REQUEST_POSITION)
            changeFABAction(id == NavigationItem.DEFAULT_HOME_POSITION)
            appBarLayout?.setExpanded(id == NavigationItem.DEFAULT_HOME_POSITION,
                                      konfigs.animationsEnabled)
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
        // ToolbarThemer.tintToolbarMenu(toolbar, menu, getActiveIconsColorFor(primaryColor))
    }

    fun changeFABVisibility(visible:Boolean) = if (visible) fab?.show() else fab?.hide()

    private fun lockFiltersDrawer(lock:Boolean) {
        val drawerLayout = filtersDrawer?.drawerLayout
        drawerLayout?.setDrawerLockMode(
                if (lock) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.END)
    }

    private fun changeFABAction(home:Boolean) {
        /*
        fab?.setImageDrawable(TintUtils.createTintedDrawable(
                IconUtils.getDrawableWithName(this, if (home) "ic_plus" else "ic_send"),
                getActiveIconsColorFor(accentColor)))
                */
        if (home) {
            fab?.count = 0
            fab?.setOnClickListener({
                                        FabTransformation.with(fab).setOverlay(overlay).transformTo(
                                                sheet)
                                    })
        } else {
            fab?.setOnClickListener({ _ ->
                                        showToast("Creating request")
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

    fun getToolbar():Toolbar? = toolbar

    fun updateToolbarColorsHere(toolbar:Toolbar, offset:Int) = updateToolbarColors(toolbar, offset)

    interface FiltersListener {
        fun onFiltersUpdated(filters:ArrayList<String>)
    }
}