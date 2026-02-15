package com.example.krishimitra.presentation.auth_screen

import com.example.krishimitra.domain.model.location.Location

data class AuthState(
    val isSuccess: Boolean = false,
    val isSignLoading: Boolean = false,
    val currentLanguage: String = "English",
    val location: Location? = null,
    val isLocationLoading: Boolean = false
)
