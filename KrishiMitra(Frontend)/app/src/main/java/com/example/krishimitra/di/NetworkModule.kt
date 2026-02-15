package com.example.krishimitra.di

import android.content.Context
import com.example.krishimitra.data.local.dao.WeatherDao
import com.example.krishimitra.data.remote.CropDiseasePredictionApiService
import com.example.krishimitra.data.remote.MandiPriceApiService
import com.example.krishimitra.data.remote.WeatherApiService
import com.example.krishimitra.data.remote_meidator.WeatherRemoteMediator
import com.example.krishimitra.data.repo.NetworkConnectivityObserverImpl
import com.example.krishimitra.domain.repo.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    const val MANDI_PRICE_RETROFIT = "mandi_price"
    const val DISEASE_PREDICTION_RETROFIT = "disease_prediction"
    const val WEATHER_DATA_RETROFIT = "weather_data"


    @Provides
    @Singleton
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context
    ): NetworkConnectivityObserver {
        return NetworkConnectivityObserverImpl(
            context = context,
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        )
    }


    @Provides
    @Singleton
    @Named(DISEASE_PREDICTION_RETROFIT)
    fun provideDiseasePredictionRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("https://7954b4a3435f.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideCropDiseasePredictionApi(
        @Named(DISEASE_PREDICTION_RETROFIT) retrofit: Retrofit
    ): CropDiseasePredictionApiService {
        return retrofit.create(CropDiseasePredictionApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMandiPriceApi(
        @Named(MANDI_PRICE_RETROFIT) retrofit: Retrofit
    ): MandiPriceApiService {
        return retrofit.create(MandiPriceApiService::class.java)
    }


    @Provides
    @Singleton
    @Named(WEATHER_DATA_RETROFIT)
    fun provideWeatherDataApi(

    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(
        @Named(WEATHER_DATA_RETROFIT) retrofit: Retrofit
    ): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }


    @Provides
    @Singleton
    @Named(MANDI_PRICE_RETROFIT)
    fun provideMandiPriceRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(MandiPriceApiService.mandi_base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideWeatherRemoteMediator(
        weatherApi: WeatherApiService,
        weaterDao: WeatherDao,
        networkConnectivityObserver: NetworkConnectivityObserver

    ): WeatherRemoteMediator {
        return WeatherRemoteMediator(
            api = weatherApi,
            dao = weaterDao,
            networkObserver = networkConnectivityObserver
        )
    }


}