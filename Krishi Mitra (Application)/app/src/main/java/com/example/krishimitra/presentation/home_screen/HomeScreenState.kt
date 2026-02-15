package com.example.krishimitra.presentation.home_screen

import com.example.krishimitra.domain.model.farmer_data.UserDataModel
import com.example.krishimitra.domain.model.govt_scheme_slider.BannerModel
import com.example.krishimitra.domain.model.weather_data.DailyWeather

data class HomeScreenState(
    val currentLanguage: String = "English",
    val schemeBannersList: List<BannerModel> = emptyList(),
    val isBannersLoading: Boolean = false,
    val isKrishiNewsLoading: Boolean = false,
    val krishiNewsBannerList: List<BannerModel> = emptyList(),
    val weatherData: List<DailyWeather> = emptyList(),
    val userData: UserDataModel = UserDataModel(),
    val isWeatherDataLoading: Boolean = false,
    val notificationStatus: Boolean = false
)