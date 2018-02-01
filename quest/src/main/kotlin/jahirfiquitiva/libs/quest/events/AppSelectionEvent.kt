package jahirfiquitiva.libs.quest.events

import jahirfiquitiva.libs.quest.IconRequest

/**
 * Created by Allan Wang on 2016-08-27.
 */
class AppSelectionEvent(val count: Int) {
    val isAtMax: Boolean
        get() {
            val max = IconRequest.get()?.maxSelectable ?: 0
            return max > 0 && count == max
        }
    
    val isAllSelected: Boolean
        get() {
            val apps = IconRequest.get()?.apps.orEmpty()
            return count == apps.size
        }
}