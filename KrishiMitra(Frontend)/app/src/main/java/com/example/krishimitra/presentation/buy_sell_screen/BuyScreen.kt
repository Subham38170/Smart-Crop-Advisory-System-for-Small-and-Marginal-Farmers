package com.example.krishimitra.presentation.buy_sell_screen

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.krishimitra.R
import com.example.krishimitra.domain.model.crops_data.CropModel

data class BuyScreenState(
    val cropList: List<CropModel> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyScreen(
    state: BuyScreenState,
    onEvent: (BuySellScreenEvent) -> Unit
) {



    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context).diskCache {
        DiskCache.Builder().directory(context.cacheDir.resolve("offline_images"))
            .maxSizeBytes(100L * 1024L * 1024).build()
    }.build()




    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        CustomizedSearchBar(
            onSearch = {
                onEvent(BuySellScreenEvent.onCropSearch(it))
            },
            onEmptySearch = {
                onEvent(BuySellScreenEvent.loadAllCrops)
            },
            onMicClick = {},
            placeHolder = stringResource(id = R.string.search_crops)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                if (state.isLoading) {
                    CircularProgressIndicator()
                }
            }
            if (state.cropList.isNotEmpty()) {
                items(state.cropList) { item ->

                    CropBuyDataItem(
                        crop = item,
                        onCallClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL)
                                    .apply {
                                        data = "tel:${item.mobileNo}".toUri()
                                    }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        context = context,
                        imageLoader = imageLoader
                    )

                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizedSearchBar(
    onSearch: (String) -> Unit,
    onMicClick: () -> Unit,
    placeHolder: String,
    onEmptySearch: () -> Unit,
    searchValue: String = ""
) {
    var search by rememberSaveable { mutableStateOf(searchValue) }

    Row(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        SearchBarDefaults.InputField(
            modifier = Modifier
                .fillMaxWidth(),
            query = search,
            onQueryChange = {
                search = it
                if (search.isBlank()) onEmptySearch()
            },
            onSearch = onSearch,
            expanded = false,
            onExpandedChange = {},
            placeholder = {
                Text(
                    text = placeHolder
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = onMicClick
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_mic_24),
                        contentDescription = ""
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }


}


@Composable
fun CropBuyDataItem(
    crop: CropModel,
    onCallClick: () -> Unit,
    context: Context,
    imageLoader: ImageLoader
) {
    Box {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color = Color.White)

        ) {

            AsyncImage(
                model = ImageRequest.Builder(context).data(crop.imageUrl).crossfade(true).build(),
                contentDescription = "Crop Image",
                imageLoader = imageLoader,
                modifier = Modifier
                    .weight(3f),
                contentScale = ContentScale.FillBounds

            )

            VerticalDivider()
            Column(
                modifier = Modifier
                    .weight(7f)
                    .padding(8.dp)
            ) {
                TextRow(
                    key = "Name:- ",
                    value = crop.cropName

                )

                TextRow(
                    key = "Variety:- ",
                    value = crop.variety

                )
                TextRow(
                    key = "Price:- ",
                    value = crop.price_per_unit.toString(),
                    trailingValue = "rs/kg"

                )
                TextRow(
                    key = "Quantity:- ",
                    value = crop.quantity.toString()

                )
                TextRow(
                    key = "Location:- ",
                    value = crop.village + ", " + crop.district + ", " + crop.state
                )


            }

        }
        IconButton(
            onClick = onCallClick,
            modifier = Modifier
                .clip(CircleShape)
                .align(Alignment.TopEnd),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = colorResource(id = R.color.slight_dark_green)
            ),
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call"
            )

        }
    }
}

@Composable
fun TextRow(
    key: String,
    value: String,
    trailingValue: String = ""
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = key,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,

            )
        Text(
            text = value + trailingValue,
            modifier = Modifier
                .fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,

            )

    }
}


