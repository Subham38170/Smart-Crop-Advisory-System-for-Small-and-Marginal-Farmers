package com.example.krishimitra.data.mappers

import com.example.krishimitra.data.local.entity.WeatherEntity
import com.example.krishimitra.domain.model.weather_data.DailyWeather
import com.example.krishimitra.domain.model.weather_data.WeatherResponse
import java.text.SimpleDateFormat
import java.util.Locale


fun List<DailyWeather>.toEntityList(): List<WeatherEntity> {
    return this.map { daily ->
        WeatherEntity(
            date = daily.date,
            temp = daily.temp,
            condition = daily.condition,
            icon = daily.icon,
            rainfall = daily.rainfall
        )
    }
}

fun List<WeatherEntity>.toDomainList(): List<DailyWeather> {
    return this.map { daily ->
        DailyWeather(
            date = daily.date,
            temp = daily.temp,
            condition = daily.condition,
            icon = daily.icon,
            rainfall = daily.rainfall
        )
    }
}
fun WeatherResponse.toDailyWeather(): List<DailyWeather> {
    val sdfInput = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val sdfGroup = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // for grouping
    val sdfOutput = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) // for formatted date

    val dailyMap = this.list.groupBy { item ->
        sdfGroup.format(sdfInput.parse(item.dt_txt)!!)
    }

    return dailyMap.map { (date, items) ->
        val first = items.first()
        val avgTemp = kelvinToCelsius(items.map { it.main.temp }.average())
        val totalRain = items.sumOf { it.rain?.`3h` ?: 0.0 }

        // Pick the item with the most severe condition
        val severeItem = items.maxByOrNull { severity(it.weather.first().description) } ?: first
        val condition = severeItem.weather.first().description

        DailyWeather(
            date = sdfOutput.format(sdfInput.parse(first.dt_txt)!!), // formatted date here
            temp = avgTemp,
            condition = condition,
            icon = condition,
            rainfall = totalRain
        )
    }
}


// Helper to prioritize conditions
fun severity(desc: String): Int {
    return when(desc.lowercase()) {
        "clear", "sunny" -> 1
        "partly cloudy", "clouds" -> 2
        "drizzle" -> 3
        "rain" -> 4
        "heavy rain" -> 5
        "thunderstorm" -> 6
        else -> 0
    }
}

// Optional: convert Kelvin to Celsius
fun kelvinToCelsius(kelvin: Double): Double = kelvin - 273.15