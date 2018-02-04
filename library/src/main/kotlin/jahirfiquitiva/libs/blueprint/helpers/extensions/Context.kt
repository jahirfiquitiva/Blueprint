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
package jahirfiquitiva.libs.blueprint.helpers.extensions

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.BPKonfigs
import jahirfiquitiva.libs.blueprint.helpers.utils.PREFERENCES_NAME
import java.util.concurrent.TimeUnit

val Context.bpKonfigs: BPKonfigs
    get() = BPKonfigs.newInstance(PREFERENCES_NAME, this)

fun Context.millisToText(millis: Long): String {
    when {
        TimeUnit.MILLISECONDS.toSeconds(millis) < 60 ->
            return (TimeUnit.MILLISECONDS.toSeconds(millis).toString() + " "
                    + getString(R.string.seconds).toLowerCase())
        TimeUnit.MILLISECONDS.toMinutes(millis) < 60 ->
            return (TimeUnit.MILLISECONDS.toMinutes(millis).toString() + " "
                    + getString(R.string.minutes).toLowerCase())
        TimeUnit.MILLISECONDS.toHours(millis) < 24 ->
            return (TimeUnit.MILLISECONDS.toHours(millis).toString() + " "
                    + getString(R.string.hours).toLowerCase())
        TimeUnit.MILLISECONDS.toDays(millis) < 7 ->
            return (TimeUnit.MILLISECONDS.toDays(millis).toString() + " "
                    + getString(R.string.days)).toLowerCase()
        millis.toWeeks() < 4 -> return (millis.toWeeks().toString() + " "
                + getString(R.string.weeks).toLowerCase())
        else -> return (millis.toMonths().toString() + " "
                + getString(R.string.months).toLowerCase())
    }
}

private fun Long.toWeeks() = TimeUnit.MILLISECONDS.toDays(this) / 7
private fun Long.toMonths() = toWeeks() / 4

internal fun Context.getUriFromResource(id: Int): Uri? {
    return Uri.parse(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://" +
                    "${resources.getResourcePackageName(id)}/" +
                    "${resources.getResourceTypeName(id)}/" +
                    resources.getResourceEntryName(id))
}