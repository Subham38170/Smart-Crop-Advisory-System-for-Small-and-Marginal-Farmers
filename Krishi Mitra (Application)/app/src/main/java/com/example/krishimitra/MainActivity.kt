package com.example.krishimitra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.krishimitra.data.repo.LanguageManager
import com.example.krishimitra.presentation.nav_graph.NavGraph
import com.example.krishimitra.presentation.ui.theme.KrishiMitraTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var languageManager: LanguageManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            languageManager.getLanguage().collect {
                languageManager.updateLanguage(it)
            }
        }

        enableEdgeToEdge()
        setContent {
            KrishiMitraTheme {
                //checkPermissions()

                val context = LocalContext.current
                // Initialize TTS and SpeechRecognizer in a side effect to ensure it's done once.
//                DisposableEffect(Unit) {
//                    tts = TextToSpeech(context) { status ->
//                        if (status == TextToSpeech.SUCCESS) {
//                            tts.language = Locale("hi", "IN")
//                        } else {
//                            Toast.makeText(context, "TTS initialization failed!", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
//                    assistantViewModel.setup(tts, speechRecognizer)
//                    onDispose {
//                        // ViewModel's onCleared will handle cleanup, but we can be explicit here.
//                        if (this@MainActivity::tts.isInitialized) tts.shutdown()
//                        if (this@MainActivity::speechRecognizer.isInitialized) speechRecognizer.destroy()
//                    }
//                }
                //     AssistantScreen(viewModel = assistantViewModel)
                val navController = rememberNavController()
                NavGraph(
                    activity = this
                )
            }
        }
    }
//    private fun checkPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
//        }
//    }
}

