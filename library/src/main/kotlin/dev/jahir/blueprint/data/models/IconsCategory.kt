package dev.jahir.blueprint.data.models

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import dev.jahir.blueprint.R
import dev.jahir.blueprint.extensions.createParcel
import dev.jahir.frames.extensions.context.integer

@Suppress("unused")
data class IconsCategory(
    val title: String,
    private val icons: ArrayList<Icon> = ArrayList(),
    val showCount: Boolean = true
) : Parcelable {

    val count: Int
        get() = icons.size

    constructor(parcelIn: Parcel) : this(
        parcelIn.readString().orEmpty(),
        ArrayList<Icon>().apply { parcelIn.readTypedList(this, Icon.CREATOR) }
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(title)
        dest?.writeTypedList(icons)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<IconsCategory> = createParcel { IconsCategory(it) }
    }

    fun setIcons(newIcons: ArrayList<Icon>) {
        icons.clear()
        icons.addAll(newIcons)
    }

    fun getIcons(): ArrayList<Icon> = ArrayList(icons.distinctBy { it.resId })

    fun hasIcons(): Boolean = icons.isNotEmpty()

    fun addIcon(icon: Icon) {
        icons.add(icon)
    }

    fun getIconsForPreview(context: Context? = null, shuffle: Boolean = false): ArrayList<Icon> {
        val expectedIcons =
            context?.integer(R.integer.icons_columns_count, icons.size) ?: icons.size
        val maxSize = if (icons.size <= expectedIcons) icons.size else expectedIcons
        val subList = icons.subList(0, maxSize)
        return ArrayList(if (shuffle) subList.shuffled() else subList)
    }
}