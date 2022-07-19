package com.example.arkbabytracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.arkbabytracker.MainActivity
import com.example.arkbabytracker.R
import kotlinx.coroutines.*

class TimerService : Service() {

    lateinit var timer: Job
    override fun onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Dino Timer Channel"
            val descriptionText = "Displays dino timer notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val seconds = intent.extras?.getInt("seconds")?:0
        Toast.makeText(this, "Starting timer for ${TimeDisplayUtil.secondsToString(seconds)}", Toast.LENGTH_SHORT).show()
        val tapIntent = Intent(this,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,tapIntent,PendingIntent.FLAG_IMMUTABLE)
        val scope = this
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ark_logo)
            .setContentTitle("Dino Timer")
            .setContentText("Timer is up! Feed your Dinos")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        timer = CoroutineScope(Dispatchers.IO).launch{
            delay(seconds.toLong() * 1000)
            with(NotificationManagerCompat.from(scope)){
                notify(0,builder.build())
            }
        }

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
    }

    companion object {
        const val CHANNEL_ID = "DinoChannel"
    }
}