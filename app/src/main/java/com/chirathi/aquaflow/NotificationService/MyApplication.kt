package com.chirathi.aquaflow.NotificationService

import android.app.Application
import com.onesignal.OneSignal

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // OneSignal initialization
       /* OneSignal.setAppId("ffd8de79-4fa2-4ad7-8a51-93edb3147e1f")
        OneSignal.setNotificationOpenedHandler { result ->
            // Handle notification opened
            val data = result.notification.additionalData
            if (data != null) {
                // Extract any custom data if needed
            }
        }

        OneSignal.promptForPushNotifications()*/
    }
}