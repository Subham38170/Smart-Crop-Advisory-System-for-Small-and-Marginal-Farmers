package com.example.krishimitra.presentation.mandi_screen

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import com.example.krishimitra.R
import com.example.krishimitra.data.local.entity.MandiPriceEntity
import com.example.krishimitra.data.mappers.toDto
import com.example.krishimitra.domain.repo.NetworkStatus
import com.example.krishimitra.presentation.buy_sell_screen.CustomizedSearchBar
import com.example.krishimitra.presentation.components.CustomizedInputChip
import com.example.krishimitra.presentation.components.MandiPriceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MandiScreen(
    state: MandiScreenState,
    mandiPrice: LazyPagingItems<MandiPriceEntity>,
    onEvent: (UiAction) -> Unit,
    scrollBehavior: BottomAppBarScrollBehavior,
) {

    val context = LocalContext.current


    var recognizedText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }


    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }
    val listener = object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                recognizedText = matches[0]
                onEvent(UiAction.onSearch(recognizedText))
            }
            isListening = false
        }

        override fun onReadyForSpeech(params: Bundle?) {
            isListening = true
        }

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {
        }

        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {
            isListening = false
        }

        override fun onError(errorCode: Int) {
            Toast.makeText(context, "Something went wrong..", Toast.LENGTH_LONG).show()
            isListening = false
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }


    DisposableEffect(Unit) {
        speechRecognizer.setRecognitionListener(listener)
        onDispose { speechRecognizer.destroy() }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
        }
    )
    val micPermissionGranted = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.RECORD_AUDIO
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    val imageLoader = ImageLoader.Builder(context).diskCache {
        DiskCache.Builder().directory(context.cacheDir.resolve("offline_images"))
            .maxSizeBytes(100L * 1024L * 1024).build()
    }.build()

    val windowInfo = LocalWindowInfo.current
    val containerWidth = windowInfo.containerSize.width

    LaunchedEffect(true) {
        Log.d("MANDI_PRICE", mandiPrice.itemCount.toString())
    }

    Scaffold {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.light_green))
                .padding(it)
                .padding(4.dp)
        ) {

            CustomizedSearchBar(

                searchValue = recognizedText,
                onSearch = {
                    onEvent(UiAction.onSearch(it))
                }, onEmptySearch = {
                    onEvent(UiAction.loadAllCrops)
                }, onMicClick = {
                    if (state.networkStatus is NetworkStatus.Disconnected) {
                        Toast.makeText(context, "Connect to internet", Toast.LENGTH_SHORT).show()
                    } else {
                        if (!micPermissionGranted) {
                            micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)

                        } else {
                            val intent =
                                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(
                                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                    )
                                    putExtra(
                                        RecognizerIntent.EXTRA_LANGUAGE,
                                        "en-IN"
                                    )
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
                                }
                            speechRecognizer.startListening(intent)

                        }
                    }

                }, placeHolder = stringResource(id = R.string.search_crops)

            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(state.listOfStates) {
                    CustomizedInputChip(isSelected = it == state.state, onSelect = {
                        onEvent(UiAction.onStateSelect(it))
                    }, label = it, onDeselect = {
                        onEvent(UiAction.onStateDeselect)
                    })
                }

            }

            if (state.state.isNotEmpty()) {

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(state.listOfDistricts) {
                        CustomizedInputChip(isSelected = it == state.district, onSelect = {
                            onEvent(UiAction.onDistrictSelect(it))
                        }, label = it, onDeselect = {
                            onEvent(UiAction.onDistrictDeselect)
                        })
                    }

                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                item {
                    if (mandiPrice.loadState.refresh is LoadState.Loading) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                }
                items(mandiPrice.itemCount) { index ->
                    mandiPrice[index]?.let {
                        MandiPriceItem(
                            mandiPrice = it.toDto(),
                            imageSize = if (containerWidth > 600) 100.dp else 80.dp,
                            context = context,
                            imageLoader = imageLoader
                        )
                    }
                }
                item {
                    if (mandiPrice.loadState.append is LoadState.Loading) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }


        }
    }

}