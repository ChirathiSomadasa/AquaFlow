package com.chirathi.aquaflow.NotificationService

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.chirathi.aquaflow.*
import com.chirathi.aquaflow.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "NOTIFICATION_CHANNEL"
const val channelName = "com.chirathi.aquaflow"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.getNotification() != null){
            val title = remoteMessage.notification!!.title!!
            val message = remoteMessage.notification!!.body!!

            // Store notification in Firestore
//            storeNotificationInFirestore(title, message)

            // Display the notification
            generateNotification(title, message)
        }
    }


    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews("com.chirathi.aquaflow.NotificationService", R.layout.notification)

        remoteView.setTextViewText(R.id.Ntitle, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.ic_notifications_active, R.drawable.ic_notifications_active)

        return remoteView
    }


    fun generateNotification(title: String, message: String){

        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,0,intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)


        //channel id, channel name
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notifications_active)
            .setAutoCancel(true)
//            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title,message))

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