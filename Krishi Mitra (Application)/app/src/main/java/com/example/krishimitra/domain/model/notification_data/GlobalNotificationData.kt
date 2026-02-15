package com.example.krishimitra.domain.model.notification_data

data class GlobalNotificationData(
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val webLink: String? = null,
    val timeStamp: Long = System.currentTimeMillis()
)