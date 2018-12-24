/*
 * Copyright (c) 2018. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcomp
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jahirfiquitiva.libs.blueprint.quest

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import jahirfiquitiva.libs.blueprint.quest.utils.formatCorrectly
import jahirfiquitiva.libs.kext.extensions.getAppIcon

/**
 * Created by Allan Wang on 2016-08-20.
 */
class App : Parcelable {
    
    var name: String = ""
        get() = field.formatCorrectly()
        private set
    var pkg: String = ""
        private set
    var comp: String = ""
        private set
    
    private var icon: Drawable? = null
    
    constructor(name: String, pkg: String, comp: String) {
        this.name = name.formatCorrectly()
        this.pkg = pkg
        this.comp = comp
    }
    
    fun getIcon(context: Context): Drawable? {
        try {
            if (icon != null) return icon
            icon = context.getAppIcon(pkg)
            return icon
        } catch (e: Exception) {
            return null
        }
    }
    
    override fun toString(): String = comp
    
    override fun equals(other: Any?): Boolean = other is App && other.comp == comp
    
    override fun hashCode(): Int {
        var result = pkg.hashCode()
        result = 31 * result + comp.hashCode()
        return result
    }
    
    override fun describeContents(): Int = 0
    
    private constructor(parcel: Parcel) {
        name = parcel.readString() ?: ""
        comp = parcel.readString() ?: ""
        pkg = parcel.readString() ?: ""
    }
    
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(comp)
        dest.writeString(pkg)
    }
    
    companion object CREATOR : Parcelable.Creator<App> {
        override fun createFromParcel(parcel: Parcel): App = App(parcel)
        override fun newArray(size: Int): Array<App?> = arrayOfNulls(size)
    }
}