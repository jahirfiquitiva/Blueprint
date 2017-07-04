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
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IkoniK#special-thanks
 */

package jahirfiquitiva.libs.ikonik.ui.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout

class SplitButtonsLayout:LinearLayout {

    var buttonCount:Int = 0

    constructor(context:Context):super(context)
    constructor(context:Context, attributeSet:AttributeSet):super(context, attributeSet)
    constructor(context:Context, attributeSet:AttributeSet, defStyleAttr:Int)
            :super(context, attributeSet, defStyleAttr)

    override fun setOrientation(orientation:Int) = super.setOrientation(HORIZONTAL)

    override fun setWeightSum(weightSum:Float) = super.setWeightSum(buttonCount.toFloat())

    fun addButton(text:String, link:String) {
        if (hasAllButtons()) return
        val nButton:Button =
                LayoutInflater.from(context).inflate(R.layout.item_credits_button, this,
                                                     false) as Button
        val lParams:LayoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1F)
        nButton.text = text
        nButton.tag = link
        addView(nButton, lParams)
    }

    fun hasAllButtons():Boolean = childCount == buttonCount
}