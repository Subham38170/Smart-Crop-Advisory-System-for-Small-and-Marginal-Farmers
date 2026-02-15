package com.example.krishimitra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.krishimitra.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY timeStamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>



    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}