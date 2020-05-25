package dev.jahir.blueprint.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.fondesa.kpermissions.PermissionStatus
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dev.jahir.blueprint.BuildConfig
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.BlueprintPreferences
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.blueprint.data.requests.RequestCallback
import dev.jahir.blueprint.data.requests.RequestState
import dev.jahir.blueprint.data.requests.RequestStateManager
import dev.jahir.blueprint.data.requests.SendIconRequest
import dev.jahir.blueprint.data.viewmodels.HomeViewModel
import dev.jahir.blueprint.data.viewmodels.IconsCategoriesViewModel
import dev.jahir.blueprint.data.viewmodels.RequestsViewModel
import dev.jahir.blueprint.extensions.ADW_ACTION
import dev.jahir.blueprint.extensions.APPLY_ACTION
import dev.jahir.blueprint.extensions.ICONS_APPLIER
import dev.jahir.blueprint.extensions.ICONS_PICKER
import dev.jahir.blueprint.extensions.IMAGE_PICKER
import dev.jahir.blueprint.extensions.NOVA_ACTION
import dev.jahir.blueprint.extensions.TURBO_ACTION
import dev.jahir.blueprint.extensions.WALLS_PICKER
import dev.jahir.blueprint.extensions.defaultLauncher
import dev.jahir.blueprint.extensions.executeLauncherIntent
import dev.jahir.blueprint.extensions.millisToText
import dev.jahir.blueprint.extensions.setup
import dev.jahir.blueprint.ui.fragments.ApplyFragment
import dev.jahir.blueprint.ui.fragments.HomeFragment
import dev.jahir.blueprint.ui.fragments.IconsCategoriesFragment
import dev.jahir.blueprint.ui.fragments.RequestFragment
import dev.jahir.blueprint.ui.fragments.dialogs.IconDialog
import dev.jahir.frames.extensions.context.boolean
import dev.jahir.frames.extensions.context.findView
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.integer
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.fragments.cancelable
import dev.jahir.frames.extensions.fragments.mdDialog
import dev.jahir.frames.extensions.fragments.message
import dev.jahir.frames.extensions.fragments.positiveButton
import dev.jahir.frames.extensions.fragments.singleChoiceItems
import dev.jahir.frames.extensions.fragments.title
import dev.jahir.frames.extensions.resources.dpToPx
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.utils.lazyViewModel
import dev.jahir.frames.extensions.utils.postDelayed
import dev.jahir.frames.extensions.views.isVisible
import dev.jahir.frames.extensions.views.setMarginBottom
import dev.jahir.frames.extensions.views.snackbar
import dev.jahir.frames.ui.activities.FramesActivity
import dev.jahir.frames.ui.fragments.CollectionsFragment
import dev.jahir.frames.ui.fragments.WallpapersFragment
import dev.jahir.kuper.data.models.Component
import dev.jahir.kuper.data.viewmodels.ComponentsViewModel
import dev.jahir.kuper.extensions.hasStoragePermission
import dev.jahir.kuper.ui.fragments.KuperWallpapersFragment
import java.util.concurrent.TimeUnit

abstract class BlueprintActivity : FramesActivity(), RequestCallback {

    open val isDebug: Boolean = BuildConfig.DEBUG

    override val collectionsFragment: CollectionsFragment? = null
    override val favoritesFragment: WallpapersFragment? = null

    override val wallpapersFragment: WallpapersFragment? by lazy {
        KuperWallpapersFragment.create(ArrayList(wallpapersViewModel.wallpapers))
    }

    var pickerKey: Int = 0
        private set
        get() {
            return intent?.let {
                when (it.action) {
                    APPLY_ACTION -> ICONS_APPLIER
                    ADW_ACTION, TURBO_ACTION, NOVA_ACTION -> ICONS_PICKER
                    Intent.ACTION_PICK, Intent.ACTION_GET_CONTENT -> IMAGE_PICKER
                    Intent.ACTION_SET_WALLPAPER -> WALLS_PICKER
                    else -> field
                }
            } ?: field
        }

    val isIconsPicker: Boolean
        get() = (pickerKey == ICONS_PICKER || pickerKey == IMAGE_PICKER || pickerKey == ICONS_APPLIER)

    open val homeFragment: HomeFragment? by lazy { HomeFragment() }
    private val iconsCategoriesFragment: IconsCategoriesFragment by lazy { IconsCategoriesFragment() }
    private val applyFragment: ApplyFragment by lazy { ApplyFragment() }
    open val requestFragment: RequestFragment? by lazy { RequestFragment() }

    open val homeViewModel: HomeViewModel? by lazyViewModel()
    private val iconsViewModel: IconsCategoriesViewModel by lazyViewModel()
    private val templatesViewModel: ComponentsViewModel by lazyViewModel()
    open val requestsViewModel: RequestsViewModel? by lazyViewModel()

    internal val fabBtn: ExtendedFloatingActionButton? by findView(R.id.fab_btn)
    private var iconDialog: IconDialog? = null
    private var shouldBuildRequest: Boolean = false

    private var requestDialog: AlertDialog? = null
    private var iconsShapePickerDialog: AlertDialog? = null
    private val blueprintPrefs: BlueprintPreferences by lazy { BlueprintPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomNavigation?.setOnNavigationItemSelectedListener {
            if (isIconsPicker && it.itemId != R.id.icons) false
            else {
                updateFab(it.itemId, true) { changeFragment(it.itemId) }
                true
            }
        }
        fabBtn?.setOnClickListener { onFabClick() }

        wallpapersViewModel.observeWallpapers(this) {
            wallpapersFragment?.updateItems(ArrayList(it))
            homeFragment?.updateWallpapersCount(it.size)
            homeViewModel?.postWallpapersCount(it.size)
        }
        templatesViewModel.observe(this) { components ->
            onTemplatesLoaded(components)
            val kustomCount =
                components.filter { it.type != Component.Type.ZOOPER && it.type != Component.Type.UNKNOWN }.size
            val zooperCount = components.filter { it.type == Component.Type.ZOOPER }.size
            homeFragment?.updateKustomCount(kustomCount)
            homeViewModel?.postKustomCount(kustomCount)
            homeFragment?.updateZooperCount(zooperCount)
            homeViewModel?.postZooperCount(zooperCount)
        }
        iconsViewModel.observe(this) {
            iconsCategoriesFragment.updateItems(it)
            homeFragment?.updateIconsCount(iconsViewModel.iconsCount)
            homeViewModel?.postIconsCount(iconsViewModel.iconsCount)
        }
        requestsViewModel?.requestsCallback = this
        requestsViewModel?.observeAppsToRequest(this) { requestFragment?.updateItems(it) }
        requestsViewModel?.observeSelectedApps(this) {
            updateFabText()
            requestFragment?.updateSelectedApps(it)
        }

        homeViewModel?.observeCounters(this, homeFragment)
        homeViewModel?.observeIconsPreviewList(this) { homeFragment?.updateIconsPreview(it) }
        homeViewModel?.observeHomeItems(this) { homeFragment?.updateHomeItems(it) }
        homeViewModel?.loadHomeItems()
        homeFragment?.showDonation(isBillingClientReady && getDonationItemsIds().isNotEmpty())

        loadIconsCategories()
        if (isIconsPicker) {
            if (currentItemId != R.id.icons) selectNavigationItem(R.id.icons)
        } else {
            loadPreviewIcons()
            templatesViewModel.loadComponents()
            loadAppsToRequest()
            requestStoragePermission()
        }
    }

    internal open fun selectNavigationItem(itemId: Int) {
        bottomNavigation?.setSelectedItemId(itemId, true)
    }

    override fun onBackPressed() {
        if (currentItemId != initialItemId) selectNavigationItem(initialItemId)
        else super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val created = super.onCreateOptionsMenu(menu)
        menu?.findItem(R.id.templates)?.isVisible = templatesViewModel.components.isNotEmpty()
        menu?.findItem(R.id.select_all)?.isVisible = currentItemId == R.id.request
        menu?.findItem(R.id.icons_shape)?.isVisible =
            currentItemId == R.id.icons && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    && boolean(R.bool.includes_adaptive_icons)
        return created
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var shouldCallSuper = true
        when (item.itemId) {
            R.id.icons_shape -> showIconsShapePickerDialog()
            R.id.select_all -> toggleSelectAll()
            R.id.templates -> startActivity(Intent(this, BlueprintKuperActivity::class.java))
            R.id.help -> startActivity(Intent(this, HelpActivity::class.java))
            R.id.settings -> {
                shouldCallSuper = false
                startActivity(Intent(this, BlueprintSettingsActivity::class.java))
            }
        }
        return if (shouldCallSuper) super.onOptionsItemSelected(item) else true
    }

    override fun onResume() {
        super.onResume()
        notifyIconShapeChanged()
        updateFab(force = true)
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissIconsShapesDialog()
        dismissRequestDialog()
        dismissIconDialog()
        homeViewModel?.destroy(this)
        iconsViewModel.destroy(this)
        templatesViewModel.destroy(this)
        requestsViewModel?.destroy(this)
    }

    private fun dismissIconDialog() {
        try {
            iconDialog?.dismiss()
            iconDialog = null
        } catch (e: Exception) {
        }
    }

    private fun dismissRequestDialog() {
        try {
            requestDialog?.dismiss()
            requestDialog = null
        } catch (e: Exception) {
        }
    }

    private fun dismissIconsShapesDialog() {
        try {
            iconsShapePickerDialog?.dismiss()
            iconsShapePickerDialog = null
        } catch (e: Exception) {
        }
    }

    override fun getToolbarTitleForItem(itemId: Int): String? =
        when (itemId) {
            R.id.home -> getAppName()
            R.id.icons -> string(R.string.icons)
            R.id.wallpapers -> string(R.string.wallpapers)
            R.id.apply -> string(R.string.apply)
            R.id.request -> string(R.string.request)
            else -> super.getToolbarTitleForItem(itemId)
        }

    override fun getNextFragment(itemId: Int): Pair<Pair<String?, Fragment?>?, Boolean>? =
        when (itemId) {
            R.id.home -> Pair(Pair(HomeFragment.TAG, homeFragment), true)
            R.id.icons -> Pair(Pair(IconsCategoriesFragment.TAG, iconsCategoriesFragment), true)
            R.id.apply -> Pair(Pair(ApplyFragment.TAG, applyFragment), true)
            R.id.request -> Pair(Pair(RequestFragment.TAG, requestFragment), true)
            else -> super.getNextFragment(itemId)
        }

    override fun internalOnPermissionsGranted(result: List<PermissionStatus>) {
        super.internalOnPermissionsGranted(result)
        homeFragment?.updateWallpaper()
        if (shouldBuildRequest) buildRequest()
    }

    override fun canShowSearch(itemId: Int): Boolean =
        when (itemId) {
            R.id.home -> false
            else -> super.canShowSearch(itemId)
        }

    override fun getSearchHint(itemId: Int): String = when (itemId) {
        R.id.icons -> string(R.string.search_icons)
        R.id.request -> string(R.string.search_apps)
        R.id.apply -> string(R.string.search_launchers)
        R.id.wallpapers -> string(R.string.search_wallpapers)
        else -> string(R.string.search_x)
    }

    override fun getLayoutRes(): Int = R.layout.activity_blueprint
    override val initialFragmentTag: String =
        if (isIconsPicker) IconsCategoriesFragment.TAG else HomeFragment.TAG
    override val initialItemId: Int = if (isIconsPicker) R.id.icons else R.id.home
    override fun getMenuRes(): Int =
        if (isIconsPicker) R.menu.toolbar_menu_simple else R.menu.blueprint_toolbar_menu

    override fun shouldLoadCollections(): Boolean = false
    override fun shouldLoadFavorites(): Boolean = false

    internal fun repostCounters() {
        homeViewModel?.repostCounters()
    }

    internal fun loadPreviewIcons(force: Boolean = false) {
        homeViewModel?.loadPreviewIcons(force)
    }

    internal fun loadIconsCategories() {
        iconsViewModel.loadIconsCategories()
    }

    private fun showIconsShapePickerDialog() {
        dismissIconsShapesDialog()
        iconsShapePickerDialog = mdDialog {
            title(R.string.icon_shape)
            singleChoiceItems(R.array.icon_shapes_options, blueprintPrefs.iconShape) { _, _ -> }
            positiveButton(android.R.string.ok) {
                val listView = (it as? AlertDialog)?.listView
                if ((listView?.checkedItemCount ?: 0) > 0) {
                    val prevOption = blueprintPrefs.iconShape
                    val selectedOption = listView?.checkedItemPosition ?: -1
                    if (selectedOption != prevOption) {
                        blueprintPrefs.iconShape = selectedOption
                        notifyIconShapeChanged()
                    }
                }
                it.dismiss()
            }
        }
        iconsShapePickerDialog?.show()
    }

    private fun notifyIconShapeChanged() {
        iconsCategoriesFragment.notifyShapeChange()
        homeFragment?.notifyShapeChange()
    }

    internal fun showIconDialog(icon: Icon?) {
        icon ?: return
        dismissIconDialog()
        iconDialog = IconDialog.create(icon)
        iconDialog?.show(this)
    }

    internal fun loadAppsToRequest() {
        requestsViewModel?.loadApps(isDebug)
    }

    internal fun changeRequestAppState(app: RequestApp, selected: Boolean): Boolean {
        return requestsViewModel?.let {
            return@let if (selected) it.selectApp(app) else it.deselectApp(app)
        } ?: false
    }


    private fun toggleSelectAll() {
        val currentState =
            RequestStateManager.getRequestState(this, requestsViewModel?.selectedApps)
        if (currentState.state == RequestState.State.TIME_LIMITED) {
            onRequestLimited(currentState)
        } else {
            if (requestsViewModel?.toggleSelectAll() == true)
                requestFragment?.updateSelectedApps(requestsViewModel?.selectedApps)
        }
    }

    private fun updateFabText(itemId: Int = currentItemId) {
        when (itemId) {
            initialItemId -> {
                val canShowText = boolean(R.bool.show_quick_apply_text, true)
                val customText = string(R.string.quick_apply_custom_text)
                val defText = string(R.string.quick_apply)
                fabBtn?.setup(
                    if (customText.hasContent()) customText else defText,
                    R.drawable.ic_apply,
                    defaultLauncher != null,
                    !canShowText
                )
            }
            R.id.request -> {
                val selectedAppsCount = requestsViewModel?.selectedApps?.size ?: 0
                fabBtn?.setup(
                    string(
                        when (selectedAppsCount) {
                            0 -> R.string.request_none
                            1 -> R.string.request_icon
                            else -> R.string.request_x_icons
                        }, selectedAppsCount
                    ),
                    R.drawable.ic_send_request,
                    selectedAppsCount > 0
                )
            }
            else -> fabBtn?.hide()
        }
        bottomNavigation?.post {
            fabBtn?.setMarginBottom((bottomNavigation?.measuredHeight ?: 0) + 16.dpToPx)
        }
        homeFragment?.setupContentBottomOffset()
        iconsCategoriesFragment.setupContentBottomOffset()
        requestFragment?.setupContentBottomOffset()
    }

    internal fun updateFab(
        itemId: Int = currentItemId,
        force: Boolean = false,
        afterHidden: () -> Unit = {}
    ) {
        if (itemId != currentItemId || force) {
            if (fabBtn?.isVisible == true) {
                fabBtn?.hide(object : ExtendedFloatingActionButton.OnChangedCallback() {
                    override fun onHidden(extendedFab: ExtendedFloatingActionButton?) {
                        super.onHidden(extendedFab)
                        updateFabText(itemId)
                    }
                })
            } else updateFabText(itemId)
        }
        afterHidden()
    }

    private fun onFabClick() {
        when (currentItemId) {
            initialItemId -> executeLauncherIntent(defaultLauncher)
            R.id.request -> {
                shouldBuildRequest = true
                requestStoragePermission()
            }
        }
    }

    private fun buildRequest() {
        shouldBuildRequest = false
        if (!hasStoragePermission) {
            snackbar(R.string.permission_denied, Snackbar.LENGTH_LONG, snackbarAnchorId)
            return
        }
        SendIconRequest.sendIconRequest(this, requestsViewModel?.selectedApps, this)
    }

    override fun onBillingClientReady() {
        super.onBillingClientReady()
        homeFragment?.showDonation(isBillingClientReady && getDonationItemsIds().isNotEmpty())
    }

    override val snackbarAnchorId: Int
        get() = if (fabBtn?.isVisible == true) R.id.fab_btn else R.id.bottom_navigation

    private fun showRequestDialog(
        showOK: Boolean = true,
        options: MaterialAlertDialogBuilder.() -> MaterialAlertDialogBuilder
    ) {
        dismissRequestDialog()
        requestDialog = mdDialog {
            options().apply {
                if (showOK) positiveButton(android.R.string.ok) { it.dismiss() }
            }
        }
        requestDialog?.show()
    }

    override fun onRequestRunning() {
        super.onRequestRunning()
        showRequestDialog {
            title(R.string.request_in_progress)
            message(R.string.request_in_progress_content)
        }
    }

    override fun onRequestStarted() {
        super.onRequestStarted()
        showRequestDialog {
            message(R.string.building_request_dialog)
            cancelable(false)
        }
    }

    override fun onRequestEmpty() {
        super.onRequestEmpty()
        showRequestDialog {
            title(R.string.no_selected_apps)
            message(R.string.no_selected_apps_content)
        }
    }

    override fun onRequestError(reason: String?, e: Throwable?) {
        super.onRequestError(reason, e)
        reason ?: return
        if (!reason.hasContent()) return
        showRequestDialog {
            message(string(R.string.requests_upload_error, reason))
        }
    }

    override fun onRequestLimited(state: RequestState, building: Boolean) {
        super.onRequestLimited(state, building)
        val content = if (state.state == RequestState.State.TIME_LIMITED) {
            val preContent = string(
                R.string.apps_limit_dialog_day,
                millisToText(TimeUnit.MINUTES.toMillis(integer(R.integer.time_limit_in_minutes).toLong()))
            )

            val contentExtra = when {
                TimeUnit.MILLISECONDS.toSeconds(state.timeLeft) >= 60 ->
                    string(R.string.apps_limit_dialog_day_extra, millisToText(state.timeLeft))
                else -> string(R.string.apps_limit_dialog_day_extra_sec)
            }
            "$preContent $contentExtra"
        } else {
            val maxAppsToRequest = integer(R.integer.max_apps_to_request, -1)
            when (val requestsLeft = state.requestsLeft) {
                maxAppsToRequest, -1 ->
                    string(R.string.apps_limit_dialog, maxAppsToRequest.toString())
                else -> string(R.string.apps_limit_dialog_more, requestsLeft.toString())
            }
        }
        if (!content.hasContent()) return
        postDelayed(25) {
            showRequestDialog {
                title(R.string.request)
                message(content)
            }
        }
    }

    override fun onRequestUploadFinished(success: Boolean) {
        super.onRequestUploadFinished(success)
        requestsViewModel?.deselectAll()
        showRequestDialog {
            message(R.string.request_upload_success_content)
        }
    }

    override fun onRequestEmailIntent(intent: Intent?) {
        super.onRequestEmailIntent(intent)
        requestsViewModel?.deselectAll()
        dismissRequestDialog()
        startActivity(intent)
    }

    open fun onTemplatesLoaded(templates: ArrayList<Component>) {
        invalidateOptionsMenu()
    }

    override fun defaultTheme(): Int = R.style.Blueprint_Default
    override fun amoledTheme(): Int = R.style.Blueprint_Default_Amoled

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("pickerKey", pickerKey)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        pickerKey = savedInstanceState.getInt("pickerKey", 0)
    }
}
