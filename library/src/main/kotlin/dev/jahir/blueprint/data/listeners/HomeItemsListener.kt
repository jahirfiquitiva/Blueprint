package dev.jahir.blueprint.data.listeners

import android.content.Intent
import dev.jahir.blueprint.data.models.Counter

interface HomeItemsListener {
    fun onIconsPreviewClicked() {}
    fun onCounterClicked(counter: Counter) {}
    fun onAppLinkClicked(url: String = "", intent: Intent? = null) {}
    fun onShareClicked() {}
    fun onDonateClicked() {}
}