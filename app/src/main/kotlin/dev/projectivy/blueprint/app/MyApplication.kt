package dev.projectivy.blueprint.app

import dev.jahir.frames.ui.FramesApplication

class MyApplication : FramesApplication() {
    override fun onCreate() {
        super.onCreate()
        // No OneSignal — push notifications disabled
    }
}
