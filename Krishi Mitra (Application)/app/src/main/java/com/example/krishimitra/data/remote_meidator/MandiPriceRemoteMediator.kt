package com.example.krishimitra.data.remote_meidator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.krishimitra.data.local.KrishiMitraDatabase
import com.example.krishimitra.data.local.entity.MandiPriceEntity
import com.example.krishimitra.data.mappers.toEntity
import com.example.krishimitra.data.remote.CropDiseasePredictionApiService
import com.example.krishimitra.data.remote.MandiPriceApiService
import okio.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class MandiPriceRemoteMediator(
    private val localDb: KrishiMitraDatabase,
    private val mandiPriceApi: MandiPriceApiService,
    private val stateFilter: String? = null,
    private val districtFilter: String? = null,
    private val marketFilter: String? = null,
    private val commodityFilter: String? = null,
    private val varietyFilter: String? = null
) : RemoteMediator<Int, MandiPriceEntity>() {
    private var currentOffset = 0

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MandiPriceEntity>
    ): MediatorResult {
        return try {
            val loadOffset = when (loadType) {
                LoadType.REFRESH -> {
                    currentOffset = 0
                    currentOffset
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    currentOffset
                }
            }

            Log.d("MANDI_REMOTE", "Loading data: type=$loadType, offset=$loadOffset")

            val response = mandiPriceApi.getMandiPrices(
                offset = loadOffset,
                limit = state.config.pageSize,
                state = stateFilter,
                district = districtFilter,
                commodity = commodityFilter,
                variety = varietyFilter,
                market = marketFilter
            )


            if (!response.isSuccessful) return MediatorResult.Error(HttpException(response))
            val mandiItems = response.body()?.records ?: emptyList()

            Log.d("MANDI_REMOTE", "Fetched ${mandiItems.size} items from API")
            if (mandiItems.isEmpty()) {
                Log.w(
                    "MANDI_REMOTE",
                    "No items fetched, check filters: state=$stateFilter, district=$districtFilter, commodity=$commodityFilter"
                )
            }

            val mandiEntity = mandiItems.map { it.toEntity() }
            localDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    localDb.mandiPriceDao().clearAll()
                    Log.d("MANDI_DB", "Cleared existing rows for REFRESH")

                }
                localDb.mandiPriceDao().updateMandiPrices(mandiEntity)
                val rowCount = localDb.mandiPriceDao().getRowCount()
                Log.d("MANDI_DB", "Rows in DB after insert: $rowCount")

            }

            currentOffset += mandiItems.size

            val endOfPagination = mandiItems.size < state.config.pageSize

            Log.d("MANDI_REMOTE", "End of pagination: $endOfPagination, new offset: $currentOffset")

            MediatorResult.Success(endOfPaginationReached = endOfPagination)

        } catch (e: IOException) {
            Log.e("MANDI_REMOTE", "IOException: ${e.localizedMessage}")

            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e("MANDI_REMOTE", "HttpException: ${e.localizedMessage}")

            MediatorResult.Error(e)
        } catch (e: Exception) {
            Log.e("MANDI_REMOTE", "Unexpected exception: ${e.localizedMessage}")

            MediatorResult.Error(e)
        }
    }

}