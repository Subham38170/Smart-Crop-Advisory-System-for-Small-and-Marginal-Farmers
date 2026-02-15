package com.example.krishimitra.presentation.disease_prediction_screen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.krishimitra.R
import com.example.krishimitra.domain.model.disease_prediction_data.Prediction
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseasePredictionScreen(
    imageUri: Uri?,
    moveBackToScreen: () -> Unit,
    state: DiseasePredictionScreenState,
    onEvent: (DiseasePredictionScreenEvent) -> Unit,

    event: SharedFlow<String>
) {
    val context = LocalContext.current

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.red_scanner)
    )
    LaunchedEffect(Unit) {
        event.collectLatest {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {

                },
                navigationIcon = {
                    IconButton(
                        onClick = moveBackToScreen
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Move back to home screen"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box {
                imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Taken Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentScale = ContentScale.FillBounds
                    )
                }

                if (state.isLoading) {
                    LottieAnimation(
                        composition = composition,
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(280.dp),
                        iterations = LottieConstants.IterateForever
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .height(12.dp)
            )
            Button(
                shape = RoundedCornerShape(4.dp),
                onClick = {
                    if (!state.isLoading) {

                        imageUri?.let {
                            onEvent(DiseasePredictionScreenEvent.PredictCropDisease(it))
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.grass_green),
                    contentColor = Color.White
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = stringResource(id = R.string.predict)
                    )
                }
            }


            state.response?.let {
                LazyColumn {
                    items(state.response.predictions) {
                        PredictionData(
                            predictedData = it,
                            onSpeak = {
                                onEvent(DiseasePredictionScreenEvent.onSpeak(it))
                            }
                        )
                    }
                }
            }

        }
    }
}


@Composable
fun PredictionData(
    predictedData: Prediction,
    onSpeak: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            IconButton(
                onClick = onSpeak,
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_volume_up_24),
                    contentDescription = "Speak"
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.disease) + ": ${predictedData.Disease}",
                    color = Color.Red,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.confidence) + ": ${(predictedData.confidence)}%",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.description) + ":",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = predictedData.Description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.treatement) + ":",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                predictedData.Treatment.forEach { treatment ->
                    Text("- $treatment", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.precautions) + ":",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                predictedData.Precautions.forEach { precaution ->
                    Text("- $precaution", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}


