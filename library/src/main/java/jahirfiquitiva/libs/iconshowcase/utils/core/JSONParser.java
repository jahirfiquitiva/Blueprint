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
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.utils.core;

import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;

import jahirfiquitiva.libs.iconshowcase.utils.NetworkUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JSONParser {

    @SuppressWarnings("ConstantConditions")
    public static JSONObject getJSONFromURL(Context context, String url) {
        try {
            if (NetworkUtils.isConnected(context)) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException ignored) {
                }
                if (response != null) {
                    try {
                        return new JSONObject(response.body().string());
                    } catch (Exception ignored) {
                        return null;
                    }
                }
            } else {
                return null;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}