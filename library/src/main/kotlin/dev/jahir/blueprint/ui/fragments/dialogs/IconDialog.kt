package dev.jahir.blueprint.ui.fragments.dialogs

import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.palette.graphics.Palette
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.extensions.asAdaptive
import dev.jahir.blueprint.ui.viewholders.IconViewHolder.Companion.ICON_ANIMATION_DELAY
import dev.jahir.blueprint.ui.viewholders.IconViewHolder.Companion.ICON_ANIMATION_DURATION
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.fragments.mdDialog
import dev.jahir.frames.extensions.fragments.positiveButton
import dev.jahir.frames.extensions.fragments.preferences
import dev.jahir.frames.extensions.fragments.title
import dev.jahir.frames.extensions.fragments.view
import dev.jahir.frames.extensions.resources.asBitmap
import dev.jahir.frames.extensions.resources.luminance
import dev.jahir.frames.extensions.utils.bestSwatch

class IconDialog : DialogFragment() {

    private var icon: Icon? = null
    private var dialog: AlertDialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        dialog = requireContext().mdDialog {
            title(icon?.name ?: context.getAppName())
            view(R.layout.item_dialog_icon)
            positiveButton(R.string.close) { dismiss() }
        }
        dialog?.setOnShowListener { onDialogShown() }
        return dialog!!
    }

    private fun onDialogShown() {
        icon?.let { icon ->
            val bitmap = try {
                context?.drawable(icon.resId)?.asAdaptive(context)?.first?.asBitmap()
            } catch (e: Exception) {
                null
            }
            bitmap?.let {
                setIconBitmap(bitmap)
                try {
                    Palette.from(it)
                        .generate { palette ->
                            setButtonColor(palette?.bestSwatch)
                        }
                } catch (e: Exception) {
                }
            } ?: { dismiss() }()
        }
    }

    private fun setIconBitmap(bitmap: Bitmap?) {
        bitmap ?: return
        val iconView: AppCompatImageView? = dialog?.findViewById(R.id.icon)
        iconView ?: return
        iconView.apply {
            scaleX = 0F
            scaleY = 0F
            alpha = 0F
            setImageBitmap(bitmap)
            if (preferences.animationsEnabled) {
                animate().scaleX(1F)
                    .scaleY(1F)
                    .alpha(1F)
                    .setStartDelay(ICON_ANIMATION_DELAY)
                    .setDuration(ICON_ANIMATION_DURATION)
                    .start()
            } else {
                scaleX = 1F
                scaleY = 1F
                alpha = 1F
            }
        }
    }

    private fun setButtonColor(swatch: Palette.Swatch? = null) {
        swatch ?: return
        val currentNightMode = try {
            context?.let {
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            } ?: Configuration.UI_MODE_NIGHT_UNDEFINED
        } catch (e: Exception) {
            Configuration.UI_MODE_NIGHT_UNDEFINED
        }
        val isDarkTheme = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        val bestColor = swatch.rgb
        if (!isDarkTheme && bestColor.luminance > (LUMINANCE_THRESHOLD - .1F)) return
        if (isDarkTheme && bestColor.luminance < (LUMINANCE_THRESHOLD + .1F)) return
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(bestColor)
    }

    fun show(fragmentActivity: FragmentActivity) {
        show(fragmentActivity.supportFragmentManager, TAG)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, TAG)
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int =
        super.show(transaction, TAG)

    companion object {
        private const val TAG = "icon_dialog_fragment"
        private const val LUMINANCE_THRESHOLD = .35F
        fun create(icon: Icon?) = IconDialog().apply { this.icon = icon }
    }
}