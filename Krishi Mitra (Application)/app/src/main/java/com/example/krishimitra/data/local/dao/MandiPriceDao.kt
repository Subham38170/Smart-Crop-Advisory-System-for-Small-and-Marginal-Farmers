package com.example.krishimitra.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.krishimitra.data.local.entity.MandiPriceEntity

@Dao
interface MandiPriceDao {

    @Query("SELECT * FROM mandi_data")
    fun getAllMandiPrices(): PagingSource<Int,MandiPriceEntity>

    @Upsert
    suspend fun updateMandiPrices(list: List<MandiPriceEntity>)

    @Query("DELETE FROM mandi_data")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM mandi_data")
    suspend fun getRowCount(): Int
}