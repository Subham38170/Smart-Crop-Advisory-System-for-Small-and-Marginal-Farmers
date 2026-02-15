package com.example.krishimitra.data.repo

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.krishimitra.Constants.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LanguageManager @Inject constructor(
    private val context: Context
) {

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("app_language")

    }

    // Update / save language
    suspend fun updateLanguage(lang: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = lang
        }
        changeLanguage(lang)

    }

    // Get language as Flow (live updates)
    fun getLanguage(): Flow<String> {
        return context.dataStore.data
            .map { preferences ->
                preferences[LANGUAGE_KEY] ?: "eng" // default English
            }
    }


    private fun changeLanguage(
        languageCode: String
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
    }


}