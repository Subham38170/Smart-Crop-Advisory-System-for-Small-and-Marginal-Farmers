package com.example.krishimitra.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.krishimitra.data.local.dao.MandiPriceDao
import com.example.krishimitra.data.local.dao.NotificationDao
import com.example.krishimitra.data.local.dao.WeatherDao
import com.example.krishimitra.data.local.entity.MandiPriceEntity
import com.example.krishimitra.data.local.entity.NotificationEntity
import com.example.krishimitra.data.local.entity.WeatherEntity


@Database(
    entities = [MandiPriceEntity::class, WeatherEntity::class, NotificationEntity::class],
    version = 1
)
abstract class KrishiMitraDatabase : RoomDatabase() {

    abstract fun mandiPriceDao(): MandiPriceDao


    abstract fun weatherDataDao(): WeatherDao


    abstract fun notificationDao(): NotificationDao


}