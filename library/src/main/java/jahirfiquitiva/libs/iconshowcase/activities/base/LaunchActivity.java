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

package jahirfiquitiva.libs.iconshowcase.activities.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.libs.iconshowcase.utils.IntentUtils;

public class LaunchActivity extends ShowcaseActivity {

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void launchShowcase(Bundle savedInstanceState) {
        try {
            Class service = getFirebaseClass();
            /*
            if (NotificationUtils.hasNotificationExtraKey(this, getIntent(), "open_link",
                    service)) {
                super.onCreate(savedInstanceState);
                Utils.openLink(this, getIntent().getStringExtra("open_link"));
                finish();
            } else {
                if (service != null) {
                    configureAndLaunch(savedInstanceState, service);
                } else {
                    catchException(savedInstanceState, null);
                }
            }
            */
        } catch (Exception ignored) {
        }
    }

    @Override
    protected Bundle getInitialConfiguration() {
        Bundle configuration = new Bundle();

        /*
        if (service != null)
            configuration.putBoolean("open_wallpapers",
                    NotificationUtils.isNotificationExtraKeyTrue(this, getIntent(), "open_walls",
                            service));
        */

        configuration.putBoolean("enableDonations", enableDonations());
        configuration.putBoolean("enableGoogleDonations", enableGoogleDonations());
        configuration.putBoolean("enablePayPalDonations", enablePayPalDonations());
        configuration.putBoolean("enableLicenseCheck", enableLicCheck());
        configuration.putBoolean("enableAmazonInstalls", enableAmazonInstalls());
        configuration.putBoolean("checkLPF", checkLPF());
        configuration.putBoolean("checkStores", checkStores());
        configuration.putString("googlePubKey", licKey());

        if (getIntent() != null) {
            if (getIntent().getDataString() != null &&
                    getIntent().getDataString().contains("_shortcut")) {
                configuration.putString("shortcut", getIntent().getDataString());
            }

            if (getIntent().getAction() != null) {
                switch (getIntent().getAction()) {
                    case IntentUtils.APPLY_ACTION:
                        configuration.putInt("picker", IntentUtils.ICONS_APPLIER);
                        break;
                    case IntentUtils.ADW_ACTION:
                    case IntentUtils.TURBO_ACTION:
                    case IntentUtils.NOVA_ACTION:
                        configuration.putInt("picker", IntentUtils.ICONS_PICKER);
                        break;
                    case Intent.ACTION_PICK:
                    case Intent.ACTION_GET_CONTENT:
                        configuration.putInt("picker", IntentUtils.IMAGE_PICKER);
                        break;
                    case Intent.ACTION_SET_WALLPAPER:
                        configuration.putInt("picker", IntentUtils.WALLS_PICKER);
                        break;
                    default:
                        configuration.putInt("picker", 0);
                        break;
                }
            }
        }
        return configuration;
    }

    protected Class getFirebaseClass() {
        return null;
    }

    protected boolean enableDonations() {
        return false;
    }

    protected boolean enableGoogleDonations() {
        return false;
    }

    protected boolean enablePayPalDonations() {
        return false;
    }

    protected boolean enableLicCheck() {
        return true;
    }

    protected boolean enableAmazonInstalls() {
        return false;
    }

    protected boolean checkLPF() {
        return true;
    }

    protected boolean checkStores() {
        return true;
    }

    protected String licKey() {
        return "key";
    }

}