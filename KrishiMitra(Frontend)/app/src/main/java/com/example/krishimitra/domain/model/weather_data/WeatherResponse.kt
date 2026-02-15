package com.example.krishimitra.domain.model.weather_data


data class WeatherResponse(
    val list: List<WeatherItem>
)


data class WeatherItem(
    val dt_txt: String,             // Date and time, e.g., "2025-09-22 15:00:00"
    val main: Main,
    val weather: List<WeatherDescription>,
    val rain: Rain? = null
)


data class Main(
    val temp: Double                // Temperature in Kelvin
)

data class WeatherCondition(
    val main: String,
    val icon: String
)


data class WeatherDescription(
    val description: String         // e.g., "light rain", "overcast clouds"
)

data class Rain(
    val `3h`: Double
)

data class DailyWeather(
    val date: String,
    val temp: Double,
    val condition: String,
    val icon: String,
    val rainfall: Double
)