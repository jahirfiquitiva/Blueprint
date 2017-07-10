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

import android.content.Context
import android.util.Log
import jahirfiquitiva.libs.blueprint.utils.LOG_TAG

fun Context.printVerbose(verbose:String) = Log.v(LOG_TAG, verbose)

fun Context.printDebug(debug:String) = Log.d(LOG_TAG, debug)

fun Context.printInfo(info:String) = Log.i(LOG_TAG, info)

fun Context.printWarning(warning:String) = Log.w(LOG_TAG, warning)

fun Context.printError(error:String) = Log.e(LOG_TAG, error)

fun Context.printError(error:String, th:Throwable) = Log.e(LOG_TAG, error, th)