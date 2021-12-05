package dev.jahir.blueprint.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArcticResponse(val status: String? = null, val error: String? = null) : Parcelable
