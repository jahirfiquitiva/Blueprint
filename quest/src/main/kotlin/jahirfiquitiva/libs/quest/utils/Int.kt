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
package jahirfiquitiva.libs.quest.utils

import android.os.Build

internal fun Int.toAndroidVersion(): String {
    return when (this) {
        Build.VERSION_CODES.CUPCAKE -> "Cupcake"
        Build.VERSION_CODES.DONUT -> "Donut"
        Build.VERSION_CODES.ECLAIR, Build.VERSION_CODES.ECLAIR_0_1, Build.VERSION_CODES.ECLAIR_MR1 -> "Eclair"
        Build.VERSION_CODES.FROYO -> "Froyo"
        Build.VERSION_CODES.GINGERBREAD, Build.VERSION_CODES.GINGERBREAD_MR1 -> "Gingerbread"
        Build.VERSION_CODES.HONEYCOMB, Build.VERSION_CODES.HONEYCOMB_MR1, Build.VERSION_CODES.HONEYCOMB_MR2 -> "Honeycomb"
        Build.VERSION_CODES.ICE_CREAM_SANDWICH, Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 -> "Ice Cream Sandwich"
        Build.VERSION_CODES.JELLY_BEAN, Build.VERSION_CODES.JELLY_BEAN_MR1, Build.VERSION_CODES.JELLY_BEAN_MR2 -> "Jelly Bean"
        Build.VERSION_CODES.KITKAT -> "KitKat"
        Build.VERSION_CODES.KITKAT_WATCH -> "KitKat Watch"
        Build.VERSION_CODES.LOLLIPOP, Build.VERSION_CODES.LOLLIPOP_MR1 -> "Lollipop"
        Build.VERSION_CODES.M -> "Marshmallow"
        Build.VERSION_CODES.N, Build.VERSION_CODES.N_MR1 -> "Nougat"
        Build.VERSION_CODES.O, Build.VERSION_CODES.O_MR1 -> "Oreo"
        28 -> "P"
        else -> "Unknown"
    }
}