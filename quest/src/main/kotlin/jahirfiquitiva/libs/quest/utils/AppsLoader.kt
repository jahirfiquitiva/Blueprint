package jahirfiquitiva.libs.quest.utils

import android.content.Context
import android.content.Intent
import jahirfiquitiva.libs.quest.App
import java.util.ArrayList
import java.util.HashSet

internal fun Context.getInstalledApps(
        filter: HashSet<String>,
        onProgress: (progress: Int) -> Unit = {}
                                     ): ArrayList<App> {
    
    val packageList = packageManager.queryIntentActivities(
            Intent("android.intent.action.MAIN")
                    .addCategory("android.intent.category.LAUNCHER"), 0)
    
    val list = ArrayList(packageList)
    list.sortWith(NameComparator(packageManager))
    
    val apps = ArrayList<App>()
    
    var loaded = 0
    var filtered = 0
    for (ri in packageList) {
        val riPkg = ri.activityInfo.packageName
        val launchStr = riPkg + "/" + ri.activityInfo.name
        
        if (filter.contains(launchStr) || packageName == riPkg) {
            filtered++
            continue
        }
        
        var name: CharSequence? = ri.loadLabel(packageManager)
        if (name == null) name = ri.activityInfo.packageName
        apps.add(
                App(
                        this.getLocalizedName(launchStr, name.toString()), launchStr,
                        ri.activityInfo.packageName))
        loaded++
        val percent = loaded * 100 / packageList.size
        onProgress(percent)
    }
    
    QuestLog.d { "Loaded ${apps.size} total app(s), filtered out $filtered app(s)." }
    return ArrayList(apps.distinctBy { it.pckg }.sortedBy { it.name })
}