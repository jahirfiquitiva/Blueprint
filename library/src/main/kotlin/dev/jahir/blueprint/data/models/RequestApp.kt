package dev.jahir.blueprint.data.models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import dev.jahir.blueprint.extensions.getAppIcon
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestApp(val name: String, val packageName: String, val component: String) :
    Parcelable {
    @IgnoredOnParcel
    var icon: Drawable? = null
        private set

    suspend fun loadIcon(context: Context?) {
        context ?: return
        if (icon != null) return
        icon = context.getAppIcon(packageName)
    }
}