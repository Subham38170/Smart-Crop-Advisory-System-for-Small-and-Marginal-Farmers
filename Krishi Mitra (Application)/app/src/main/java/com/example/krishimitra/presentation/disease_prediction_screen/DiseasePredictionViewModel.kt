package com.example.krishimitra.presentation.disease_prediction_screen

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.R
import com.example.krishimitra.data.repo.TextToSpeechManager
import com.example.krishimitra.domain.ResultState
import com.example.krishimitra.domain.repo.Repo
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

@HiltViewModel
class DiseasePredictionViewModel @Inject constructor(
    private val repo: Repo,
    @ApplicationContext private val context: Context,
    private val ttsManager: TextToSpeechManager
) : ViewModel() {


    private val _state = MutableStateFlow(DiseasePredictionScreenState())
    val state = _state.asStateFlow()
    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()


    init {
        getCurrentLanguage()

        collectEventFromTTS()
    }


    fun onEvent(event: DiseasePredictionScreenEvent) {
        when (event) {
            is DiseasePredictionScreenEvent.PredictCropDisease -> {
                predictCropDisease(event.imageUrl)
            }

            is DiseasePredictionScreenEvent.onSpeak -> {
                val textToSpeak = buildString {
                    append("${context.getString(R.string.the_predicted_disease_is)} ${event.predictedData.Disease}. ")
                    append("${context.getString(R.string.here_is_description)}: ${event.predictedData.Description}. ")
                    append(
                        "${context.getString(R.string.recommended_precautions_are)}: ${
                            event.predictedData.Precautions.joinToString(
                                ", "
                            )
                        }. "
                    )
                    append(
                        "${context.getString(R.string.recommended_treatment_is)}: ${
                            event.predictedData.Treatment.joinToString(
                                ", "
                            )
                        }."
                    )
                }
                ttsManager.speak(textToSpeak)
            }

            is DiseasePredictionScreenEvent.onStopSpeak -> {
                ttsManager.stop()
            }
        }
    }


    private fun collectEventFromTTS() {
        viewModelScope.launch(Dispatchers.IO) {
            ttsManager.events.collectLatest { evnt ->
                _event.emit(evnt)
            }
        }
    }

    private fun predictCropDisease(filePath: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.predictCropDisease(state.value.currentLang, filePath).collect { collect ->
                when (collect) {
                    is ResultState.Error -> {
                        _event.emit(collect.exception)
                        _state.update { it.copy(isLoading = false) }
                    }

                    ResultState.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }

                    is ResultState.Success -> {
                        _state.update { it.copy(isLoading = false, response = collect.data) }
                    }
                }
            }
        }
    }


    private fun getCurrentLanguage() {
        viewModelScope.launch {
            repo.getLanguage().collectLatest { lang ->
                _state.update {
                    it.copy(
                        currentLang = lang
                    )
                }
                ttsManager.init(state.value.currentLang)

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }

}