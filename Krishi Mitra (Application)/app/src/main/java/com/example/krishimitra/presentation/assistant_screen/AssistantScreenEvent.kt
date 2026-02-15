package com.example.krishimitra.presentation.assistant_screen

sealed class AssistantScreenEvent {
    object onMicClick: AssistantScreenEvent()


    data class sendQuery(val query: String): AssistantScreenEvent()


    object stopSpeaking: AssistantScreenEvent()


}