package com.example.krishimitra.di

import android.content.Context
import androidx.room.Room
import com.example.krishimitra.data.local.KrishiMitraDatabase
import com.example.krishimitra.data.local.dao.MandiPriceDao
import com.example.krishimitra.data.local.dao.NotificationDao
import com.example.krishimitra.data.local.dao.WeatherDao
import com.example.krishimitra.data.repo.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LocalDbModule {

    @Provides
    @Singleton
    fun provideKrishiMitraDatabase(
        @ApplicationContext context: Context
    ): KrishiMitraDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = KrishiMitraDatabase::class.java,
            name = "krishi_mitra_db"
        ).build()

    }

    @Provides
    @Singleton
    fun provideNotificationDao(
        krishiMitraDatabase: KrishiMitraDatabase
    ): NotificationDao {
        return krishiMitraDatabase.notificationDao()
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(
        @ApplicationContext context: Context
    ): DataStoreManager {
        return DataStoreManager(context)
    }


    @Provides
    @Singleton
    fun provideWeatherDao(
        krishiMitraDatabase: KrishiMitraDatabase
    ): WeatherDao {
        return krishiMitraDatabase.weatherDataDao()
    }




    @Provides
    @Singleton
    fun provideMandiPriceDao(
        krishiMitraDatabase: KrishiMitraDatabase
    ): MandiPriceDao {
        return krishiMitraDatabase.mandiPriceDao()
    }

}