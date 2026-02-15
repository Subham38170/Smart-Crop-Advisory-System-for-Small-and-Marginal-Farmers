package com.example.krishimitra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.krishimitra.data.local.entity.WeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(weather: List<WeatherEntity>)


    @Query("SELECT * FROM weather_data")
    suspend fun getALlWeatherData(): List<WeatherEntity>

    @Query("DELETE FROM weather_data")
    suspend fun clearAllWeatherData()


    @Transaction
    suspend fun updateWeatherTransaction(weather: List<WeatherEntity>){
        clearAllWeatherData()
        insertAll(weather)
    }
}
