package com.example.krishimitra.presentation.fertilizer_recommendation

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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

data class MoistureLevel(
    val imageRes: Int,
    val value: String,
    @StringRes val labelRes: Int
)

@Composable
fun SoilMoisture(
    onSelect: (String) -> Unit,
    selectedMoisture: String?,
    onNextClick: () -> Unit,
    onPrevClick: ()-> Unit
) {

    val moistureLevels = listOf(
        MoistureLevel(R.drawable.low_moistured, "Low", R.string.low_moistured),
        MoistureLevel(R.drawable.mid_moistured, "Medium", R.string.medium_moistured),
        MoistureLevel(R.drawable.high_moistured, "High", R.string.high_moistured)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.how_wet_your_soil_is),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        // Single row for 3 options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            moistureLevels.forEach { moisture ->
                MoistureItem(
                    moisture = moisture,
                    selected = selectedMoisture == moisture.value,
                    onClick = { onSelect(moisture.value) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

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
                enabled = selectedMoisture != null
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
private fun MoistureItem(
    moisture: MoistureLevel,
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
            painter = painterResource(id = moisture.imageRes),
            contentDescription = stringResource(id = moisture.labelRes),
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
        Text(text = stringResource(id = moisture.labelRes))
    }
}
