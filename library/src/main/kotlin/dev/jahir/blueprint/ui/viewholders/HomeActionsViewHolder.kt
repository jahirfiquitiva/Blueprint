package dev.jahir.blueprint.ui.viewholders

import android.view.View
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import com.google.android.material.button.MaterialButton
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.listeners.HomeItemsListener
import dev.jahir.frames.extensions.context.openLink
import dev.jahir.frames.extensions.context.preferences
import dev.jahir.frames.extensions.views.context
import dev.jahir.frames.extensions.views.findView
import dev.jahir.frames.extensions.views.gone
import dev.jahir.frames.extensions.views.visible
import dev.jahir.frames.ui.activities.base.BaseLicenseCheckerActivity.Companion.PLAY_STORE_LINK_PREFIX

class HomeActionsViewHolder(itemView: View) : SectionedViewHolder(itemView) {
    private val shareBtn: View? by itemView.findView(R.id.share_btn)
    private val rateBtn: View? by itemView.findView(R.id.rate_btn)
    private val donateBtn: View? by itemView.findView(R.id.donate_btn)

    fun bind(showDonate: Boolean, listener: HomeItemsListener? = null) {
        (shareBtn as? MaterialButton)?.setSupportAllCaps(!context.preferences.useMaterialYou)
        (rateBtn as? MaterialButton)?.setSupportAllCaps(!context.preferences.useMaterialYou)
        (donateBtn as? MaterialButton)?.setSupportAllCaps(!context.preferences.useMaterialYou)

        shareBtn?.setOnClickListener { listener?.onShareClicked() }
        rateBtn?.setOnClickListener { context.openLink(PLAY_STORE_LINK_PREFIX + context.packageName) }
        if (showDonate) {
            donateBtn?.visible()
            donateBtn?.setOnClickListener { listener?.onDonateClicked() }
        } else donateBtn?.gone()
    }
}
