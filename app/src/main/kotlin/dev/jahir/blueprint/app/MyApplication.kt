package dev.jahir.blueprint.app

import dev.jahir.frames.ui.FramesApplication

// TODO: Remove comment marks to enable
// import com.onesignal.OneSignal

class MyApplication : FramesApplication() {
    override fun onCreate() {
        super.onCreate()
        // TODO: Remove comment marks to enable
        /*
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
         */
    }
}