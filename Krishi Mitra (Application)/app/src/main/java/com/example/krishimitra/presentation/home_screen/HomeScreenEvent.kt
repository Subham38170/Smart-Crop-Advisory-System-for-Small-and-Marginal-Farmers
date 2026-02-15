package com.example.krishimitra.presentation.home_screen

sealed class HomeScreenEvent {

    data class ChangeLanguage(val lang: String): HomeScreenEvent()


    data class Error(val message: String ): HomeScreenEvent()
}