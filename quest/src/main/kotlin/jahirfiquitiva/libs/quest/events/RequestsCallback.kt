package jahirfiquitiva.libs.quest.events

import android.content.Context

import jahirfiquitiva.libs.quest.App
import jahirfiquitiva.libs.quest.IconRequest

import java.util.ArrayList

abstract class RequestsCallback {
    abstract fun onAppsLoaded(apps: ArrayList<App>)

    abstract fun onRequestLimited(
            context: Context,
            @IconRequest.State reason: Int,
            requestsLeft: Int,
            millis: Long
                                 )

    abstract fun onRequestEmpty(context: Context)
}