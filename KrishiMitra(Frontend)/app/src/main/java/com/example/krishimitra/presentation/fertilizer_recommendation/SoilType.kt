package com.example.krishimitra.presentation.fertilizer_recommendation

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.krishimitra.R


data class Soil(
    val imageRes: Int,
    val englishName: String,
    @StringRes val labelRes: Int
)

@Composable
fun SoilType(
    onSelect: (String) -> Unit,
    selectedSoil: String?,
    onNextClick: () -> Unit
) {

    val soilTypes = listOf(
        Soil(R.drawable.red_soil, "Red Soil", R.string.red_soil),
        Soil(R.drawable.black_soil, "Black Soil", R.string.black_soil),
        Soil(R.drawable.sandy_soil, "Sandy Soil", R.string.sandy_soil),
        Soil(R.drawable.clay_soil, "Clay Soil", R.string.clayed_soil),
        Soil(R.drawable.loamy_soil, "Loamy Soil", R.string.loamy_soil)
    )



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = stringResource(id = R.string.which_type_of_soil),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            soilTypes.take(3).forEach { soil ->
                SoilItem(
                    soil = soil,
                    selected = selectedSoil == soil.englishName,
                    onClick = {
                        onSelect(soil.englishName)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            soilTypes.drop(3).forEach { soil ->
                SoilItem(
                    soil = soil,
                    selected = selectedSoil == soil.englishName,
                    onClick = {
                        onSelect(soil.englishName)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
            )

            Button(
                onClick = onNextClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.slight_dark_green),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f),
                enabled = selectedSoil != null
            ) {
                Text(
                    text = stringResource(id = R.string.next),
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }
}

@Composable
private fun SoilItem(
    soil: Soil,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = soil.imageRes),
            contentDescription = stringResource(id = soil.labelRes),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) Color.Green else Color.Gray,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = stringResource(id = soil.labelRes))
    }
}
