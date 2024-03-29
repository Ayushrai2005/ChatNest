package ayush.chatnest

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            // Handle the data payload.

        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            // Handle the notification message here.
            // For example, you can send a notification to the user with the notification content.
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // If you want to send messages to this application instance or
        // manage this app's subscriptions on the server side, send the
        // Instance ID token to your app server.
    }
}