package dev.jahir.blueprint.data.models

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class HomeMenuItem (
    @IdRes val id: Int,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
)
