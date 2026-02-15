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


data class PreviousCrop(
    val imageRes: Int,
    val englishName: String,
    @StringRes val labelRes: Int
)

@Composable
fun PreviousCropSelection(
    onSelect: (String) -> Unit,
    selectedCrop: String?,
    onNextClick: () -> Unit,
    onPrevClick: ()-> Unit
) {
    val crops = listOf(
        PreviousCrop(R.drawable.legumes_, "Legumes(Pulses,Soyabean)", R.string.legumes),
        PreviousCrop(R.drawable.cereals,"Cereals(Rice,Wheat,Maize)", R.string.cereals),
        PreviousCrop(R.drawable.oil_seeds, "Oil seeds", R.string.oil_seeds),
        PreviousCrop(R.drawable.vegetables, "Vegetables", R.string.wheat),
        PreviousCrop(R.drawable.others, "Others", R.string.others)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = stringResource(id = R.string.previous_crop),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        // First row: 3 crops
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            crops.take(3).forEach { crop ->
                CropItem(
                    crop = crop,
                    selected = selectedCrop == crop.englishName,
                    onClick = { onSelect(crop.englishName) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Second row: 2 crops
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            crops.drop(3).forEach { crop ->
                CropItem(
                    crop = crop,
                    selected = selectedCrop == crop.englishName,
                    onClick = { onSelect(crop.englishName) },
                    modifier = Modifier.weight(1f)
                )
            }
            Box(modifier = Modifier.weight(1f)) // empty space to center 2 items
        }

        // Next button
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onPrevClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.slight_dark_green),
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.prev),
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onNextClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.slight_dark_green),
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f),
                enabled = selectedCrop != null
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
private fun CropItem(
    crop: PreviousCrop,
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
            painter = painterResource(id = crop.imageRes),
            contentDescription = stringResource(id = crop.labelRes),
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
        Text(text = stringResource(id = crop.labelRes))
    }
}
