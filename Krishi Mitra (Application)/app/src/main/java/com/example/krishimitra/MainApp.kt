package com.example.krishimitra

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()

        createGlobalNotificationChannel()
    }

    private fun createGlobalNotificationChannel() {

        val channelName = "Farmer Global Notification"
        val channelDescription = "Notifications for farmers about updates, alerts, and news"
        val importance = NotificationManager.IMPORTANCE_HIGH


        val channel =
            NotificationChannel(NotificationConstants.GLOBAL_CHANNEL_ID, channelName, importance)
                .apply {
                    description = channelDescription
                }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)


    }
}