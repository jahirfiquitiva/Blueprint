package dev.jahir.blueprint.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Launcher
import dev.jahir.blueprint.extensions.executeLauncherIntent
import dev.jahir.blueprint.extensions.showLauncherNotInstalledDialog
import dev.jahir.blueprint.ui.adapters.LaunchersAdapter
import dev.jahir.blueprint.ui.decorations.GridDividerItemDecoration
import dev.jahir.frames.extensions.context.integer
import dev.jahir.frames.extensions.resources.lower
import dev.jahir.frames.ui.fragments.base.BaseFramesFragment

class ApplyFragment : BaseFramesFragment<Pair<Launcher, Boolean>>() {

    private val launchersAdapter: LaunchersAdapter by lazy {
        LaunchersAdapter(context, ::onLauncherClicked)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val columnsCount = context?.integer(R.integer.launchers_columns_count, 3) ?: 3
        recyclerView?.layoutManager =
            GridLayoutManager(context, columnsCount, GridLayoutManager.VERTICAL, false)
        recyclerView?.addItemDecoration(
            GridDividerItemDecoration(context, GridLayoutManager.VERTICAL)
        )
        recyclerView?.addItemDecoration(
            GridDividerItemDecoration(context, GridLayoutManager.HORIZONTAL)
        )
        recyclerView?.adapter = launchersAdapter
        recyclerView?.setHasFixedSize(true)
        updateItems(Launcher.getSupportedLaunchers(context))
    }

    override fun getFilteredItems(
        originalItems: ArrayList<Pair<Launcher, Boolean>>,
        filter: String
    ): ArrayList<Pair<Launcher, Boolean>> =
        ArrayList(
            originalItems
                .filter { it.first.cleanAppName.lower().contains(filter.lower()) }
                .sortedByDescending { it.second })

    override fun loadData() {
        updateItems(Launcher.getSupportedLaunchers(context))
    }

    override fun updateItemsInAdapter(items: ArrayList<Pair<Launcher, Boolean>>) {
        launchersAdapter.launchers = items
    }

    private fun onLauncherClicked(launcher: Launcher, installed: Boolean) {
        if (installed) context?.executeLauncherIntent(launcher)
        else context?.showLauncherNotInstalledDialog(launcher)
    }

    companion object {
        const val TAG = "apply_fragment"
    }
}