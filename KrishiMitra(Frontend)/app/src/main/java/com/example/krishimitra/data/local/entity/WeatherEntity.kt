package com.example.krishimitra.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "weather_data")
data class WeatherEntity(
    @PrimaryKey
    val date: String,
    val temp: Double,
    val condition: String,
    val icon: String,
    val rainfall: Double
)

