package dev.jahir.blueprint.extensions

import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import dev.jahir.frames.extensions.resources.hasContent

class InstalledAppsComparator(private val packageManager: PackageManager? = null) :
    Comparator<ResolveInfo> {
    override fun compare(ra: ResolveInfo?, rb: ResolveInfo?): Int {
        packageManager ?: return 0
        try {
            var sa: String = (ra?.loadLabel(packageManager) ?: "").toString()
            var sb: String = (rb?.loadLabel(packageManager) ?: "").toString()

            if (!sa.hasContent()) sa = ra?.activityInfo?.packageName.orEmpty()
            if (!sb.hasContent()) sb = rb?.activityInfo?.packageName.orEmpty()

            if (!sa.hasContent() && !sb.hasContent()) return 0
            if (!sa.hasContent()) return -1
            if (!sb.hasContent()) return 1
            return sa.compareTo(sb)
        } catch (e: Exception) {
            return 0
        }
    }
}