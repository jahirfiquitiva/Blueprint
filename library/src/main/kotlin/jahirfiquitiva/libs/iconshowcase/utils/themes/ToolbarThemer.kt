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
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.callbacks.CollapsingToolbarCallback
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils
import jahirfiquitiva.libs.iconshowcase.utils.CoreUtils
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils
import java.lang.reflect.Field
import java.util.*

object ToolbarThemer {
    fun updateToolbarColors(activity:AppCompatActivity, toolbar:Toolbar, offset:Int) {
        val defaultIconsColor = ResourceUtils.getColor(activity, android.R.color.white)
        var ratio = CoreUtils.round(offset / 255.0, 1)
        if (ratio > 1)
            ratio = 1.0
        else if (ratio < 0) ratio = 0.0
        val rightIconsColor = ColorUtils.blendColors(defaultIconsColor,
                ColorUtils.getMaterialActiveIconsColor(
                        ColorUtils.isDarkColor(
                                AttributeExtractor.getPrimaryColorFrom(activity))), ratio.toFloat())
        tintToolbar(toolbar, rightIconsColor)
        updateStatusBarStyle(activity, if (ratio > 0.7)
            CollapsingToolbarCallback.State.COLLAPSED
        else
            CollapsingToolbarCallback.State.EXPANDED)
    }

    fun tintToolbar(toolbar:Toolbar, toolbarIconsColor:Int) {
        val colorFilter = PorterDuffColorFilter(toolbarIconsColor, PorterDuff.Mode.SRC_IN)

        for (i in 0..toolbar.childCount - 1) {
            val v = toolbar.getChildAt(i)

            //Step 1 : Changing the color of back button (or open drawer button).
            if (v is ImageButton) {
                v.drawable.colorFilter = colorFilter
            }

            if (v is ActionMenuView) {
                for (j in 0..v.childCount - 1) {
                    //Step 2: Changing the color of any ActionMenuViews - icons that are not back
                    // button, nor text, nor overflow menu icon.
                    val innerView = v.getChildAt(j)
                    if (innerView is ActionMenuItemView) {
                        for (k in 0..innerView.compoundDrawables.size - 1) {
                            if (innerView.compoundDrawables[k] != null) {
                                val finalK = k
                                innerView.post { innerView.compoundDrawables[finalK].colorFilter = colorFilter }
                            }
                        }
                    }
                }
            }
        }

        // Step 3: Changing the color of title and subtitle.
        toolbar.setTitleTextColor(ColorUtils.getMaterialPrimaryTextColor(
                ColorUtils.isDarkColor(AttributeExtractor.getPrimaryColorFrom(toolbar.context))))
        toolbar.setSubtitleTextColor(ColorUtils.getMaterialSecondaryTextColor(
                ColorUtils.isDarkColor(AttributeExtractor.getPrimaryColorFrom(toolbar.context))))

        // Step 4: Tint toolbar menu.
        tintToolbarMenu(toolbar, toolbar.menu, toolbarIconsColor)

        // Step 5: Change the color of overflow menu icon.
        var drawable = toolbar.overflowIcon
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable)
            toolbar.overflowIcon = TintUtils.createTintedDrawable(drawable, toolbarIconsColor)
        }
        // setOverflowButtonColor(toolbar, toolbarIconsColor)
    }

    fun tintToolbarMenu(toolbar:Toolbar?, menu:Menu?,
                        @ColorInt iconsColor:Int) {
        if (toolbar == null || menu == null) return
        // The collapse icon displays when action views are expanded (e.g. SearchView)
        try {
            val field = Toolbar::class.java.getDeclaredField("mCollapseIcon")
            field.isAccessible = true
            val collapseIcon = field.get(toolbar) as Drawable
            field.set(toolbar, TintUtils.createTintedDrawable(collapseIcon, iconsColor))
        } catch (e:Exception) {
            e.printStackTrace()
        }

        // Theme menu action views
        for (i in 0..menu.size() - 1) {
            val item = menu.getItem(i)
            if (item.actionView is SearchView) {
                themeSearchView(iconsColor, item.actionView as SearchView)
            }
        }

        // Display icons for easy UI understanding
        /*
        try {
            val MenuBuilder = menu.javaClass
            val setOptionalIconsVisible = MenuBuilder.getDeclaredMethod("setOptionalIconsVisible",
                    Boolean::class.javaPrimitiveType)
            if (!setOptionalIconsVisible.isAccessible) setOptionalIconsVisible.isAccessible = true
            setOptionalIconsVisible.invoke(menu, true)
        } catch (ignored:Exception) {
        }
        */
    }

    private fun setOverflowButtonColor(toolbar:Toolbar,
                                       @ColorInt color:Int) {
        @SuppressLint("PrivateResource")
        val overflowDescription = toolbar.resources.getString(
                R.string.abc_action_menu_overflow_description)
        val outViews = ArrayList<View>()
        toolbar.findViewsWithText(
                outViews, overflowDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
        if (outViews.isEmpty()) return
        val overflow = outViews[0] as AppCompatImageView
        overflow.setImageDrawable(TintUtils.createTintedDrawable(overflow.drawable, color))
    }

    private fun themeSearchView(tintColor:Int, view:SearchView) {
        val cls = view.javaClass
        try {
            val mSearchSrcTextViewField = cls.getDeclaredField("mSearchSrcTextView")
            mSearchSrcTextViewField.isAccessible = true
            val mSearchSrcTextView = mSearchSrcTextViewField.get(view) as EditText
            mSearchSrcTextView.setTextColor(tintColor)
            mSearchSrcTextView.setHintTextColor(
                    ColorUtils.getMaterialDisabledHintTextColor(ColorUtils.isDarkColor(tintColor)))
            TintUtils.setCursorTint(mSearchSrcTextView, tintColor)

            var field = cls.getDeclaredField("mSearchButton")
            tintImageView(view, field, tintColor)
            field = cls.getDeclaredField("mGoButton")
            tintImageView(view, field, tintColor)
            field = cls.getDeclaredField("mCloseButton")
            tintImageView(view, field, tintColor)
            field = cls.getDeclaredField("mVoiceButton")
            tintImageView(view, field, tintColor)

            field = cls.getDeclaredField("mSearchPlate")
            field.isAccessible = true
            TintUtils.setTintAuto(field.get(view) as View, tintColor, true,
                    ColorUtils.isDarkColor(tintColor))

            field = cls.getDeclaredField("mSearchHintIcon")
            field.isAccessible = true
            field.set(view, TintUtils.createTintedDrawable(field.get(view) as Drawable, tintColor))
        } catch (e:Exception) {
            e.printStackTrace()
        }

    }

    @Throws(Exception::class)
    private fun tintImageView(target:Any, field:Field, tintColor:Int) {
        field.isAccessible = true
        val imageView = field.get(target) as ImageView
        if (imageView.drawable != null) {
            imageView.setImageDrawable(
                    TintUtils.createTintedDrawable(imageView.drawable, tintColor))
        }
    }

    private fun updateStatusBarStyle(activity:AppCompatActivity,
                                     state:CollapsingToolbarCallback.State) {
        if (state === CollapsingToolbarCallback.State.COLLAPSED) {
            ThemeUtils.setStatusBarModeTo(activity)
        } else {
            ThemeUtils.setStatusBarModeTo(activity, false)
        }
    }

}