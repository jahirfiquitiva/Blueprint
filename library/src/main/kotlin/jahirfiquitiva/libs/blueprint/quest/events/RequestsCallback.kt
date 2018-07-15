package jahirfiquitiva.libs.blueprint.quest.events

import android.content.Context

import jahirfiquitiva.libs.blueprint.quest.App
import jahirfiquitiva.libs.blueprint.quest.IconRequest

import java.util.ArrayList

interface RequestsCallback {
    fun onAppsLoaded(apps: ArrayList<App>) {}
    
    fun onRequestLimited(
        context: Context,
        @IconRequest.State reason: Int,
        requestsLeft: Int,
        millis: Long
                        ) {
    }
    
    fun onRequestEmpty(context: Context) {}
    
    fun onRequestProgress(progress: Int) {}
}