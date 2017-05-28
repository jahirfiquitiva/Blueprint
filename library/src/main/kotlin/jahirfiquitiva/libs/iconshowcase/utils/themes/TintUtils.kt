/*
 * Copyright (c) 2017.  Jahir Fiquitiva
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
 *
 * Special thanks to the project contributors and collaborators
 *   https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.utils.themes

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.support.v13.view.ViewCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.TintableBackgroundView
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.widget.*
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils

/**
 * @author Aidan Follestad (afollestad)
 */
object TintUtils {
    // This returns a NEW Drawable because of the mutate() call. The mutate() call is necessary
    // because Drawables with the same resource have shared states otherwise.
    @CheckResult
    fun createTintedDrawable(drawable:Drawable?, @ColorInt color:Int):Drawable? {
        if (drawable == null) return null
        val nDrawable = DrawableCompat.wrap(drawable.mutate())
        DrawableCompat.setTintMode(nDrawable, PorterDuff.Mode.SRC_IN)
        DrawableCompat.setTint(nDrawable, color)
        return nDrawable
    }

    // This returns a NEW Drawable because of the mutate() call. The mutate() call is necessary
    // because Drawables with the same resource have shared states otherwise.
    @CheckResult
    fun createTintedDrawable(drawable:Drawable?, sl:ColorStateList):Drawable? {
        if (drawable == null) return null
        val nDrawable = DrawableCompat.wrap(drawable.mutate())
        DrawableCompat.setTintList(nDrawable, sl)
        return nDrawable
    }

    fun setCursorTint(editText:EditText, @ColorInt color:Int) {
        try {
            val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            fCursorDrawableRes.isAccessible = true
            val mCursorDrawableRes = fCursorDrawableRes.getInt(editText)
            val fEditor = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor.get(editText)
            val clazz = editor.javaClass
            val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
            fCursorDrawable.isAccessible = true
            val drawables = arrayOfNulls<Drawable>(2)
            drawables[0] = ContextCompat.getDrawable(editText.context, mCursorDrawableRes)
            drawables[0] = createTintedDrawable(drawables[0], color)
            drawables[1] = ContextCompat.getDrawable(editText.context, mCursorDrawableRes)
            drawables[1] = createTintedDrawable(drawables[1], color)
            fCursorDrawable.set(editor, drawables)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("PrivateResource")
    fun setTintAuto(view:View, @ColorInt color:Int, background:Boolean, isDark:Boolean) {
        if (!background) {
            if (view is RadioButton) {
                setTint(view, color, isDark)
            } else if (view is SeekBar) {
                setTint(view, color, isDark)
            } else if (view is ProgressBar) {
                setTint(view, color)
            } else if (view is EditText) {
                setTint(view, color, isDark)
            } else if (view is CheckBox) {
                setTint(view, color, isDark)
            } else if (view is ImageView) {
                setTint(view, color)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !background
                && view.background is RippleDrawable) {
                // Ripples for the above views (e.g. when you tap and hold a switch or checkbox)
                val rd = view.background as RippleDrawable
                val unchecked = ContextCompat.getColor(view.context,
                        if (isDark)
                            R.color.ripple_material_dark
                        else
                            R.color.ripple_material_light)
                val checked = ColorUtils.adjustAlpha(color, 0.4f)
                val sl = ColorStateList(
                        arrayOf(intArrayOf(-android.R.attr.state_activated,
                                -android.R.attr.state_checked),
                                intArrayOf(android.R.attr.state_activated),
                                intArrayOf(android.R.attr.state_checked)),
                        intArrayOf(unchecked, checked, checked))
                rd.setColor(sl)
            }
        }
        if (background || ((view !is Switch && view !is SwitchCompat))) {
            // Need to tint the background of a view
            if (view is FloatingActionButton || view is Button) {
                setTintSelector(view, color, false, isDark)
            } else if (view.background != null) {
                var drawable:Drawable? = view.background
                if (drawable != null) {
                    if (view is TextInputEditText) {
                        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    } else {
                        drawable = createTintedDrawable(drawable, color)
                        setBackgroundCompat(view, drawable)
                    }
                }
            }
        }
    }

    fun setTint(radioButton:RadioButton, @ColorInt color:Int,
                useDarker:Boolean) {
        val sl = ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_enabled),
                        intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)),
                intArrayOf(
                        // Radio button includes own alpha for disabled state
                        ColorUtils.stripAlpha(
                                ColorUtils.getMaterialDisabledHintTextColor(useDarker)),
                        ColorUtils.getMaterialSecondaryTextColor(useDarker), color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            radioButton.buttonTintList = sl
        } else {
            @SuppressLint("PrivateResource")
            val d = createTintedDrawable(
                    ContextCompat.getDrawable(radioButton.context,
                            R.drawable.abc_btn_radio_material), sl)
            radioButton.buttonDrawable = d
        }
    }

    fun setTint(seekBar:SeekBar, @ColorInt color:Int, useDarker:Boolean) {
        val s1 = getDisabledColorStateList(color,
                ColorUtils.getMaterialDisabledHintTextColor(useDarker))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            seekBar.thumbTintList = s1
            seekBar.progressTintList = s1
        } else {
            val progressDrawable = createTintedDrawable(seekBar.progressDrawable, s1)
            seekBar.progressDrawable = progressDrawable
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val thumbDrawable = createTintedDrawable(seekBar.thumb, s1)
                seekBar.thumb = thumbDrawable
            }
        }
    }

    fun setTint(progressBar:ProgressBar, @ColorInt color:Int) = setTint(progressBar, color, false)

    private fun setTint(progressBar:ProgressBar, @ColorInt color:Int,
                        skipIndeterminate:Boolean) {
        val sl = ColorStateList.valueOf(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.progressTintList = sl
            progressBar.secondaryProgressTintList = sl
            if (!skipIndeterminate) {
                progressBar.indeterminateTintList = sl
            }
        } else {
            val mode = PorterDuff.Mode.SRC_IN
            if (!skipIndeterminate && progressBar.indeterminateDrawable != null) {
                progressBar.indeterminateDrawable.setColorFilter(color, mode)
            }
            if (progressBar.progressDrawable != null) {
                progressBar.progressDrawable.setColorFilter(color, mode)
            }
        }
    }

    fun setTint(editText:EditText, @ColorInt color:Int, useDarker:Boolean) {
        val editTextColorStateList = ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_enabled),
                        intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_pressed,
                                -android.R.attr.state_focused), intArrayOf()),
                intArrayOf(
                        ColorUtils.getMaterialDisabledHintTextColor(useDarker),
                        ColorUtils.getMaterialSecondaryTextColor(useDarker), color))
        if (editText is TintableBackgroundView) {
            ViewCompat.setBackgroundTintList(editText, editTextColorStateList)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.backgroundTintList = editTextColorStateList
        }
        setCursorTint(editText, color)
    }

    fun setTint(box:CheckBox, @ColorInt color:Int, useDarker:Boolean) {
        val sl = ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_enabled),
                        intArrayOf(android.R.attr.state_enabled, -android.R.attr
                                .state_checked),
                        intArrayOf(android.R.attr.state_enabled, android.R.attr
                                .state_checked)),
                intArrayOf(
                        ColorUtils.getMaterialDisabledHintTextColor(useDarker),
                        ColorUtils.getMaterialSecondaryTextColor(useDarker), color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            box.buttonTintList = sl
        } else {
            @SuppressLint("PrivateResource")
            val drawable = createTintedDrawable(
                    ContextCompat.getDrawable(box.context, R.drawable.abc_btn_check_material), sl)
            box.buttonDrawable = drawable
        }
    }

    fun setTint(image:ImageView, @ColorInt color:Int) = image.setColorFilter(color,
            PorterDuff.Mode.SRC_ATOP)

    fun getDisabledColorStateList(@ColorInt normal:Int, @ColorInt disabled:Int):ColorStateList {
        return ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr
                        .state_enabled)),
                intArrayOf(disabled, normal))
    }

    fun setTintSelector(view:View, @ColorInt color:Int,
                        darker:Boolean, useDarkTheme:Boolean) {
        val isLightColor = ColorUtils.isLightColor(color)
        val disabled = ColorUtils.getMaterialDisabledHintTextColor(useDarkTheme)
        val pressed = ColorUtils.shiftColor(color, if (darker) 0.9f else 1.1f)
        val activated = ColorUtils.shiftColor(color, if (darker) 1.1f else 0.9f)
        val rippleColor = ColorUtils.getDefaultRippleColor(view.context, isLightColor)
        val textColor = ColorUtils.getMaterialPrimaryTextColor(isLightColor)

        val sl:ColorStateList
        if (view is Button) {
            sl = getDisabledColorStateList(color, disabled)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view.getBackground() is RippleDrawable) {
                val rd = view.getBackground() as RippleDrawable
                rd.setColor(ColorStateList.valueOf(rippleColor))
            }

            // Disabled text color state for buttons, may get overridden later by ATE tags
            view.setTextColor(
                    getDisabledColorStateList(
                            textColor,
                            ColorUtils.getMaterialDisabledHintTextColor(useDarkTheme)))
        } else if (view is FloatingActionButton) {
            // FloatingActionButton doesn't support disabled state?
            sl = ColorStateList(
                    arrayOf(intArrayOf(-android.R.attr.state_pressed), intArrayOf(android.R
                            .attr.state_pressed)),
                    intArrayOf(color, pressed))

            val fab = view
            fab.rippleColor = rippleColor
            fab.backgroundTintList = sl
            if (fab.drawable != null)
                fab.setImageDrawable(createTintedDrawable(fab.drawable, textColor))
            return
        } else {
            sl = ColorStateList(
                    arrayOf(intArrayOf(-android.R.attr.state_enabled),
                            intArrayOf(android.R.attr.state_enabled),
                            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed),
                            intArrayOf(android.R.attr.state_enabled,
                                    android.R.attr.state_activated),
                            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)),
                    intArrayOf(disabled, color, pressed, activated, activated))
        }

        var drawable:Drawable? = view.background
        if (drawable != null) {
            drawable = createTintedDrawable(drawable, sl)
            setBackgroundCompat(view, drawable)
        }
        if (view is TextView && view !is Button) {
            view.setTextColor(
                    getDisabledColorStateList(
                            textColor,
                            ColorUtils.getMaterialDisabledHintTextColor(isLightColor)))
        }
    }

    fun setBackgroundCompat(view:View, drawable:Drawable?) {
        view.background = drawable
    }
}