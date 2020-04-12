package dev.jahir.blueprint.data.models

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import dev.jahir.blueprint.extensions.createParcel

data class Icon(val name: String, @DrawableRes val resId: Int) : Parcelable, Comparable<Icon> {

    constructor(parcelIn: Parcel) : this(
        parcelIn.readString().orEmpty(),
        parcelIn.readInt()
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(name)
        dest?.writeInt(resId)
    }

    override fun describeContents(): Int = 0
    override fun compareTo(other: Icon): Int = name.compareTo(other.name)

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Icon> = createParcel { Icon(it) }
    }
}