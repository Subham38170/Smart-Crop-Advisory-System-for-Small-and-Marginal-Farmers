package com.example.krishimitra.presentation.mandi_screen

import com.example.krishimitra.domain.model.mandi_data.MandiPriceDto
import com.example.krishimitra.domain.repo.NetworkStatus

data class MandiScreenState(
    val error: String? = null,
    val isLoading: Boolean = false,
    val mandiPriceList: List<MandiPriceDto> = emptyList(),
    val state: String = "",
    val district: String = "",
    val listOfStates: List<String> = emptyList(),
    val listOfDistricts: List<String> = emptyList(),
    val networkStatus: NetworkStatus = NetworkStatus.Disconnected
)