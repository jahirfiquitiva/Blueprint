package dev.jahir.blueprint.data.models

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

data class HomeItem(
    val title: String, val description: String, val url: String,
    val icon: Drawable?, @DrawableRes val openIcon: Int?,
    val isAnApp: Boolean, val isInstalled: Boolean,
    val intent: Intent?
)