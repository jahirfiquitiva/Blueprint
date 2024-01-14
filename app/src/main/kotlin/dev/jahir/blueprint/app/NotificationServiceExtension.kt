package dev.jahir.blueprint.app

/* TODO: Remove comment marks to enable
import android.content.Context
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler
import dev.jahir.kuper.app.R
import dev.jahir.frames.extensions.context.color
import dev.jahir.frames.extensions.context.hasNotificationsPermission
import dev.jahir.frames.extensions.context.preferences

class NotificationServiceExtension : OSRemoteNotificationReceivedHandler {
    override fun remoteNotificationReceived(
        context: Context,
        notificationReceivedEvent: OSNotificationReceivedEvent
    ) {
        if (!context.preferences.notificationsEnabled || !context.hasNotificationsPermission) {
            notificationReceivedEvent.complete(null)
            return
        }
        val notification = notificationReceivedEvent.notification
        val mutableNotification = notification.mutableCopy()
        mutableNotification.setExtender { extender ->
            extender.apply {
                color = context.color(R.color.accent)
                setSmallIcon(R.drawable.ic_notification)
            }
        notificationReceivedEvent.complete(mutableNotification)
    }
}
*/
