package com.example.krishimitra.presentation.nav_graph

import kotlinx.serialization.Serializable

sealed class Routes {

    @Serializable
    data object AuthScreen : Routes()

    @Serializable
    data object HomeScreen : Routes()

    @Serializable
    data object ProfileScreen : Routes()

    @Serializable
    data object BuySellScreen : Routes()

    @Serializable
    data object MandiScreen : Routes()

    @Serializable
    data class DiseasePredictionScreen(val imageUri: String?) : Routes()


    @Serializable
    data object CommunityMainScreen : Routes()

    @Serializable
    data class StateCommunityScreen(val state: String) : Routes()


    @Serializable
    data class AssistantScreen(
        val query: String = ""
    ) : Routes()


    @Serializable
    object NotificationScreen : Routes()


    @Serializable
    data object FeedbackScreen : Routes()

    @Serializable
    data object FertilizerRecommendationScreen: Routes()
}