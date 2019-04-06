/*
 * Copyright (c) 2018. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jahirfiquitiva.libs.blueprint.ui.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.palette.graphics.Palette
import ca.allanwang.kau.utils.isColorDark
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.customview.customView
import jahirfiquitiva.libs.archhelpers.extensions.mdDialog
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.ICONS_ANIMATION_DURATION
import jahirfiquitiva.libs.blueprint.helpers.utils.ICONS_ANIMATION_DURATION_DELAY
import jahirfiquitiva.libs.kext.extensions.accentColor
import jahirfiquitiva.libs.kext.extensions.actv
import jahirfiquitiva.libs.kext.extensions.bestSwatch
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.customView
import jahirfiquitiva.libs.kext.extensions.isColorLight
import jahirfiquitiva.libs.kext.extensions.toBitmap
import jahirfiquitiva.libs.kext.extensions.usesDarkTheme

@Suppress("DEPRECATION")
class IconDialog : BasicDialogFragment() {
    
    private var name: String = ""
    private var resId: Int = 0
    private var animate: Boolean = false
    
    companion object {
        private const val NAME = "name"
        private const val RES_ID = "resId"
        private const val ANIMATE = "animate"
        const val TAG = "icon_dialog"
        
        fun invoke(name: String, resId: Int, animate: Boolean): IconDialog {
            return IconDialog().apply {
                this.name = name
                this.resId = resId
                this.animate = animate
            }
        }
    }
    
    fun show(activity: FragmentActivity, name: String, resId: Int, animate: Boolean) {
        dismiss(activity, TAG)
        IconDialog.invoke(name, resId, animate).show(activity.supportFragmentManager, TAG)
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = actv.mdDialog {
            title(text = name)
            customView(R.layout.dialog_icon)
            positiveButton(R.string.close)
        }
        
        dialog.customView?.let {
            val iconView: ImageView? by it.bind(R.id.dialogicon)
            iconView?.let {
                with(it) {
                    if (resId > 0) {
                        if (animate) {
                            scaleX = 0F
                            scaleY = 0F
                            alpha = 0F
                        }
                        
                        val icon = ContextCompat.getDrawable(actv, resId)?.toBitmap()
                        setImageBitmap(icon)
                        
                        icon?.let {
                            Palette.from(it).generate(
                                Palette.PaletteAsyncListener { palette ->
                                    if (animate) {
                                        animate().scaleX(1F)
                                            .scaleY(1F)
                                            .alpha(1F)
                                            .setStartDelay(
                                                ICONS_ANIMATION_DURATION_DELAY / 2)
                                            .setDuration(ICONS_ANIMATION_DURATION)
                                            .start()
                                    }
                                    
                                    val iconSwatch =
                                        palette?.bestSwatch ?: return@PaletteAsyncListener
                                    val color = iconSwatch.rgb
                                    val buttonText = dialog.getActionButton(WhichButton.POSITIVE)
                                    
                                    val correctTextColor: Int
                                    correctTextColor = if (actv.usesDarkTheme) {
                                        if (color.isColorLight) color
                                        else actv.accentColor
                                    } else {
                                        if (color.isColorDark) color
                                        else actv.accentColor
                                    }
                                    
                                    if (correctTextColor != 0) {
                                        if (animate) {
                                            buttonText.alpha = 0F
                                            buttonText.setTextColor(correctTextColor)
                                            buttonText.animate().alpha(1F).setDuration(
                                                ICONS_ANIMATION_DURATION).start()
                                        } else {
                                            buttonText.setTextColor(correctTextColor)
                                        }
                                    }
                                })
                        }
                    }
                }
            }
        }
        return dialog
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let {
            this.name = it.getString(NAME) ?: ""
            this.resId = it.getInt(RES_ID)
            this.animate = it.getBoolean(ANIMATE)
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        with(outState) {
            putString(NAME, name)
            putInt(RES_ID, resId)
            putBoolean(ANIMATE, animate)
        }
        super.onSaveInstanceState(outState)
    }
}
