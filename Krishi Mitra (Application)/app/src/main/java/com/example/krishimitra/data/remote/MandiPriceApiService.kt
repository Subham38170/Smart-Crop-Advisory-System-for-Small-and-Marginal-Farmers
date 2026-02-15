package com.example.krishimitra.data.remote

import com.example.krishimitra.Constants
import com.example.krishimitra.domain.model.mandi_data.MandiPriceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MandiPriceApiService {


    companion object {
        const val mandi_base_url = "https://api.data.gov.in/"
        const val mandi_relative_url = "resource/9ef84268-d588-465a-a308-a864a43d0070"
    }



    @GET(mandi_relative_url)
    suspend fun getMandiPrices(
        @Query("api-key") apiKey: String = Constants.MANDI_API_KEY,
        @Query("format") format: String = "json",
        @Query("offset") offset: Int?= null,
        @Query("limit") limit: Int? = null,
        @Query("filters[state.keyword]") state: String? = null,
        @Query("filters[district]") district: String? = null,
        @Query("filters[market]") market: String? = null,
        @Query("filters[commodity]") commodity: String? = null,
        @Query("filters[variety]") variety: String? = null,
        @Query("filters[grade]") grade: String? = null

        ): Response<MandiPriceResponse>




}