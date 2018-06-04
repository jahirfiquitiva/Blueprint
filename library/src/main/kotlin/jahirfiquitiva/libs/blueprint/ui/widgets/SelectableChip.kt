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
package jahirfiquitiva.libs.blueprint.ui.widgets

import android.content.Context
import android.support.annotation.ColorInt
import android.util.AttributeSet
import ca.allanwang.kau.utils.tint
import com.jahirfiquitiva.chip.ChipView
import jahirfiquitiva.libs.kext.extensions.cardBackgroundColor
import jahirfiquitiva.libs.kext.extensions.dividerColor
import jahirfiquitiva.libs.kext.extensions.drawable
import jahirfiquitiva.libs.kext.extensions.getPrimaryTextColorFor

internal class SelectableChip : ChipView {
    
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, style: Int) : super(context, attrs, style)
    
    @ColorInt
    private val normalColor: Int = context.cardBackgroundColor
    
    private var onSelectedListener: (Boolean) -> Unit = {}
    
    @ColorInt
    var selectedColor: Int = normalColor
    
    var chipSelected: Boolean = false
        set(value) {
            field = value
            val bgColor = if (value) selectedColor else normalColor
            val textColor = context.getPrimaryTextColorFor(bgColor, 0.65F)
            setBackgroundColor(bgColor)
            setTextColor(textColor)
            setIcon(
                context.drawable("ic_chip_bullet")?.tint(if (value) textColor else selectedColor))
            onSelectedListener(value)
        }
    
    init {
        setBackgroundColor(normalColor)
        strokeColor = context.dividerColor
    }
    
    fun setOnSelectedListener(listener: (Boolean) -> Unit) {
        this.onSelectedListener = listener
    }
    
    fun initClickListener() {
        setOnClickListener { chipSelected = !chipSelected }
    }
}