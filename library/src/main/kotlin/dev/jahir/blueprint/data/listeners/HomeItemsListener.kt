package dev.jahir.blueprint.data.listeners

import dev.jahir.blueprint.data.models.Counter

interface HomeItemsListener {
    fun onIconsPreviewClicked() {}
    fun onCounterClicked(counter: Counter) {}
}