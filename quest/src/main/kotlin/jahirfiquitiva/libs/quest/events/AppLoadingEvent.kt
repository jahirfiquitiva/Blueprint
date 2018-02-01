package jahirfiquitiva.libs.quest.events

import java.util.Locale

/**
 * Created by Allan Wang on 2016-08-27.
 * @property percent Gets percentage loaded; -1 refers to a loading AppFilter
 */
class AppLoadingEvent(val percent: Int) {
    val message: String
        get() {
            return when (percent) {
                -2 -> "Loading Appfilter..."
                -1 -> "Retrieving App List..."
                else -> String.format(Locale.getDefault(), "Loading %d%%", percent)
            }
        }
}
