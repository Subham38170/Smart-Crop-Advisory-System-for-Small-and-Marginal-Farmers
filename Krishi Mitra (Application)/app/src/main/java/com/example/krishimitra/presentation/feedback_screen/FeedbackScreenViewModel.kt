package com.example.krishimitra.presentation.feedback_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.domain.ResultState
import com.example.krishimitra.domain.model.feedback.FeedbackData
import com.example.krishimitra.domain.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackScreenViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {


    private val _state = MutableStateFlow(FeedbackScreenState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()

    fun onEvent(event: FeedbackScreenEvent) {
        when (event) {
            is FeedbackScreenEvent.sendFeedback -> {
                sendFeedback(event.feedbackData)
            }
        }
    }


    private fun getCurrLanguage() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getLanguage().collectLatest { langCode ->
                _state.update {
                    it.copy(
                        currLang = getLocaleFromLangCode(langCode)
                    )
                }
            }
        }
    }

    private fun sendFeedback(
        feedbackData: FeedbackData
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.sendFeedback(feedbackData)
                .collectLatest { result ->
                    when (result) {
                        is ResultState.Error -> {
                            _event.emit(result.exception)
                            _state.update { it.copy(isSending = false) }
                        }

                        is ResultState.Loading -> {
                            _state.update { it.copy(isSending = true) }

                        }

                        is ResultState.Success -> {
                            _event.emit("Thank your for giving feedback.")
                            _state.update { it.copy(isSending = false) }

                        }
                    }
                }
        }
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