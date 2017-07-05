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
 * 	https://github.com/jahirfiquitiva/Blueprint#special-thanks
 */

package jahirfiquitiva.libs.blueprint.extensions

import android.support.v7.graphics.Palette
import java.util.*

fun Palette.isColorDark() = !this.isColorLight()

fun Palette.isColorLight() = this.bestSwatch?.rgb?.isColorLight() ?: false

val Palette.bestSwatch:Palette.Swatch?
    get() {
        this.vibrantSwatch?.let { return it }
        this.lightVibrantSwatch?.let { return it }
        this.darkVibrantSwatch?.let { return it }
        this.mutedSwatch?.let { return it }
        this.lightMutedSwatch?.let { return it }
        this.darkMutedSwatch?.let { return it }
        if (this.swatches.isNotEmpty()) return getBestPaletteSwatch(this.swatches)
        return null
    }

fun getBestPaletteSwatch(swatches:List<Palette.Swatch>):Palette.Swatch =
        Collections.max<Palette.Swatch>(swatches) { opt1, opt2 ->
            val a = opt1?.population ?: 0
            val b = opt2?.population ?: 0
            a - b
        }