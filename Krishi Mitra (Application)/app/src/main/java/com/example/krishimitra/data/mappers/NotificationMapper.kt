package com.example.krishimitra.data.mappers

import com.example.krishimitra.data.local.entity.NotificationEntity
import com.example.krishimitra.domain.model.notification_data.GlobalNotificationData


fun GlobalNotificationData.toEntity(): NotificationEntity{
    return NotificationEntity(
        title = title,
        description = description,
        imageUrl = imageUrl,
        webLink = webLink
    )
}


fun NotificationEntity.toNotificationModel(): GlobalNotificationData{
    return GlobalNotificationData(
        title = title,
        description = description,
        imageUrl = imageUrl,
        webLink = webLink
    )
}