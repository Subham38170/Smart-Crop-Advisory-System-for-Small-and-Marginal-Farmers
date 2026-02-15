package com.example.krishimitra

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.preferencesDataStore


data class Language(
    val code: String,
    val englishName: String,
    val nativeName: String
)


object Constants {
    val TEXT_FIELD_DEFAULT_SIZE = 18.sp
    val TEXT_FIELD_ROUNDED_CORNER_SIZE = 20.dp

    val SUPPORTED_LANGUAGES = listOf(
        Language("eng", "English", "English"),
        Language("as", "Assamese", "অসমীয়া"),
        Language("bh", "Bihari", "भोजपुरी/मैथिली/मगही"),
        Language("bn", "Bengali", "বাংলা"),
        Language("gu", "Gujarati", "ગુજરાતી"),
        Language("hi", "Hindi", "हिन्दी"),
        Language("him", "Himalayan", "हिमालयन"),
        Language("kn", "Kannada", "ಕನ್ನಡ"),
        Language("ks", "Kashmiri", "कश्मीरी"),
        Language("ml", "Malayalam", "മലയാളം"),
        Language("mni", "Manipuri", "মণিপুরী"),
        Language("mr", "Marathi", "मराठी"),
        Language("or", "Odia", "ଓଡ଼ିଆ"),
        Language("pa", "Punjabi", "ਪੰਜਾਬੀ"),
        Language("raj", "Rajasthani", "राजस्थानी"),
        Language("sa", "Sanskrit", "संस्कृतम्"),
        Language("sat", "Santali", "ᱥᱟᱱᱛᱟᱲᱤ"),
        Language("sd", "Sindhi", "سنڌي"),
        Language("ta", "Tamil", "தமிழ்"),
        Language("te", "Telugu", "తెలుగు"),
        Language("ur", "Urdu", "اردو")
    )


    val Context.dataStore by preferencesDataStore(name = "settings")
    val Context.userDataStore by preferencesDataStore(name = "user_data")

    const val MANDI_API_KEY = BuildConfig.MANDI_API_KEY

    const val WEATHER_API_KEY = BuildConfig.WEATHER_API_KEY

}

object FirebaseConstants {
    const val USERS = "users"
    const val STATES = "states"
    const val GOVT_SCHEMES = "govt_schemes"
    const val KRISHI_NEWS = "krishi_news"
    const val CROP_BAZAR_IMAGES = "Buy_Sell_Crops"
    const val CROP_BAZAR = "krishi_bazar"
    const val MANDI_CROP_IMAGES = "mandi_price_crop_images"
    const val USER_FEEDBACK = "user_feedback"
}


object NotificationConstants{
    const val GLOBAL_CHANNEL_ID = "Global"
}