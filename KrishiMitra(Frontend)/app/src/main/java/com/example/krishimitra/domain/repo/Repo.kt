package com.example.krishimitra.domain.repo

import android.app.Activity
import android.net.Uri
import androidx.paging.PagingData
import com.example.krishimitra.data.local.entity.MandiPriceEntity
import com.example.krishimitra.data.local.entity.NotificationEntity
import com.example.krishimitra.domain.ResultState
import com.example.krishimitra.domain.model.crops_data.CropModel
import com.example.krishimitra.domain.model.disease_prediction_data.DiseasePredictionResponse
import com.example.krishimitra.domain.model.farmer_data.UserDataModel
import com.example.krishimitra.domain.model.feedback.FeedbackData
import com.example.krishimitra.domain.model.govt_scheme_slider.BannerModel
import com.example.krishimitra.domain.model.location.Location
import com.example.krishimitra.domain.model.mandi_data.MandiPriceDto
import com.example.krishimitra.domain.model.weather_data.DailyWeather
import kotlinx.coroutines.flow.Flow

interface Repo {


    suspend fun getLocation(): Location?

    fun getLanguage(): Flow<String>

    suspend fun changeLanguage(lang: String)

    fun requestLocationPermission(activity: Activity)

    suspend fun storeUserData(userData: UserDataModel)

    fun getUserData(): Flow<UserDataModel>

    fun getUserName(): Flow<String>

    fun getStateName(): Flow<String>

    suspend fun getMandiPrices(
        offset: Int? = null,
        limit: Int? = null,
        state: String? = null,
        district: String? = null,
        market: String? = null,
        commodity: String? = null,
        variety: String? = null,
        grade: String? = null
    ): Flow<ResultState<List<MandiPriceDto>>>


    fun getMandiPricesPaging(
        state: String? = null,
        district: String? = null,
        market: String? = null,
        commodity: String? = null,
        variety: String? = null,
        grade: String? = null
    ): Flow<PagingData<MandiPriceEntity>>


    fun loadGovtSchemes(): Flow<ResultState<List<BannerModel>>>

    fun predictCropDisease(
        lang: String,
        filePath: Uri
    ): Flow<ResultState<DiseasePredictionResponse>>

    suspend fun loadKrishiNews(): Flow<ResultState<List<BannerModel>>>


    suspend fun sellCrop(cropData: CropModel): Flow<ResultState<Boolean>>


    suspend fun getAllBuyingCrops(): Flow<ResultState<List<CropModel>>>

    suspend fun getMyListedItems(): Flow<ResultState<List<CropModel>>>


    suspend fun getUserDataFromFirebase(): Flow<ResultState<UserDataModel>>


    suspend fun searchBuyingCrops(cropName: String): Flow<ResultState<List<CropModel>>>
    suspend fun deleteListedCrop(cropModel: CropModel): Flow<ResultState<Boolean>>


    suspend fun getWeatherData(
        lat: Double,
        long: Double
    ): Flow<ResultState<List<DailyWeather>>>


    suspend fun getAllNotifications(): Flow<List<NotificationEntity>>

    suspend fun saveNotification(notification: NotificationEntity)

    suspend fun clearAllNotification()

    suspend fun newNotificationStatus(): Flow<Boolean>

    suspend fun setNewNotificationStatus(status: Boolean)

    fun networkStatus(): Flow<NetworkStatus>

    suspend fun sendFeedback(feedbackData: FeedbackData): Flow<ResultState<Boolean>>


}
