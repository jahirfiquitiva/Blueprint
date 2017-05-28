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

package jahirfiquitiva.libs.iconshowcase.utils.core

import android.content.Context
import jahirfiquitiva.libs.iconshowcase.utils.NetworkUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

object JSONParser {
    fun getJSONFromURL(context:Context, url:String):JSONObject? {
        try {
            if (NetworkUtils.isConnected(context)) {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                var response:Response? = null
                try {
                    response = client.newCall(request).execute()
                } catch (ignored:Exception) {
                }
                return JSONObject(response?.body()?.string())
            }
        } catch (ignored:Exception) {
        }
        return null
    }
}