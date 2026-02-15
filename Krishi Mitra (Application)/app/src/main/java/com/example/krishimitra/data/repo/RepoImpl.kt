package com.example.krishimitra.data.repo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.net.toUri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.krishimitra.FirebaseConstants
import com.example.krishimitra.data.local.KrishiMitraDatabase
import com.example.krishimitra.data.local.dao.NotificationDao
import com.example.krishimitra.data.local.entity.MandiPriceEntity
import com.example.krishimitra.data.local.entity.NotificationEntity
import com.example.krishimitra.data.remote.CropDiseasePredictionApiService
import com.example.krishimitra.data.remote.MandiPriceApiService
import com.example.krishimitra.data.remote.WeatherApiService
import com.example.krishimitra.data.remote_meidator.MandiPriceRemoteMediator
import com.example.krishimitra.data.remote_meidator.WeatherRemoteMediator
import com.example.krishimitra.domain.ResultState
import com.example.krishimitra.domain.model.crops_data.CropModel
import com.example.krishimitra.domain.model.disease_prediction_data.DiseasePredictionResponse
import com.example.krishimitra.domain.model.farmer_data.UserDataModel
import com.example.krishimitra.domain.model.feedback.FeedbackData
import com.example.krishimitra.domain.model.govt_scheme_slider.BannerModel
import com.example.krishimitra.domain.model.location.Location
import com.example.krishimitra.domain.model.mandi_data.MandiPriceDto
import com.example.krishimitra.domain.repo.NetworkConnectivityObserver
import com.example.krishimitra.domain.repo.NetworkStatus
import com.example.krishimitra.domain.repo.Repo
import com.example.krishimitra.domain.model.weather_data.DailyWeather
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.UUID
import javax.inject.Inject

class RepoImpl @Inject constructor(
    private val networkConnectivityObserver: NetworkConnectivityObserver,
    private val firestoreDb: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth,
    private val languageManager: LanguageManager,
    private val locationManager: LocationManager,
    private val mandiApiService: MandiPriceApiService,
    private val diseasePredictionApiService: CropDiseasePredictionApiService,
    private val weatherApiService: WeatherApiService,
    private val notificationDao: NotificationDao,
    private val localDb: KrishiMitraDatabase,
    private val dataStoreManager: DataStoreManager,
    private val weatherRemoteMediator: WeatherRemoteMediator,
     private val context: Context
) : Repo {
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun getLocation(): Location? {
        return locationManager.getLocation()
    }


    override fun getLanguage(): Flow<String> {
        return languageManager.getLanguage()
    }

    override suspend fun changeLanguage(lang: String) {
        languageManager.updateLanguage(lang)
    }

    override fun requestLocationPermission(activity: Activity) {
        locationManager.requestEnableLocation(activity = activity)
    }

    override suspend fun storeUserData(userData: UserDataModel) {
        dataStoreManager.storeUserData(userData)
    }

    override fun getUserData(): Flow<UserDataModel> {
        return dataStoreManager.getUser()
    }


    override fun getUserName(): Flow<String> {
        return dataStoreManager.getUserName()
    }

    override fun getStateName(): Flow<String> {
        return dataStoreManager.getStateName()
    }

    override suspend fun getMandiPrices(
        offset: Int?,
        limit: Int?,
        state: String?,
        district: String?,
        market: String?,
        commodity: String?,
        variety: String?,
        grade: String?
    ): Flow<ResultState<List<MandiPriceDto>>> {
        return callbackFlow {
            trySend(ResultState.Loading)

            try {
                val response = mandiApiService.getMandiPrices(
                    offset = offset,
                    limit = limit,
                    state = state,
                    district = district,
                    market = market,
                    commodity = commodity,
                    variety = variety,
                    grade = grade
                )
                Log.d("API_DATA", response.body()?.records.toString())
                if (response.isSuccessful) {
                    trySend(ResultState.Success(response.body()?.records ?: emptyList()))

                } else {
                    trySend(ResultState.Error("Something went wrong? ${response.message()}"))
                }
            } catch (e: Exception) {
                trySend(ResultState.Error(e.message.toString()))
            }

            awaitClose { close() }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getMandiPricesPaging(
        state: String?,
        district: String?,
        market: String?,
        commodity: String?,
        variety: String?,
        grade: String?
    ): Flow<PagingData<MandiPriceEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = MandiPriceRemoteMediator(
                localDb = localDb,
                mandiPriceApi = mandiApiService,
                stateFilter = state,
                districtFilter = district,
                marketFilter = market,
                varietyFilter = variety,
                commodityFilter = commodity
            ),
            pagingSourceFactory = {
                localDb.mandiPriceDao().getAllMandiPrices()
            }

        ).flow
    }

    override fun loadGovtSchemes(): Flow<ResultState<List<BannerModel>>> {
        return callbackFlow {
            trySend(ResultState.Loading)
            try {
                firestoreDb.collection(FirebaseConstants.GOVT_SCHEMES)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            trySend(ResultState.Error(e.message.toString()))
                            return@addSnapshotListener
                        }
                        snapshot?.let {
                            val schemes = it.toObjects(BannerModel::class.java)
                            trySend(ResultState.Success(schemes))
                        }
                    }
            } catch (e: Exception) {
                trySend(ResultState.Error(e.message.toString()))

            }
            awaitClose { close() }
        }

    }


    override fun predictCropDisease(
        lang: String,
        filePath: Uri
    ): Flow<ResultState<DiseasePredictionResponse>> {
        return callbackFlow {
            trySend(ResultState.Loading)
            try {
                val imagePart = uriToMultipart(context, filePath)
                val response = diseasePredictionApiService.uploadImage(lang, imagePart)
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.let {
                        trySend(ResultState.Success(result))
                    }
                } else {
                    trySend(ResultState.Error(response.message()))
                }
            } catch (e: Exception) {
                trySend(ResultState.Error(e.message.toString()))
            }
            awaitClose { close() }
        }
    }

    override suspend fun loadKrishiNews(): Flow<ResultState<List<BannerModel>>> {
        return callbackFlow {
            trySend(ResultState.Loading)
            try {
                firestoreDb.collection(FirebaseConstants.KRISHI_NEWS)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            trySend(ResultState.Error(e.message.toString()))
                            return@addSnapshotListener
                        }
                        snapshot?.let {
                            val news = it.toObjects(BannerModel::class.java)
                            trySend(ResultState.Success(news))
                        }
                    }
            } catch (e: Exception) {
                trySend(ResultState.Error(e.message.toString()))

            }
            awaitClose { close() }
        }
    }

    override suspend fun sellCrop(cropData: CropModel): Flow<ResultState<Boolean>> {
        return callbackFlow {
            trySend(ResultState.Loading)
            try {
                if (cropData.imageUrl != null) {
                    val fileName = "${firebaseAuth.uid}{System.currentTimeMillis()}.jpg"
                    val storageRef = firebaseStorage
                        .getReference(FirebaseConstants.CROP_BAZAR_IMAGES)
                        .child(fileName)


                    val documentPath = UUID.randomUUID().toString()

                    storageRef.putFile(cropData.imageUrl.toUri())
                        .addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { link ->
                                firestoreDb.collection(FirebaseConstants.CROP_BAZAR)
                                    .document(documentPath)
                                    .set(
                                        cropData.copy(
                                            imageUrl = link.toString(),
                                            cropId = documentPath
                                        )
                                    )
                                    .addOnSuccessListener {
                                        trySend(ResultState.Success(true))
                                    }
                                    .addOnFailureListener {
                                        trySend(ResultState.Error(it.message.toString()))
                                    }
                            }.addOnFailureListener {
                                trySend(ResultState.Error(it.message.toString()))

                            }
                        }
                } else {
                    firestoreDb.collection(FirebaseConstants.CROP_BAZAR)
                        .document()
                        .set(cropData)
                        .addOnSuccessListener {
                            trySend(ResultState.Success(true))
                        }
                        .addOnFailureListener {
                            trySend(ResultState.Error(it.message.toString()))
                        }
                }
            } catch (e: Exception) {
                trySend(ResultState.Error(e.message.toString()))

            }
            awaitClose {
                close()
            }
        }

    }


    override suspend fun getAllBuyingCrops(): Flow<ResultState<List<CropModel>>> {
        return callbackFlow {
            trySend(ResultState.Loading)

            try {
                firestoreDb.collection(FirebaseConstants.CROP_BAZAR)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(ResultState.Error(error.message.toString()))
                            return@addSnapshotListener
                        }

                        if (snapshot != null) {
                            val crops = snapshot.toObjects(CropModel::class.java)
                            trySend(ResultState.Success(crops))

                        } else {
                            trySend(ResultState.Success(emptyList()))
                        }
                    }
            } catch (e: Exception) {
                trySend(ResultState.Error(e.message.toString()))

            }
            awaitClose { close() }
        }
    }

    override suspend fun getMyListedItems(): Flow<ResultState<List<CropModel>>> {
        return callbackFlow {
            trySend(ResultState.Loading)
            try {
                firestoreDb.collection(FirebaseConstants.CROP_BAZAR)
                    .whereEqualTo("farmerId", firebaseAuth.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(ResultState.Error(error.message.toString()))
                            return@addSnapshotListener
                        }
                        snapshot?.let {
                            val crops = it.toObjects(CropModel::class.java)

                            trySend(ResultState.Success(crops))
                        }

                    }
            } catch (e: Exception) {
                trySend(ResultState.Error(e.message.toString()))

            }
            awaitClose { close() }
        }
    }

    override suspend fun getUserDataFromFirebase(): Flow<ResultState<UserDataModel>> {
        return callbackFlow {

            firestoreDb
                .collection(FirebaseConstants.USERS)
                .document(firebaseAuth.uid ?: "")
                .get()
                .addOnSuccessListener {
                    val userData = it.toObject(UserDataModel::class.java)
                    userData?.let {
                        trySend(ResultState.Success(userData))
                    }
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.message.toString()))
                }
            awaitClose { close() }
        }

    }

    override suspend fun searchBuyingCrops(cropName: String): Flow<ResultState<List<CropModel>>> {
        return callbackFlow {
            try {
                trySend(ResultState.Loading)
                firestoreDb
                    .collection(FirebaseConstants.CROP_BAZAR)
                    .whereEqualTo("cropName", cropName)
                    .addSnapshotListener { result, e ->
                        if (e != null) {
                            trySend(ResultState.Error(e.message.toString()))
                        }
                        result?.let {
                            val crops = it.toObjects(CropModel::class.java)
                            trySend(ResultState.Success(crops))
                        }
                    }
            } catch (e: Exception) {
                trySend(ResultState.Error(e.message.toString()))

            }
            awaitClose { close() }
        }
    }

    override suspend fun deleteListedCrop(cropModel: CropModel): Flow<ResultState<Boolean>> {
        return callbackFlow {
            trySend(ResultState.Loading)
            try {
                firestoreDb
                    .collection(FirebaseConstants.CROP_BAZAR)
                    .document(cropModel.cropId.toString())
                    .delete()
                    .addOnSuccessListener {
                        deleteImagefromCropBazar(cropModel.imageUrl.toString())
                        trySend(ResultState.Success(true))

                    }
                    .addOnFailureListener {
                        trySend(ResultState.Error(it.message.toString()))
                    }
            } catch (e: Exception) {
                trySend(ResultState.Error(e.message.toString()))

            }
            awaitClose { close() }

        }
    }

    override suspend fun getWeatherData(
        lat: Double,
        long: Double
    ): Flow<ResultState<List<DailyWeather>>> {
        return weatherRemoteMediator.getWeather(lat, long)

    }

    override suspend fun getAllNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotifications()
    }

    override suspend fun saveNotification(notification: NotificationEntity) {
        notificationDao.insertNotification(notification)
    }

    override suspend fun clearAllNotification() {
        notificationDao.clearAll()
    }

    override suspend fun newNotificationStatus(): Flow<Boolean> {
        return dataStoreManager.newNotificationStatus()
    }

    override suspend fun setNewNotificationStatus(status: Boolean) {
        try {
            dataStoreManager.setNotificationStatus(status)
        } catch (e: Exception) {

        }
    }

    override fun networkStatus(): Flow<NetworkStatus> {
        return networkConnectivityObserver.networkStatus
    }

    override suspend fun sendFeedback(feedbackData: FeedbackData): Flow<ResultState<Boolean>> {
        return callbackFlow {
            trySend(ResultState.Loading)
            try {

                firestoreDb.collection(FirebaseConstants.USER_FEEDBACK)
                    .document()
                    .set(feedbackData.copy(uid = firebaseAuth.uid ?: "Unknown"))
                    .addOnSuccessListener {
                        trySend(ResultState.Success(true))
                    }
                    .addOnFailureListener {
                        trySend(ResultState.Error(it.message.toString()))
                    }
            } catch (e: Exception) {
                trySend(ResultState.Error(e.message.toString()))
            }
            awaitClose { close() }
        }
    }


    private fun deleteImagefromCropBazar(imageUrl: String) {
        try {
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
            storageRef.delete()

        } catch (e: Exception) {

        }

    }

    fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val fileBytes = inputStream.readBytes()
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), fileBytes)
        val fileName = "image.jpg" // you can extract name from uri if needed
        return MultipartBody.Part.createFormData("file", fileName, requestBody)
    }


}