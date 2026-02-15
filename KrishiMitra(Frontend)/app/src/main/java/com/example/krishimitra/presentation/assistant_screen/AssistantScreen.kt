package com.example.krishimitra.presentation.assistant_screen

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.krishimitra.Constants
import com.example.krishimitra.R
import com.example.krishimitra.domain.repo.NetworkStatus
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    onEvent: (AssistantScreenEvent) -> Unit,
    state: AssistantScreenState,
    moveBackToHomeScreen: () -> Unit,
    event: SharedFlow<String>
) {
    val context = LocalContext.current


    var recognizedText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var micLevel by remember { mutableStateOf(0f) }


    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }
    val listener = object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                recognizedText = matches[0]
                onEvent(AssistantScreenEvent.sendQuery(recognizedText))
            }
            isListening = false
            micLevel = 0f
        }

        override fun onReadyForSpeech(params: Bundle?) {
            isListening = true
        }

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {
            micLevel = (rmsdB / 10f).coerceIn(0f, 1f)
        }

        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {
            isListening = false
            micLevel = 0f
        }

        override fun onError(errorCode: Int) {
            Toast.makeText(context, "Something went wrong..", Toast.LENGTH_LONG).show()
            micLevel = 0f
            isListening = false
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }


    DisposableEffect(Unit) {
        speechRecognizer.setRecognitionListener(listener)
        onDispose { speechRecognizer.destroy() }
    }

    val lazyListState = rememberLazyListState()

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
        }
    )
    val micPermissionGranted = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.RECORD_AUDIO
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED








    Scaffold(
        topBar = {
            AssistantScreenTopAppBar(
                moveBackToHomeScreen = moveBackToHomeScreen,
                networkStatus = state.networkStatus
            )
        },
        containerColor = colorResource(id = R.color.light_green)

    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 80.dp),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {


                item {
                    if (state.isGeneratingResponse) {
                        ChatMessage(
                            message = ChatBotMessage(user = false),
                            isGenerating = true
                        )
                    }
                }
                items(state.messageList.reversed(), key = { it.hashCode() }) {
                    ChatMessage(
                        message = it
                    )
                }


            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.BottomCenter
            ) {


                AssistantSCreenFAB(
                    onClick = {
                        if (state.networkStatus is NetworkStatus.Disconnected) {
                            Toast.makeText(context, "Connect to internet", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            if (!micPermissionGranted) {
                                micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)

                            } else {

                                onEvent(AssistantScreenEvent.stopSpeaking)

                                val intent =
                                    Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(
                                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                        )
                                        putExtra(
                                            RecognizerIntent.EXTRA_LANGUAGE,
                                            state.currentLanguage
                                        )
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
                                    }
                                speechRecognizer.startListening(intent)

                            }
                        }
                    },
                    enabled = !state.isGeneratingResponse,
                    isListening = isListening,
                    micLevel = micLevel
                )


            }

        }
    }
}


@Composable
fun ChatMessage(
    message: ChatBotMessage,
    isGenerating: Boolean = false,
    onSpeak: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.user) 20.dp else 0.dp,
                end = if (message.user) 0.dp else 20.dp
            )
    ) {
        IconButton(
            onClick = onSpeak
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_volume_up_24),
                contentDescription = "Speak"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    shape = RoundedCornerShape(
                        topStart = Constants.TEXT_FIELD_ROUNDED_CORNER_SIZE,
                        topEnd = Constants.TEXT_FIELD_ROUNDED_CORNER_SIZE,
                        bottomEnd = if (message.user) 0.dp else Constants.TEXT_FIELD_ROUNDED_CORNER_SIZE,
                        bottomStart = if (message.user) Constants.TEXT_FIELD_ROUNDED_CORNER_SIZE else 0.dp
                    )
                )
                .background(
                    if (message.user) colorResource(id = R.color.olive_green) else colorResource(
                        id = R.color.green_dark
                    )
                )
                .padding(8.dp)

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Person"
                )
                Text(
                    text = if (message.user) stringResource(id = R.string.you) else stringResource(
                        id = R.string.krishi_dost
                    ),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(thickness = 2.dp)
            Row {
                if (!isGenerating) {
                    SelectionContainer {
                        Text(
                            text = message.message, color = Color.White
                        )
                    }
                } else {
                    Text(
                        text = "Wait...",
                        color = Color.White
                    )
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreenTopAppBar(
    moveBackToHomeScreen: () -> Unit,
    networkStatus: NetworkStatus
) {

    TopAppBar(
        title = {
            Text(stringResource(id = R.string.krishi_dost))
        }, navigationIcon = {
            IconButton(onClick = moveBackToHomeScreen) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Move back to home screen"
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id = R.color.slight_dark_green),
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White
        ), actions = {
            Icon(
                painter = if (networkStatus is NetworkStatus.Connected) painterResource(
                    id = R.drawable.outline_android_wifi_3_bar_24
                ) else painterResource(id = R.drawable.outline_android_wifi_3_bar_off_24),
                contentDescription = "Network status",
                tint = Color.White,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    )
}


@Composable
fun AssistantSCreenFAB(
    onClick: () -> Unit,
    enabled: Boolean,
    isListening: Boolean,
    micLevel: Float

) {


    val micScale by animateFloatAsState(
        targetValue = if (isListening) 1f + micLevel * 0.5f else 1f,
        animationSpec = tween(durationMillis = 100)
    )
    Box(
        modifier = Modifier
            .size(70.dp)
            .scale(micScale)
            .background(
                color = if (isListening) Color.Red.copy(alpha = 0.3f + 0.7f * micLevel)
                else colorResource(id = R.color.slight_dark_green),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        FilledIconButton(

            onClick = onClick,
            modifier = Modifier
                .size(60.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorResource(id = R.color.slight_dark_green),
                contentColor = Color.White
            ),
            enabled = enabled
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_mic_24),
                contentDescription = "Mic"
            )
        }
    }


}