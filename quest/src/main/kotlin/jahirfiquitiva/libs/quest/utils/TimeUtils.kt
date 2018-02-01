package jahirfiquitiva.libs.quest.utils

import java.util.Calendar

/**
 * Created by Allan Wang on 2016-08-20.
 */
internal object TimeUtils {
    /**
     * This method returns current time in milliseconds
     *
     * @return time in milliseconds
     */
    val currentTimeInMillis: Long
        get() = Calendar.getInstance().timeInMillis
}