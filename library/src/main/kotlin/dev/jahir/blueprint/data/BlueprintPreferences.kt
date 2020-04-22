package dev.jahir.blueprint.data

import android.content.Context
import dev.jahir.frames.data.Preferences

class BlueprintPreferences(context: Context) : Preferences(context) {

    var savedTime: Long
        get() = prefs.getLong(KEY_SAVED_TIME_MILLIS, -1)
        set(value) = prefsEditor.putLong(KEY_SAVED_TIME_MILLIS, value).apply()

    var maxApps: Int
        get() = prefs.getInt(MAX_APPS, -1)
        set(value) = prefsEditor.putInt(MAX_APPS, value).apply()

    var iconShape: Int
        get() = prefs.getInt(ICON_SHAPE, 0)
        set(value) = prefsEditor.putInt(ICON_SHAPE, value).apply()

    companion object {
        private const val KEY_SAVED_TIME_MILLIS = "saved_time_millis"
        private const val MAX_APPS = "apps_to_request"
        private const val ICON_SHAPE = "icon_shape"
    }
}