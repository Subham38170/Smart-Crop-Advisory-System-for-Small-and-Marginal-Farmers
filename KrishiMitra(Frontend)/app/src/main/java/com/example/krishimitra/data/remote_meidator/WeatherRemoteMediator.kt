package com.example.krishimitra.data.remote_meidator

import com.example.krishimitra.data.local.dao.WeatherDao
import com.example.krishimitra.data.mappers.toDailyWeather
import com.example.krishimitra.data.mappers.toDomainList
import com.example.krishimitra.data.mappers.toEntityList
import com.example.krishimitra.data.remote.WeatherApiService
import com.example.krishimitra.domain.ResultState
import com.example.krishimitra.domain.repo.NetworkConnectivityObserver
import com.example.krishimitra.domain.repo.NetworkStatus
import com.example.krishimitra.domain.model.weather_data.DailyWeather
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRemoteMediator @Inject constructor(
    private val api: WeatherApiService,
    private val dao: WeatherDao,
    private val networkObserver: NetworkConnectivityObserver
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getWeather(lat: Double, long: Double): Flow<ResultState<List<DailyWeather>>> =
        networkObserver.networkStatus.flatMapLatest { status ->
            flow {
                emit(ResultState.Loading)
                val localData = dao.getALlWeatherData().toDomainList()

                if (status == NetworkStatus.Connected) {
                    try {
                        val remoteData = api.getWeatherForecast(lat, long)
                        if (remoteData.isSuccessful) {
                            remoteData.body()?.let { weatherResponse ->
                                val dailyWeather = weatherResponse.toDailyWeather()
                                dao.updateWeatherTransaction(dailyWeather.toEntityList())
                                emit(ResultState.Success(dailyWeather))
                            } ?: run {
                                if (localData.isNotEmpty()) emit(ResultState.Success(localData))
                                else emit(ResultState.Error("Empty API response"))
                            }
                        } else {
                            if (localData.isNotEmpty()) emit(ResultState.Success(localData))
                            else emit(ResultState.Error(remoteData.message() ?: "API Error"))
                        }
                    } catch (e: Exception) {
                        if (localData.isNotEmpty()) emit(ResultState.Success(localData))
                        else emit(ResultState.Error(e.message ?: "Unknown error"))
                    }
                } else {
                    if (localData.isNotEmpty()) emit(ResultState.Success(localData))
                    else emit(ResultState.Error("No internet available"))
                }
            }
        }.catch { e ->
            emit(ResultState.Error(e.message ?: "Unexpected error"))
        }


}