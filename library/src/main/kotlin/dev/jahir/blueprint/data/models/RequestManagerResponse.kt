package dev.jahir.blueprint.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestManagerResponse(val status: String? = null, val message: String? = null) :
    Parcelable
