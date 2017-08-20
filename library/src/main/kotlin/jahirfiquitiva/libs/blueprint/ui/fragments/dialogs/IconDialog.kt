/*
 * Copyright (c) 2017. Jahir Fiquitiva
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
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.widget.ImageView
import ca.allanwang.kau.utils.isColorDark
import ca.allanwang.kau.utils.scaleXY
import com.afollestad.materialdialogs.DialogAction
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.ICONS_ANIMATION_DURATION
import jahirfiquitiva.libs.blueprint.helpers.utils.ICONS_ANIMATION_DURATION_DELAY
import jahirfiquitiva.libs.frames.helpers.extensions.buildMaterialDialog
import jahirfiquitiva.libs.frames.helpers.extensions.toBitmap
import jahirfiquitiva.libs.kauextensions.extensions.accentColor
import jahirfiquitiva.libs.kauextensions.extensions.bestSwatch
import jahirfiquitiva.libs.kauextensions.extensions.isColorLight
import jahirfiquitiva.libs.kauextensions.extensions.usesDarkTheme

class IconDialog:DialogFragment() {
    
    private var name:String = ""
    private var resId:Int = 0
    private var animate:Boolean = false
    
    companion object {
        private val NAME = "name"
        private val RESID = "resId"
        private val ANIMATE = "animate"
        private val TAG = "icon_dialog"
        
        fun newInstance(name:String, resId:Int, animate:Boolean):IconDialog {
            val f = IconDialog()
            val args = Bundle()
            args.putString(NAME, name)
            args.putInt(RESID, resId)
            args.putBoolean(ANIMATE, animate)
            f.arguments = args
            return f
        }
    }
    
    fun show(context:FragmentActivity, name:String, resId:Int,
             animate:Boolean) {
        val frag = context.supportFragmentManager.findFragmentByTag(TAG)
        if (frag != null) (frag as IconDialog).dismiss()
        IconDialog.newInstance(name, resId, animate).show(context.supportFragmentManager, TAG)
    }
    
    fun dismiss(context:FragmentActivity) {
        val frag = context.supportFragmentManager.findFragmentByTag(TAG)
        if (frag != null) (frag as IconDialog).dismiss()
        try {
            dismiss()
        } catch (ignored:Exception) {
        }
    }
    
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        this.name = arguments.getString(NAME)
        this.resId = arguments.getInt(RESID)
        this.animate = arguments.getBoolean(ANIMATE)
    }
    
    override fun onActivityCreated(savedInstanceState:Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let {
            name = it.getString(NAME)
            resId = it.getInt(RESID)
            animate = it.getBoolean(ANIMATE)
        }
    }
    
    override fun onCreateDialog(savedInstanceState:Bundle?):Dialog {
        val dialog = activity.buildMaterialDialog {
            title(name)
            customView(R.layout.dialog_icon, false)
            positiveText(R.string.close)
            positiveColor(activity.accentColor)
        }
        
        dialog.customView?.let {
            val iconView:ImageView = it.findViewById(R.id.dialogicon)
            with(iconView) {
                if (resId > 0) {
                    if (animate) {
                        scaleXY = 0F
                    }
                    
                    val icon = ContextCompat.getDrawable(activity, resId).toBitmap(activity)
                    setImageBitmap(icon)
                    
                    Palette.from(icon).generate(Palette.PaletteAsyncListener { palette ->
                        if (animate) {
                            animate().scaleX(1F)
                                    .scaleY(1F)
                                    .setStartDelay(ICONS_ANIMATION_DURATION_DELAY / 2)
                                    .setDuration(ICONS_ANIMATION_DURATION)
                                    .start()
                        }
                        if (palette == null) return@PaletteAsyncListener
                        
                        val iconSwatch = palette.bestSwatch ?: return@PaletteAsyncListener
                        val color = iconSwatch.rgb
                        val buttonText = dialog.getActionButton(
                                DialogAction.POSITIVE) ?: return@PaletteAsyncListener
                        
                        val correctTextColor:Int
                        correctTextColor = if (activity.usesDarkTheme) {
                            if (color.isColorLight) color
                            else activity.accentColor
                        } else {
                            if (color.isColorDark) color
                            else activity.accentColor
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
        return dialog
    }
    
    override fun onSaveInstanceState(outState:Bundle?) {
        outState?.let {
            it.putString(NAME, name)
            it.putInt(RESID, resId)
            it.putBoolean(ANIMATE, animate)
        }
        super.onSaveInstanceState(outState)
    }
    
}