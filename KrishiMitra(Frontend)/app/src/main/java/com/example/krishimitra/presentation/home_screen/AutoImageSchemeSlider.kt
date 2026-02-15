package com.example.krishimitra.presentation.home_screen

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.krishimitra.R
import com.example.krishimitra.domain.model.govt_scheme_slider.BannerModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AutoImageSchemeSlider(
    modifier: Modifier = Modifier,
    banners: List<BannerModel>

) {
    val pagerState = rememberPagerState(
        initialPage = 0, pageCount = { banners.size })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context).diskCache {
        DiskCache.Builder().directory(context.cacheDir.resolve("offline_images"))
            .maxSizeBytes(100L * 1024L * 1024).build()
    }.build()
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            state = pagerState, modifier = Modifier.fillMaxWidth()
        ) { page ->

            BannerImage(
                imageUrl = banners[page].imageUrl,
                visitLink = banners[page].link,
                context = context,
                imageLoader = imageLoader,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))

            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                SliderCircleButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier
                        .padding(4.dp),
                    isCurrent = pagerState.currentPage == index
                )
            }


        }
    }
}


@Composable
fun SliderCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCurrent: Boolean
) {

    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(if (isCurrent) colorResource(id = R.color.grass_green) else Color.Black)
            .clickable(
                enabled = true, interactionSource = null, onClick = onClick
            )
    )
}


@Composable
fun BannerImage(
    imageUrl: String?,
    visitLink: String?,
    context: Context,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)

    ) {
        AsyncImage(
            model = ImageRequest.Builder(context).data(imageUrl).crossfade(true).build(),
            contentDescription = "Scheme Banner",
            imageLoader = imageLoader,
            modifier = modifier
                .clickable(
                    enabled = true,
                    interactionSource = null,
                    onClick = {
                        visitLink?.let { url ->
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ),
            contentScale = ContentScale.FillBounds

        )
    }
}