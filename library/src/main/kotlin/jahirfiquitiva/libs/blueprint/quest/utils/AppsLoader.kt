package jahirfiquitiva.libs.blueprint.quest.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import jahirfiquitiva.libs.blueprint.helpers.utils.BL
import jahirfiquitiva.libs.blueprint.quest.App
import jahirfiquitiva.libs.blueprint.quest.events.RequestsCallback
import java.util.ArrayList
import java.util.HashSet

internal fun Context.getInstalledApps(
    filter: HashSet<String>,
    callback: RequestsCallback? = null
                                     ): ArrayList<App> {
    
    val packagesList = try {
        packageManager.queryIntentActivities(
            Intent("android.intent.action.MAIN")
                .addCategory("android.intent.category.LAUNCHER"),
            PackageManager.GET_RESOLVED_FILTER)
    } catch (e: Exception) {
        ArrayList<ResolveInfo>()
    }
    
    val apps = ArrayList<App>()
    
    var loaded = 0
    var filtered = 0
    
    if (packagesList.isNotEmpty()) {
        val list = ArrayList(packagesList.distinct())
        list.sortWith(NameComparator(packageManager))
        
        for (ri in packagesList) {
            val riPkg = ri.activityInfo.packageName
            val component = riPkg + "/" + ri.activityInfo.name
            
            if (filter.contains(component) || packageName == riPkg) {
                filtered++
                continue
            }
            
            var name: CharSequence? = ri.loadLabel(packageManager)
            if (name == null) name = riPkg
            
            val appToAdd = App(getLocalizedName(riPkg, name.toString()), riPkg, component)
            appToAdd.loadIcon(this)
            apps.add(appToAdd)
            loaded++
            
            try {
                val percent = loaded * 100 / list.size
                callback?.onRequestProgress(percent)
            } catch (e: Exception) {
            }
        }
    }
    
    BL.d("Loaded ${apps.size} total app(s), filtered out $filtered app(s).")
    try {
        callback?.onRequestProgress(100)
    } catch (e: Exception) {
    }
    return ArrayList(apps.distinctBy { it.pkg }.sortedBy { it.name })
}