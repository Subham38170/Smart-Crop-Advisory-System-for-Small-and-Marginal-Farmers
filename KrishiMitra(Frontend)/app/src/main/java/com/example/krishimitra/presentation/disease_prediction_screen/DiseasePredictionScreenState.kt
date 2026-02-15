package com.example.krishimitra.presentation.disease_prediction_screen

import com.example.krishimitra.domain.model.disease_prediction_data.DiseasePredictionResponse

data class DiseasePredictionScreenState(
    val isLoading: Boolean = false,
    val response: DiseasePredictionResponse? = null,
    val currentLang: String = "hi"
)