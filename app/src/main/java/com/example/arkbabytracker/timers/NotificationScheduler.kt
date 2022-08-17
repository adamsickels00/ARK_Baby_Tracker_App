package com.example.arkbabytracker.timers

import android.R
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.os.SystemClock
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.arkbabytracker.MainActivity
import com.example.arkbabytracker.troughtracker.BabyTroughFragment.Companion.CHANNEL_ID


fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    val name = "Dino Timer Channel"
    val descriptionText = "Displays dino timer notifications"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        description = descriptionText
    }
    // Register the channel with the system
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

fun scheduleNotification(
    context: Context,
    delay: Long,
    notificationId: Int
) { //delay is after how much time(in millis) from current time you want to schedule the notification
    val tapIntent = Intent(context, MainActivity::class.java)

    val onTapIntent = PendingIntent.getActivity(context,0,tapIntent,PendingIntent.FLAG_IMMUTABLE)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(com.example.arkbabytracker.R.drawable.ark_logo)
        .setContentTitle("Dino Timer")
        .setContentText("Timer is up! Feed your Dinos")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(onTapIntent)
        .setAutoCancel(true)

    val activity = PendingIntent.getActivity(
        context,
        notificationId,
        tapIntent,
        PendingIntent.FLAG_IMMUTABLE

    )
    builder.setContentIntent(activity)
    val notification: Notification = builder.build()
    val notificationIntent = Intent(context, MyNotificationPublisher::class.java)
    notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId)
    notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        notificationIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val futureInMillis = SystemClock.elapsedRealtime() + delay*1000
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,futureInMillis,pendingIntent)
    Toast.makeText(context,"Started Timer for $delay seconds",Toast.LENGTH_SHORT).show()
}

class MyNotificationPublisher : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = intent.getParcelableExtra(NOTIFICATION)!!
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        notificationManager.notify(notificationId, notification)

    }

    companion object {
        var NOTIFICATION_ID = "notification_id"
        var NOTIFICATION = "notification"
    }
}