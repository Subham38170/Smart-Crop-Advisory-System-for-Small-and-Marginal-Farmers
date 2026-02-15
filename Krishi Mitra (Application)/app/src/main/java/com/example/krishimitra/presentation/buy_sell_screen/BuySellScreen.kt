package com.example.krishimitra.presentation.buy_sell_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.krishimitra.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuySellScreen(
    buyScreenState: BuyScreenState,
    sellScreenState: SellScreenState,
    onEvent: (BuySellScreenEvent) -> Unit,
    event: SharedFlow<String>,
    scrollBahavior: BottomAppBarScrollBehavior
) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        event.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

    }


    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 2 }

    Box(
        modifier = Modifier
            .fillMaxSize()

            .background(colorResource(id = R.color.light_green))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            BuySellToggleButton(
                pagerState = pagerState,
                scope = scope
            )


            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (it) {
                    0 -> {
                        BuyScreen(
                            state = buyScreenState,
                            onEvent = onEvent
                        )
                    }

                    1 -> {

                        SellScreen(
                            state = sellScreenState,
                            sellCrop = {
                                onEvent(BuySellScreenEvent.onListProduct(it))
                            },
                            onEvent = onEvent
                        )

                    }
                }
            }
        }
    }
}


@Composable
fun BuySellToggleButton(
    pagerState: PagerState,
    scope: CoroutineScope
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(36.dp)

                .border(
                    color = colorResource(id = R.color.slight_dark_green),
                    shape = RoundedCornerShape(40.dp),
                    width = 1.dp
                )
                .background(Color.White, shape = RoundedCornerShape(40.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .height(36.dp)
                    .background(
                        colorResource(id = R.color.slight_dark_green),
                        shape = RoundedCornerShape(40.dp)
                    )
                    .align(if (pagerState.currentPage == 0) Alignment.TopStart else Alignment.TopEnd)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = stringResource(id = R.string.buy),
                    color = if (pagerState.currentPage == 1) colorResource(id = R.color.slight_dark_green) else Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable(
                        enabled = true,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        indication = null
                    )

                )

                Text(
                    text = stringResource(id = R.string.sell),
                    color = if (pagerState.currentPage == 0) colorResource(id = R.color.slight_dark_green) else Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable(
                        enabled = true,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        indication = null
                    )
                )
            }

        }


    }
}