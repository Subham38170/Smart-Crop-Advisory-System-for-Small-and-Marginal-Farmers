package com.example.krishimitra.data.remote

import com.example.krishimitra.domain.model.disease_prediction_data.DiseasePredictionResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface CropDiseasePredictionApiService {

    @Multipart
    @POST("predict")
    suspend fun uploadImage(
        @Query("lang") lang: String,
        @Part file: MultipartBody.Part
    ): Response<DiseasePredictionResponse>


}