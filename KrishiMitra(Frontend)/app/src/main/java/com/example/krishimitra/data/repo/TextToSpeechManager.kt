package com.example.krishimitra.data.repo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


class TextToSpeechManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isSpeaking = false
    companion object{
        var isttsSpeaking = mutableStateOf(false)

    }



    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()


    fun init(langCode: String = "hi") {
        release() // clear any previous instance

        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val locale = getLocaleFromLangCode(langCode)
                val result = tts?.setLanguage(locale)

                when (result) {
                    TextToSpeech.LANG_MISSING_DATA -> {
                        promptIntallTtsData(langCode)
                        isInitialized = false
                        emitEvent("Language data missing for '$langCode'. Please install.")
                    }

                    TextToSpeech.LANG_NOT_SUPPORTED -> {
                        isInitialized = false
                        emitEvent("Language '$langCode' not supported by TTS engine.")
                    }

                    else -> {
                        isInitialized = true
                        emitEvent("TTS ready for language: $langCode")
                    }
                }
            } else {
                isInitialized = false
                emitEvent("TTS initialization failed with status: $status")
            }
        }
    }

    fun speak(text: String) {
        if (!isInitialized) {
            emitEvent("TTS engine not initialized")
            return
        }
        if (text.isBlank()) {
            emitEvent("Text is empty.")
            return
        }

        try {

            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1")
            isSpeaking = true
            isttsSpeaking.value = true

        } catch (e: Exception) {
            emitEvent("Error speaking text: ${e.message}")
        }

    }

    fun stop() {
        if (isSpeaking) {
            tts?.stop()
            isSpeaking = false
            isttsSpeaking.value = false


        }
    }


    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            emitEvent("Error releasing TTS: ${e.message}")
        } finally {

            tts = null
            isInitialized = false
            isSpeaking = false
            isttsSpeaking.value = false
        }
    }


    //Prompt uer to install TTS lnaguage data
    private fun promptIntallTtsData(langCode: String) {
        try {
            val installIntent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(installIntent)
        } catch (e: Exception) {
            emitEvent("Cannot install TTS language for for $langCode: ${e.message}")
        }
    }

    private fun emitEvent(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _events.emit(message)
        }
    }

    private fun getLocaleFromLangCode(code: String): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (code) {
                "eng" -> Locale.forLanguageTag("en-IN")
                "bn" -> Locale.forLanguageTag("bn-IN")
                "gu" -> Locale.forLanguageTag("gu-IN")
                "hi" -> Locale.forLanguageTag("hi-IN")
                "kn" -> Locale.forLanguageTag("kn-IN")
                "ml" -> Locale.forLanguageTag("ml-IN")
                "mr" -> Locale.forLanguageTag("mr-IN")
                "or" -> Locale.forLanguageTag("or-IN")
                "pa" -> Locale.forLanguageTag("pa-IN")
                "ta" -> Locale.forLanguageTag("ta-IN")
                "te" -> Locale.forLanguageTag("te-IN")
                "ur" -> Locale.forLanguageTag("ur-IN")
                else -> Locale.forLanguageTag("en-IN")
            }
        } else {
            return when (code) {
                "eng" -> Locale("en", "IN")
                "bn" -> Locale("bn", "IN")
                "gu" -> Locale("gu", "IN")
                "hi" -> Locale("hi", "IN")
                "kn" -> Locale("kn", "IN")
                "ml" -> Locale("ml", "IN")
                "mr" -> Locale("mr", "IN")
                "or" -> Locale("or", "IN")
                "pa" -> Locale("pa", "IN")
                "ta" -> Locale("ta", "IN")
                "te" -> Locale("te", "IN")
                "ur" -> Locale("ur", "IN")
                else -> Locale("en", "IN")
            }
        }
    }
}