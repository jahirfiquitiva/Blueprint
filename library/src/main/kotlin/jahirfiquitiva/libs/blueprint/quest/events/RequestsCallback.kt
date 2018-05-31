package jahirfiquitiva.libs.blueprint.quest.events

import android.content.Context

import jahirfiquitiva.libs.blueprint.quest.App
import jahirfiquitiva.libs.blueprint.quest.IconRequest

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