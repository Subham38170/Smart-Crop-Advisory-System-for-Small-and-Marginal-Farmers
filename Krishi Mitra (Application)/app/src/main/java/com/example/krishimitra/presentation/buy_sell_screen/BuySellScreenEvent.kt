package com.example.krishimitra.presentation.buy_sell_screen

import com.example.krishimitra.domain.model.crops_data.CropModel

sealed class BuySellScreenEvent {

    data class onSearch(val searching: String): BuySellScreenEvent()
    data class onSuggestionSearch(val suggestion: String): BuySellScreenEvent()

    data class onListProduct(val cropData: CropModel): BuySellScreenEvent()

    data class onCropSearch(val cropName: String): BuySellScreenEvent()

    data object loadAllCrops: BuySellScreenEvent()

    data class deleteSellCrop(val cropModel: CropModel): BuySellScreenEvent()

}