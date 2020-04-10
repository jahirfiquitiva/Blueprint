package dev.jahir.blueprint.ui.fragments

import dev.jahir.frames.data.models.Wallpaper
import dev.jahir.frames.ui.fragments.WallpapersFragment

class BlueprintWallpapersFragment : WallpapersFragment() {
    override val canShowFavoritesButton: Boolean = false

    companion object {
        @JvmStatic
        fun create(list: ArrayList<Wallpaper> = ArrayList()) = BlueprintWallpapersFragment().apply {
            this.isForFavs = false
            notifyCanModifyFavorites(false)
            updateItemsInAdapter(list)
        }
    }
}