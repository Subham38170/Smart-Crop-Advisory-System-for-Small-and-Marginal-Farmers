package com.example.krishimitra.presentation.home_screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import com.example.krishimitra.domain.model.govt_scheme_slider.BannerModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.krishimitra.R





@Composable
fun AutoImageNewsSlider(
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

            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                BannerImage(
                    imageUrl = banners[page].imageUrl,
                    context = context,
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(),
                    visitLink = null
                )
                Text(
                    text = banners[page].title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .padding(4.dp)
                        .height(40.dp),
                    overflow = TextOverflow.Ellipsis
                )


                Text(
                    text = banners[page].description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(4.dp)
                        .height(120.dp),
                    overflow = TextOverflow.Ellipsis

                )

                TextButton(
                    onClick = {
                        banners[page].link?.let {

                            try {
                                val intent = Intent(Intent.ACTION_VIEW, it.toUri())
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    interactionSource = remember { MutableInteractionSource() },
                    modifier = Modifier.fillMaxWidth()

                ) {
                    Text(
                        text = stringResource(id = R.string.read_more)+" -->",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color.Blue

                    )
                }

            }
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
