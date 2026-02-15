package com.example.krishimitra.presentation.assistant_screen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.data.repo.TextToSpeechManager
import com.example.krishimitra.domain.repo.Repo
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ChatBotMessage(
    val user: Boolean = true,
    val message: String = ""

)

@HiltViewModel
class AssistantScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: Repo,
    private val ttsManager: TextToSpeechManager,
) : ViewModel() {
    private val _state = MutableStateFlow(AssistantScreenState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()

    init {
        checkNetworkStatus()
        viewModelScope.launch(Dispatchers.IO) {
            repo.getLanguage().collect { langCode ->
                _state.update { it.copy(currentLanguage = getLocaleFromLangCode(langCode)) }
                ttsManager.init(langCode)
            }
        }

    }


    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")

    fun onEvent(
        event: AssistantScreenEvent
    ) {
        when (event) {
            is AssistantScreenEvent.onMicClick -> {}

            is AssistantScreenEvent.sendQuery -> {
                _state.update {
                    it.copy(
                        messageList = it.messageList + ChatBotMessage(true, event.query),
                        isGeneratingResponse = true
                    )
                }
                generateResponse(query = event.query)

            }

            is AssistantScreenEvent.stopSpeaking -> {
                ttsManager.stop()
            }
        }

    }


    private fun generateResponse(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = model.generateContent(query)
                response.text?.let {
                    val response = it.replace("*", "").replace("#", "")
                    _state.update {
                        it.copy(
                            messageList = it.messageList + ChatBotMessage(
                                user = false,
                                message = response
                            ),
                            isGeneratingResponse = false

                        )
                    }
                    startSpeaking(response)
                }
            }catch (e: Exception){
                _event.emit("${e.message}")
            }
        }
    }

    private fun checkNetworkStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.networkStatus().collectLatest { status ->
                _state.update {
                    it.copy(
                        networkStatus = status
                    )
                }
            }
        }

    }


    private fun startSpeaking(text: String) {
        viewModelScope.launch(Dispatchers.Main) {
            if (TextToSpeechManager.isttsSpeaking.value) ttsManager.stop()
            ttsManager.speak(text)
        }
    }


    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }


    private fun getLocaleFromLangCode(code: String): String {
        return when (code) {
            "eng" -> "en-IN"
            "bn" -> "bn-IN"
            "gu" -> "gu-IN"
            "hi" -> "hi-IN"
            "kn" -> "kn-IN"
            "ml" -> "ml-IN"
            "mr" -> "mr-IN"
            "or" -> "or-IN"
            "pa" -> "pa-IN"
            "ta" -> "ta-IN"
            "te" -> "te-IN"
            "ur" -> "ur-IN"
            else -> "en-IN"
        }
    }

}

