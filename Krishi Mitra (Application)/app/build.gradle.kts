import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")

    kotlin("plugin.serialization") version "2.1.0"
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.krishimitra"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.krishimitra"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String","MANDI_API_KEY",properties.getProperty("MANDI_API_KEY"))
        buildConfigField("String","WEATHER_API_KEY",properties.getProperty("WEATHER_API_KEY"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig =true
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.paging.compose)
    implementation(libs.firebase.storage)
    implementation(libs.google.generativeai)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.compose.ui.text)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Icons
    implementation(libs.androidx.compose.material.icons.extended)


    //Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose) // Check for the latest version


    //Navigation
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3) // Or latest version
    //Permissions for jetpack compose
    implementation(libs.accompanist.permissions)



    //Room database
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)  // For annotation processing
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    //Datastore
    implementation(libs.androidx.datastore.preferences) // <-- Add this line

    //Location services
    implementation(libs.play.services.location)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)



    //Lottie Animation
    implementation(libs.lottie.compose)

    //Gemini sdk


    implementation(libs.google.firebase.ai)
    implementation(platform(libs.firebase.bom))

}