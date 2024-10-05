package com.chirathi.aquaflow.NotificationService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.chirathi.aquaflow.HomeActivity
import com.chirathi.aquaflow.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "NOTIFICATION_CHANNEL"
const val channelName = "com.chirathi.aquaflow.NotificationService"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")
        }
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
            generateNotification(it.title!!, it.body!!)
        }

//        if(remoteMessage.getNotification() != null){
//            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
//        }
    }


    fun getRemoteView(tvNotificationTitle: String, tvNotificationContent: String): RemoteViews {
        val remoteView = RemoteViews("com.chirathi.aquaflow", R.layout.notification_item)

        remoteView.setTextViewText(R.id.tvNotificationTitle, tvNotificationTitle)
        remoteView.setTextViewText(R.id.tvNotificationContent, tvNotificationContent)
        remoteView.setImageViewResource(R.id.ic_stat_ic_notification, R.drawable.ic_stat_ic_notification)

        return remoteView
    }


    fun generateNotification(tvNotificationTitle: String, tvNotificationContent: String){

        val intent = Intent(this,HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,0,intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE)


        //channel id, channel name
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(tvNotificationTitle,tvNotificationContent))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, builder.build())

    }



}