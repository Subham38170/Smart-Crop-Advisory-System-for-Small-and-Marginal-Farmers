package com.example.krishimitra.presentation.assistant_screen

import com.example.krishimitra.domain.repo.NetworkStatus


data class AssistantScreenState(
    val error: String? = null,
    val messageList: List<ChatBotMessage> = emptyList(),
    val isGeneratingResponse: Boolean = false,
    val networkStatus: NetworkStatus = NetworkStatus.Disconnected,
    val micLevel: Float = 0f,
    val isListening: Boolean = false,
    val currentLanguage: String = "hi"
)