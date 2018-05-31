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
package jahirfiquitiva.libs.blueprint.quest.utils

import java.text.Normalizer
import java.util.Locale

internal fun String.formatCorrectly(): String {
    return replace("[^\\w\\s]+".toRegex(), " ").trim { it <= ' ' }
        .replace(" +".toRegex(), " ")
        .replace("\\p{Z}".toRegex(), "_")
}

internal fun CharSequence.hasContent(): Boolean = trim().isNotBlank() && trim().isNotEmpty()

internal fun String.safeDrawableName(): String {
    val text = if (Character.isDigit(get(0))) ("a_" + this) else this
    val normalized = Normalizer.normalize(text, Normalizer.Form.NFKD)
    val withoutAccents = normalized.replace("[\\p{InCombiningDiacriticalMarks}]", "")
    return withoutAccents.formatCorrectly().toLowerCase(Locale.getDefault()).replace(" ", "_")
}