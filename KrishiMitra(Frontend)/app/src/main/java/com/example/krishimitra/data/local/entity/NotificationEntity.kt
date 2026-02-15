package com.example.krishimitra.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val webLink: String? = null,
    val timeStamp: Long = System.currentTimeMillis()
)