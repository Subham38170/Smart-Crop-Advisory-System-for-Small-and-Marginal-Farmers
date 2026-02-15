package com.example.krishimitra.presentation.home_screen

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.krishimitra.R
import com.example.krishimitra.domain.model.farmer_data.UserDataModel
import com.example.krishimitra.domain.model.weather_data.DailyWeather
import com.example.krishimitra.presentation.components.shimmerEffect
import com.example.krishimitra.presentation.components.top_app_bars.HomeScreenTopBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File


val crops = listOf(
    RecommendedCropFarmer(
        imageRes = R.drawable.maize,
        name = "Maize",
        soilLabel = "Loamy",
        temperatureLabel = "20-30°C",
        rainfallLabel = "Medium",
        seasonLabel = "Kharif",
        fertilizerLabel = "Medium"
    ),
    RecommendedCropFarmer(
        imageRes = R.drawable.wheat,
        name = "Wheat",
        soilLabel = "Clay",
        temperatureLabel = "15-25°C",
        rainfallLabel = "Low",
        seasonLabel = "Rabi",
        fertilizerLabel = "Medium"
    ),
    RecommendedCropFarmer(
        imageRes = R.drawable.rice,
        name = "Rice",
        soilLabel = "Alluvial",
        temperatureLabel = "25-35°C",
        rainfallLabel = "High",
        seasonLabel = "Kharif",
        fertilizerLabel = "Heavy"
    ),
    RecommendedCropFarmer(
        imageRes = R.drawable.vegetables,
        name = "Vegetables",
        soilLabel = "Loamy",
        temperatureLabel = "18-28°C",
        rainfallLabel = "Medium",
        seasonLabel = "All-season",
        fertilizerLabel = "Medium"
    )
)


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onEvent: (HomeScreenEvent) -> Unit,
    state: HomeScreenState,
    moveToMandiScreen: () -> Unit,
    moveToDiseasePredictionScreen: (Uri?) -> Unit,
    moveToKrishiBazar: () -> Unit,
    moveToNotificationScreen: () -> Unit,
    moveToFertilizerRecommendationScreen: () -> Unit,
    scrollBehavior: BottomAppBarScrollBehavior,
) {


    val context = LocalContext.current

//    val fcmTokenKey = stringPreferencesKey("gcm_token")
//    val fcmToken = flow<String> {
//        context.dataStore.data.map {
//            it[fcmTokenKey]
//        }.collect(collector = {
//            if (it != null) {
//                this.emit(it)
//            }
//        })
//    }.collectAsState(initial = "")
//    LaunchedEffect(fcmToken.value) {
//        Log.d("FCMTOKEN", fcmToken.value)
//    }
//
//    val notificationTitle = remember {
//        mutableStateOf(
//            if (intent.hasExtra("title")) intent.getStringExtra("title")
//            else ""
//        )
//    }
//    val notificationBody = remember {
//        mutableStateOf(
//            if (intent.hasExtra("title")) intent.getStringExtra("body")
//            else ""
//        )
//    }

    var showDiseasePredictionAlertDialog by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val photoFile = remember {
        File(context.cacheDir, "captured_image.jpg").apply {
            createNewFile()
        }
    }

    val uri = FileProvider.getUriForFile(
        context, "${context.packageName}.provider", photoFile
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(), onResult = { sucess ->
            if (sucess) imageUri = uri

        })


    LaunchedEffect(imageUri) {
        imageUri?.let {
            showDiseasePredictionAlertDialog = false
            moveToDiseasePredictionScreen(it)
        }
    }

    val galleryPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE
    val imagePermissionState = rememberPermissionState(galleryPermission)
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), onResult = {
            imageUri = it
        })

    if (showDiseasePredictionAlertDialog) {

        AlertDialog(containerColor = colorResource(id = R.color.light_green), onDismissRequest = {
            showDiseasePredictionAlertDialog = !showDiseasePredictionAlertDialog
        }, confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = {
                        if (imagePermissionState.status.isGranted) {
                            galleryLauncher.launch("image/*")
                        } else {
                            imagePermissionState.launchPermissionRequest()
                        }
                    }) {
                    Text(
                        text = stringResource(id = R.string.upload_image)
                    )
                }
                TextButton(
                    onClick = {
                        if (cameraPermissionState.status.isGranted) {
                            launcher.launch(uri)
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }) {
                    Text(
                        text = stringResource(id = R.string.click_photo)
                    )
                }
            }
        })
    }


    Scaffold(
        topBar = {
            HomeScreenTopBar(
                currentLanguage = state.currentLanguage, onLanguageChange = {
                    onEvent(HomeScreenEvent.ChangeLanguage(it))
                },
                onNotificationClick = moveToNotificationScreen,
                notificatonStatus = state.notificationStatus
            )
        },

        ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            //  item {
//            Text(
//                text = "FCM Token ${fcmToken}"
//            )
//        }
            //         item {
//                Text(
//                    text = "${notificationBody.value} ${notificationTitle.value}"
//                )
//            }
            item {
                HorizontalDivider(thickness = 1.dp, color = Color.White)
            }
            item {
                if (state.weatherData.isNotEmpty()) {
                    WeatherCard(
                        modifier = Modifier.fillMaxWidth(),
                        userData = state.userData,
                        weatherApiResponseItem = state.weatherData
                    )
                } else {
                    WeatherCardShimmerEffect()
                }
            }
            item {
                if (state.schemeBannersList.isNotEmpty()) {
                    AutoImageSchemeSlider(

                        banners = state.schemeBannersList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
            item {

                CropRecommendationSliderFarmer(
                    crops = crops,
                    title = stringResource(id = R.string.crop_recommendation)
                )

            }
            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CustomizedHomeButton(
                        onClick = {
                            showDiseasePredictionAlertDialog = true
                        },
                        painter = painterResource(id = R.drawable.disease_prediction),
                        modifier = Modifier
                            .weight(1f)
                            .shadow(2.dp, RoundedCornerShape(12.dp)),
                        text =  stringResource(id = R.string.disease_prediction)
                    )
                    CustomizedHomeButton(
                        onClick = moveToFertilizerRecommendationScreen,
                        painter = painterResource(id = R.drawable.soil_health),
                        modifier = Modifier
                            .weight(1f)
                            .shadow(2.dp, RoundedCornerShape(12.dp)),
                        text = stringResource(id = R.string.fertilizer_recommendation)
                    )

                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CustomizedHomeButton(
                        onClick = moveToMandiScreen,
                        painter = painterResource(id = R.drawable.mandi_price),
                        modifier = Modifier
                            .weight(1f)
                            .shadow(2.dp, RoundedCornerShape(12.dp)),
                        text = stringResource(id = R.string.mandi_price)
                    )
                    CustomizedHomeButton(
                        onClick = moveToKrishiBazar,
                        painter = painterResource(id = R.drawable.buy_sell_crop),
                        modifier = Modifier
                            .weight(1f)
                            .shadow(2.dp, RoundedCornerShape(12.dp)),
                        text = stringResource(id = R.string.krishi_bazar)
                    )

                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.agri_news),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

            }
            item {
                if (state.krishiNewsBannerList.isNotEmpty()) {
                    AutoImageNewsSlider(
                        banners = state.krishiNewsBannerList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


@Composable
fun CustomizedHomeButton(
    onClick: () -> Unit, painter: Painter, modifier: Modifier = Modifier, text: String
) {

    Column(
        modifier = modifier
            .background(
                brush = Brush.sweepGradient(
                    colors = listOf(Color(0xFF94FD72), Color(0xFFC4D9C8), Color(0xFFD4DBD5))
                ), shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .padding(2.dp)
            .clickable(
                enabled = true,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )

    ) {
        Card {
            Image(
                painter = painter,
                contentDescription = "Plant Disease",
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),

                contentScale = ContentScale.FillBounds
            )
        }
        Text(

            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun WeatherCardShimmerEffect(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(220.dp)
            .background(
                colorResource(id = R.color.slight_dark_green),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(8.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(5) {
                Box(
                    modifier = Modifier
                        .size(220.dp, 160.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .shimmerEffect()
                )
            }
        }


    }
}


@Composable
fun WeatherCard(
    modifier: Modifier,
    userData: UserDataModel,
    weatherApiResponseItem: List<DailyWeather>
) {

    Column(
        modifier = modifier
            .height(220.dp)
            .background(
                colorResource(id = R.color.slight_dark_green),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(8.dp)

    ) {
        Row(
            modifier = Modifier.height(40.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = Color.White
            )
            Text(
                text = buildAnnotatedString {
                    append(userData.village)
                    append(", ")
                    append(userData.district)
                    append(", ")
                    append(userData.state)
                }, fontWeight = FontWeight.Bold, color = Color.White
            )
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(weatherApiResponseItem) { data ->
                WeatherIcon(
                    dailyWeather = data
                )
            }
        }
    }


}

fun getWeatherIcon(condition: String): Int {
    return when (condition.lowercase()) {
        "clear", "clear sky", "sunny" -> R.mipmap.sunny
        "partly cloudy", "few clouds", "scattered clouds", "broken clouds", "overcast clouds" -> R.mipmap.cloudy_sunny
        "patchy rain nearby", "light rain shower", "light rain", "moderate rain", "rain", "heavy intensity rain", "very heavy rain", "extreme rain", "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain" -> R.mipmap.rainy
        "patchy light drizzle", "drizzle", "light intensity drizzle", "heavy intensity drizzle", "light intensity drizzle rain", "drizzle rain", "heavy intensity drizzle rain", "shower rain and drizzle", "heavy shower rain and drizzle", "shower drizzle" -> R.mipmap.rainy
        "thundery outbreaks in nearby", "thunderstorm", "thunderstorm with light rain", "thunderstorm with rain", "thunderstorm with heavy rain", "light thunderstorm", "heavy thunderstorm", "ragged thunderstorm", "thunderstorm with light drizzle", "thunderstorm with drizzle", "thunderstorm with heavy drizzle" -> R.mipmap.storm
        else -> R.drawable.outline_error_24
    }
}


@Composable
fun WeatherIcon(
    dailyWeather: DailyWeather
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
            .border(1.dp, colorResource(id = R.color.yellow), shape = RoundedCornerShape(20.dp))

    ) {

        Column(
            modifier = Modifier
                .size(220.dp, 160.dp)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${dailyWeather.temp.toInt()} \u00B0 C",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black.copy(alpha = 0.7f)
                )
                Image(
                    painter = painterResource(getWeatherIcon(dailyWeather.icon)),
                    contentDescription = ""
                )

            }
            Text(
                text = dailyWeather.date,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.5f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Humidity",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = "34"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    text = "Condition",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = dailyWeather.condition,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    text = "Rain Chances",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = "34"
                )
            }


        }
    }

}