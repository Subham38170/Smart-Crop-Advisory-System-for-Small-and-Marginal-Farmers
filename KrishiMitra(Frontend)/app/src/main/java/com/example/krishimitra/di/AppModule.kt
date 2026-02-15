package com.example.krishimitra.di

import android.content.Context
import com.example.krishimitra.data.repo.LanguageManager
import com.example.krishimitra.data.repo.LocationManager
import com.example.krishimitra.data.repo.TextToSpeechManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(
        @ApplicationContext context: Context
    ): Context = context

    @Provides
    @Singleton
    fun provideLanguageManager(
        @ApplicationContext context: Context
    ): LanguageManager {
        return LanguageManager(context)
    }

    @Provides
    @Singleton
    fun provideFusedLocationClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationManager(
        @ApplicationContext context: Context,
        fusedLocationClient: FusedLocationProviderClient
    ): LocationManager {
        return LocationManager(
            context = context,
            fusedLocationClient = fusedLocationClient
        )
    }

    @Singleton
    @Provides
    fun provideTextToSpeechManager(
        @ApplicationContext context: Context
    ): TextToSpeechManager {
        return TextToSpeechManager(
            context = context
        )
    }




}