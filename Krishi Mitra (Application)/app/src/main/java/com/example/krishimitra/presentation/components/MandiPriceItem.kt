package com.example.krishimitra.presentation.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.krishimitra.data.local.json.getMandiCropImageUrl
import com.example.krishimitra.data.local.json.getMandiModalTrendPrice
import com.example.krishimitra.domain.model.mandi_data.MandiPriceDto

@Composable
fun MandiPriceItem(
    imageSize: Dp = 80.dp,
    mandiPrice: MandiPriceDto,
    context: Context,
    imageLoader: ImageLoader
) {


    val getMandiTrend = getMandiModalTrendPrice(
        context,
        mandiPrice.state,
        mandiPrice.district,
        mandiPrice.market,
        mandiPrice.commodity
    )
    val imageUrl = getMandiCropImageUrl(context, mandiPrice.commodity)
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(4.dp)
        ) {
            Column {

                AsyncImage(
                    model = ImageRequest.Builder(context).data(imageUrl).crossfade(true).build(),
                    contentDescription = "Crop Image",
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .size(imageSize)
                        .padding(4.dp),
                    contentScale = ContentScale.FillBounds

                )
                getMandiTrend?.let { previousPrice ->
                    val change = ((mandiPrice.modal_price.toInt() - previousPrice).toDouble() / previousPrice) * 100
                    val displayText: String
                    val color: Color

                    when {
                        change > 0 -> {
                            displayText = "↑ %.2f%%".format(change)
                            color = Color(0xFF4CAF50) // Green
                        }

                        change < 0 -> {
                            displayText = "↓ %.2f%%".format(-change)
                            color = Color(0xFFF44336) // Red
                        }

                        else -> {
                            displayText = "→ 0.00%"
                            color = Color(0xFF9E9E9E) // Gray
                        }
                    }

                    Text(
                        text = displayText,
                        color = color,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }


            Column {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Commodity : ")
                        }
                        append(mandiPrice.commodity)
                    },
                    overflow = TextOverflow.Ellipsis

                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Variety : ")
                        }
                        append(mandiPrice.variety)
                    },
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Max :")
                            }
                            append("\u20B9")
                            append(mandiPrice.max_price)

                        },
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Min : ")
                            }
                            append("\u20B9")

                            append(mandiPrice.min_price)
                        },
                        overflow = TextOverflow.Ellipsis

                    )
                }
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Modal : ")
                        }
                        append("\u20B9")
                        append(mandiPrice.modal_price)
                    },
                    overflow = TextOverflow.Ellipsis

                )
                Row {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Symbol",
                        modifier = Modifier
                            .size(16.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append(mandiPrice.state)
                            append(", ")
                            append(mandiPrice.district)
                            append(", ")
                            append(mandiPrice.market)
                        },
                        overflow = TextOverflow.Ellipsis

                    )
                }
            }


        }
    }
}

