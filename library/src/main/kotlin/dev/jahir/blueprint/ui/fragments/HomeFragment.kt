package dev.jahir.blueprint.ui.fragments

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.listeners.HomeItemsListener
import dev.jahir.blueprint.data.models.Counter
import dev.jahir.blueprint.data.models.HomeItem
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.data.models.IconsCounter
import dev.jahir.blueprint.data.models.KustomCounter
import dev.jahir.blueprint.data.models.WallpapersCounter
import dev.jahir.blueprint.data.models.ZooperCounter
import dev.jahir.blueprint.extensions.defaultLauncher
import dev.jahir.blueprint.ui.activities.BlueprintActivity
import dev.jahir.blueprint.ui.activities.BlueprintKuperActivity
import dev.jahir.blueprint.ui.adapters.HomeAdapter
import dev.jahir.blueprint.ui.decorations.HomeGridSpacingItemDecoration
import dev.jahir.frames.extensions.context.boolean
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.integer
import dev.jahir.frames.extensions.context.openLink
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.context.toast
import dev.jahir.frames.extensions.resources.dpToPx
import dev.jahir.frames.extensions.views.setPaddingBottom
import dev.jahir.frames.ui.activities.base.BaseBillingActivity
import dev.jahir.frames.ui.activities.base.BaseLicenseCheckerActivity.Companion.PLAY_STORE_LINK_PREFIX
import dev.jahir.frames.ui.activities.base.BaseSystemUIVisibilityActivity
import dev.jahir.frames.ui.widgets.StatefulRecyclerView
import dev.jahir.kuper.extensions.hasStoragePermission


@SuppressLint("MissingPermission")
class HomeFragment : Fragment(R.layout.fragment_home), HomeItemsListener {

    private val wallpaper: Drawable?
        get() = activity?.let {
            try {
                val wm = WallpaperManager.getInstance(it)
                if (it.hasStoragePermission) wm?.fastDrawable else null
            } catch (e: Exception) {
                null
            }
        }

    private val staticWallpaper: Drawable?
        get() = activity?.let {
            try {
                it.drawable(it.string(R.string.static_icons_preview_picture))
            } catch (e: Exception) {
                null
            }
        }

    private val rightWallpaper: Drawable?
        get() = activity?.let {
            if (it.boolean(R.bool.static_icons_preview_picture_by_default))
                staticWallpaper ?: wallpaper
            else wallpaper ?: staticWallpaper
        }

    private val adapter: HomeAdapter by lazy {
        HomeAdapter(
            context?.integer(R.integer.home_actions_style, 1) ?: 1,
            context?.boolean(R.bool.show_overview, true) == true, this
        )
    }

    private var recyclerView: StatefulRecyclerView? = null

    private val fabHeight: Int
        get() {
            (activity as? BlueprintActivity)?.let {
                return if (it.initialItemId == R.id.home && it.defaultLauncher != null)
                    it.fabBtn?.measuredHeight ?: 0
                else 0
            } ?: return 0
        }

    private val extraHeight: Int
        get() = if (fabHeight > 0) 16.dpToPx else 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setupContentBottomOffset()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        setupContentBottomOffset(view)
        recyclerView?.loading = false
        recyclerView?.setFastScrollEnabled(false)
        val columnsCount = 2
        val layoutManager =
            GridLayoutManager(context, columnsCount, GridLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = layoutManager

        adapter.setLayoutManager(layoutManager)
        adapter.wallpaper = rightWallpaper
        adapter.actionsStyle = context?.integer(R.integer.home_actions_style, 1) ?: 1
        adapter.showOverview = context?.boolean(R.bool.show_overview, true) == true
        recyclerView?.adapter = adapter
        recyclerView?.addItemDecoration(HomeGridSpacingItemDecoration(columnsCount, 8.dpToPx))
        (activity as? BlueprintActivity)?.repostCounters()
    }

    internal fun setupContentBottomOffset(view: View? = null) {
        (view ?: getView())?.let { v ->
            v.post {
                val bottomNavigationHeight =
                    (context as? BaseSystemUIVisibilityActivity<*>)?.bottomNavigation?.measuredHeight
                        ?: 0
                v.setPaddingBottom(bottomNavigationHeight + fabHeight + extraHeight)
            }
        }
    }

    internal fun notifyShapeChange() {
        adapter.notifyDataSetChanged()
        setupContentBottomOffset()
    }

    internal fun updateIconsPreview(icons: List<Icon>) {
        adapter.iconsPreviewList = ArrayList(icons)
        setupContentBottomOffset()
    }

    internal fun updateHomeItems(items: List<HomeItem>) {
        adapter.homeItems = ArrayList(items)
        setupContentBottomOffset()
    }

    internal fun updateWallpaper() {
        adapter.wallpaper = rightWallpaper
        setupContentBottomOffset()
    }

    internal fun updateIconsCount(count: Int) {
        adapter.iconsCount = count
        setupContentBottomOffset()
    }

    internal fun updateWallpapersCount(count: Int) {
        adapter.wallpapersCount = count
        setupContentBottomOffset()
    }

    internal fun updateKustomCount(count: Int) {
        adapter.kustomCount = count
        setupContentBottomOffset()
    }

    internal fun updateZooperCount(count: Int) {
        adapter.zooperCount = count
        setupContentBottomOffset()
    }

    override fun onIconsPreviewClicked() {
        super.onIconsPreviewClicked()
        (activity as? BlueprintActivity)?.loadPreviewIcons(true)
    }

    override fun onCounterClicked(counter: Counter) {
        super.onCounterClicked(counter)
        when (counter) {
            is IconsCounter -> {
                (activity as? BlueprintActivity)?.selectNavigationItem(R.id.icons)
            }
            is WallpapersCounter -> {
                (activity as? BlueprintActivity)?.selectNavigationItem(R.id.wallpapers)
            }
            is KustomCounter, is ZooperCounter -> {
                (activity as? BlueprintActivity)?.let {
                    it.startActivity(Intent(it, BlueprintKuperActivity::class.java))
                }
            }
        }
    }

    override fun onAppLinkClicked(url: String, intent: Intent?) {
        super.onAppLinkClicked(url, intent)
        intent?.let { activity?.startActivity(it) } ?: { context?.openLink(url) }()
    }

    internal fun showDonation(show: Boolean) {
        adapter.showDonateButton = show
    }

    override fun onShareClicked() {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    context?.string(
                        R.string.share_text,
                        context?.getAppName(),
                        PLAY_STORE_LINK_PREFIX + context?.packageName.orEmpty()
                    )
                )
            }
            context?.startActivity(
                Intent.createChooser(
                    intent,
                    context?.string(R.string.share).orEmpty()
                )
            )
        } catch (e: Exception) {
            context?.toast(R.string.error)
        }
    }

    override fun onDonateClicked() {
        (activity as? BaseBillingActivity<*>)?.showDonationsDialog()
    }

    companion object {
        const val TAG = "home_fragment"
    }
}