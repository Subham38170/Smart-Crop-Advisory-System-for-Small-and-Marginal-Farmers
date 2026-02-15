package com.example.krishimitra.data.remote

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.krishimitra.Constants.dataStore
import com.example.krishimitra.MainActivity
import com.example.krishimitra.NotificationConstants
import com.example.krishimitra.R
import com.example.krishimitra.data.local.entity.NotificationEntity
import com.example.krishimitra.domain.repo.Repo
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class KrishiMitraFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var repo: Repo

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)



    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.v("CloudMessage", "From ${message.from}")

        if (message.data.isNotEmpty()) {
            Log.v("CloudMessage", "Message Data${message.data}")
        }
        if (message.notification != null) {
            Log.v("CloudMessage", "Notification Title: ${message.notification?.title}")
            Log.v("CloudMessage", "Notification Body ${message.notification?.body}")
        }

        val notificationData = mutableMapOf<String, String>()
        message.data.forEach { (key, value) -> notificationData[key] = value }


        message.notification?.let {
            notificationData["title"] = it.title ?: notificationData["title"] ?: "KrishiMitra"
            notificationData["body"] = it.body ?: notificationData["body"] ?: ""

        }


        message.data.let {
            Log.v("CloudMessage", "Message Notification Body ${it["body"]}")
            Log.v("CloudMessage", "Message Notification Body ${it["title"]}")

        }


        if (message.data.isNotEmpty()) {

            showGlobalNotification(notificationData)
        }

        if (message.notification != null) {
            Log.v("Cloud Message", "Notification ${message.notification}")
            Log.v("Cloud Message", "Notification Title ${message.notification!!.title}")
            Log.v("Cloud Message", "Notification Body ${message.notification!!.body}")

        }

        val entity = NotificationEntity(
            title = notificationData["title"] ?: "KrishiMitra",
            description = notificationData["body"] ?: "",
            imageUrl = notificationData["imageUrl"],
            webLink = notificationData["webLink"]
        )
       serviceScope.launch(Dispatchers.IO) {
            repo.setNewNotificationStatus(true)
        }
        serviceScope.launch(Dispatchers.IO){
            repo.saveNotification(entity)
        }

    }

    private fun showGlobalNotification(data: Map<String, String>) {


        val title = data["title"] ?: "KrishiMitra"
        val body = data["body"] ?: ""
        val imageUrl = data["imageUrl"]
        val webLink = data["webLink"]
        val notificationScreenUri =
            "app://krishimitra.com/notifications?title=${title}&body=${body}&imageUrl=${imageUrl}&webLink=${webLink}".toUri()


        val intent = Intent(
            Intent.ACTION_VIEW,
            notificationScreenUri,
            this,
            MainActivity::class.java
        )
            .apply {
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        val requestCode = System.currentTimeMillis().toInt()

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat
            .Builder(this, NotificationConstants.GLOBAL_CHANNEL_ID)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(data["body"]))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher_round)

        with(NotificationManagerCompat.from(this)) {
            notify(requestCode, builder.build())
        }


    }


    @OptIn(DelicateCoroutinesApi::class)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        GlobalScope.launch(Dispatchers.IO) {
            saveFCMToken(token)
        }

    }

    private suspend fun saveFCMToken(token: String) {
        val gckTokenKey = stringPreferencesKey("gcm_token")
        baseContext.dataStore.edit { pref ->
            pref[gckTokenKey] = token
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}


