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

data class SelectionItem(
    val imageRes: Int,
    val englishName: String,
    @StringRes val labelRes: Int
)

@Composable
fun IrrigationAndFYMScreen(
    selectedIrrigation: String?,
    selectedFYM: String?,
    onIrrigationSelect: (String) -> Unit,
    onFYMSelect: (String) -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit

) {
    val irrigationTypes = listOf(
        SelectionItem(R.drawable.rainified, "Rainfied", R.string.rainfed),
        SelectionItem(R.drawable.canal, "Canal", R.string.cancel),
        SelectionItem(R.drawable.borewell, "Borewell", R.string.borewell)
    )

    val fymTypes = listOf(
        SelectionItem(R.drawable.compost, "FYM", R.string.yes),
        SelectionItem(R.drawable.compost, "Compost", R.string.no)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Irrigation Section
        Text(
            text = stringResource(id = R.string.irrigation_type),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            irrigationTypes.forEach { item ->
                SelectionItemView(
                    item = item,
                    selected = selectedIrrigation == item.englishName,
                    onClick = { onIrrigationSelect(item.englishName) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Text(
            text = stringResource(id = R.string.manure_compost),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            fymTypes.forEach { item ->
                SelectionItemView(
                    item = item,
                    selected = selectedFYM == item.englishName,
                    onClick = { onFYMSelect(item.englishName) },
                    modifier = Modifier.weight(1f)
                )
            }
            if (fymTypes.size < 3) Box(modifier = Modifier.weight(1f))
        }

        // Next Button
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onPrevClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.slight_dark_green),
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f),
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
                enabled = selectedIrrigation != null && selectedFYM != null
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
private fun SelectionItemView(
    item: SelectionItem,
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
            painter = painterResource(id = item.imageRes),
            contentDescription = stringResource(id = item.labelRes),
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
        Text(text = stringResource(id = item.labelRes))
    }
}
