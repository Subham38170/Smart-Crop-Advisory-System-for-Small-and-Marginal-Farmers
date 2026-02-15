package com.example.krishimitra.domain.model.farmer_data


data class UserDataModel(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val village: String = "New Delhi",
    val district: String = "New Delhi",
    val state: String = "Delhi",
    val mobileNo: String = "",
    val pinCode: String = "110001",
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090
)