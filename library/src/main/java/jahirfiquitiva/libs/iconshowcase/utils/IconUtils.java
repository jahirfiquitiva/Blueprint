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

package jahirfiquitiva.libs.iconshowcase.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;

import java.io.File;
import java.io.FileOutputStream;

public class IconUtils {

    public static Bitmap getBitmapWithName(Context context, String iconName) {
        return getBitmapDrawableWithName(context, iconName).getBitmap();
    }

    public static BitmapDrawable getBitmapDrawableWithName(Context context, String iconName) {
        try {
            return (BitmapDrawable) ResourcesCompat.getDrawable(context.getResources(),
                    getIconResourceWithName(context, iconName), null);
        } catch (Exception e) {
            throw new Resources.NotFoundException("Icon with name \'" + iconName + "\' could not " +
                    "be found");
        }
    }

    public static Drawable getDrawableWithName(Context context, String iconName) {
        try {
            return ContextCompat.getDrawable(context, getIconResourceWithName(context, iconName));
        } catch (Exception e) {
            throw new Resources.NotFoundException("Icon with name \'" + iconName + "\' could not " +
                    "be found");
        }
    }

    private static int getIconResourceWithName(Context context, String iconName) {
        int res = context.getResources()
                .getIdentifier(iconName, "drawable", context.getPackageName());
        if (res != 0) {
            return res;
        } else {
            return 0;
        }
    }

    public static Uri getUriForIcon(Context context, String name, Bitmap icon) {
        Uri uri = null;
        File iconFile = new File(context.getCacheDir(), name + ".png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(iconFile);
            icon.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            uri = getUriFromFile(context, iconFile);
            if (uri == null) uri = Uri.fromFile(iconFile);
        } catch (Exception ignored) {
        }
        if (uri == null) {
            int resId = getIconResourceWithName(context, name);
            try {
                uri = getUriFromResource(context, resId);
            } catch (Exception e) {
                try {
                    uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + context.getPackageName() + "/" +
                            String.valueOf(resId));
                } catch (Exception ignored) {
                }
            }
        }
        return uri;
    }

    private static Uri getUriFromResource(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

    private static Uri getUriFromFile(Context context, File file) {
        try {
            return FileProvider.getUriForFile(context, context.getPackageName() +
                    ".fileProvider", file);
        } catch (Exception ignored) {
        }
        return null;
    }

}