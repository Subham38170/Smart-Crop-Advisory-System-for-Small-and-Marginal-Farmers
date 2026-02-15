package com.example.krishimitra.domain.model.location

data class Location(
    val village: String = "",
    val district: String = "",
    val state: String = "",
    val pinCode: String = "",
    val longitude: Double = 28.6139,
    val latitude: Double = 77.2090
)