package dev.jahir.blueprint.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.view.postDelayed
import com.google.android.material.card.MaterialCardView

class RequestCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    init {
        isClickable = true
        isFocusable = true
        isCheckable = true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun performClick(): Boolean {
        postDelayed(10) { toggle() }
        return true
    }
}