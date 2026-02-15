package com.example.krishimitra.presentation.home_screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class RecommendedCropFarmer(
    val imageRes: Int,
    val name: String,
    val soilLabel: String,
    val temperatureLabel: String,
    val rainfallLabel: String,  // Low / Medium / High
    val seasonLabel: String,
    val fertilizerLabel: String
)
@Composable
fun CropRecommendationSliderFarmer(
    crops: List<RecommendedCropFarmer>,
    title: String
) {
    val pagerState = rememberPagerState { crops.size }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // slightly reduced spacing
    ) {
        // Slider Title
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val crop = crops[page]

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp) // less vertical padding
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8F8F8))
                    .padding(12.dp), // inner padding slightly reduced
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = crop.imageRes),
                    contentDescription = crop.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Crop Info Text
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp), // reduced space
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = crop.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = "Soil: ${crop.soilLabel}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Temp: ${crop.temperatureLabel}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Rainfall: ${crop.rainfallLabel}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Season: ${crop.seasonLabel}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Fertilizer: ${crop.fertilizerLabel}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
