package com.example.rez.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.rez.R
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.ui.fragment.dashboard.NotificationFragment
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


const val TAG_NOTIFICATION_RECEIVED_FROM_BACKEND = "TAG_NOTIFICATION_RECEIVED_2"
const val INT_FIREBASE_PENDING_INTENT_REQUEST_CODE = 700
const val channelId = "rez_channel"
class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("FROMTAG", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("NEWTAG", "Message data payload: ${remoteMessage.data}")
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("NOTIFYTAG", "Message Notification Body: ${it.body}")
            it.body?.let { it1 -> sendNotification(it1) }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(remoteMessage: String) {
//        val intent = Intent(this, DashboardActivity::class.java)
//        //intent.action = STRING_FIREBASE_INTENT_ACTION
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//        intent.putExtra(TAG_NOTIFICATION_RECEIVED_FROM_BACKEND, true)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            INT_FIREBASE_PENDING_INTENT_REQUEST_CODE /* Request code */,
//            intent,
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
//        )

//        val bundle = Bundle()
//        val pendingIntent = NavDeepLinkBuilder(this)
//            .setGraph(R.navigation.dashboard_nav_graph)
//            .setDestination(R.id.notificationFragment)
//            .createPendingIntent()

        val newIntent = Intent(applicationContext, DashboardActivity::class.java)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val newMsg = "remoteMessage"
        newIntent.putExtra("PUSHNOTIFICATION", newMsg)
        Log.d("NOTSEND", newMsg)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val inboxStyle = NotificationCompat.InboxStyle()
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(newIntent)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Rez")
         //   .setStyle(NotificationCompat.BigTextStyle().bigText(remoteMessage))
            .setSmallIcon(R.drawable.app_icon_new)
            //.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background))
            .setContentText(remoteMessage)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(inboxStyle)
            .setContentIntent(pendingIntent)


        //val notificationManager = NotificationManagerCompat.from(applicationContext)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "REZ",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(Random().nextInt() /* ID of notification */, notificationBuilder.build())
    }

//    private fun generateNotification(context: Context, message: String) {
//        val icon: Int = R.drawable.app_icon_new
//        val `when` = System.currentTimeMillis()
//        val notificationManager = context
//            .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        // Notification notification = new Notification(icon, message, when);
//        var notificationBuilder: NotificationCompat.Builder? = null
//        val title = context.getString(R.string.app_name)
//        val requestID = System.currentTimeMillis().toInt()
//        val notificationIntent = Intent(context, DashboardActivity::class.java)
//
//        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
//                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        val intent = PendingIntent.getActivity(
//            context, requestID,
//            notificationIntent, 0
//        )
//        // notification.setLatestEventInfo(context, title, message, intent);
//        // notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notificationBuilder = NotificationCompat.Builder(
//            applicationContext
//        ).setWhen(`when`).setContentText(message)
//            .setContentTitle(title).setSmallIcon(icon).setAutoCancel(true)
//            .setDefaults(Notification.DEFAULT_LIGHTS)
//            .setContentIntent(intent)
//        val defaultRingtoneUri: Uri = RingtoneManager
//            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        notificationBuilder.setSound(defaultRingtoneUri)
//        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
//        val notification = notificationBuilder.build()
//        notificationManager.notify(`when`.toInt(), notification)
//        Log.v("My Message is", message)
//        notificationClicked = true
//    }


}
