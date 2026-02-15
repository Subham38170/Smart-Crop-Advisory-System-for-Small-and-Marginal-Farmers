package com.example.krishimitra.presentation.fertilizer_recommendation

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.krishimitra.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FertilizerRecommendationScreen(
    onBackClick: () -> Unit
) {
    var selectedSoil by remember { mutableStateOf<String?>(null) }
    var prevGrownCrop by remember { mutableStateOf<String?>(null) }
    var selectedIrrigation by remember { mutableStateOf<String?>(null) }
    var selectedFymCompost by remember { mutableStateOf<String?>(null) }
    var selectedCurrentCrop by remember { mutableStateOf<String?>(null) }


    var selectedMoisture by remember { mutableStateOf<String?>(null) }
    val pagerState = rememberPagerState { 6 }
    val scope = rememberCoroutineScope()
    Scaffold(
        containerColor = colorResource(id = R.color.light_green),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.slight_dark_green)
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.fertilizer_recommendation),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) {


        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(it),
            userScrollEnabled = false
        ) {

            when (it) {
                0 -> {
                    SoilType(
                        onSelect = { selectedSoil = it },
                        selectedSoil = selectedSoil,
                        onNextClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }

                        }
                    )
                }

                1 -> {
                    SoilMoisture(
                        onSelect = {
                            selectedMoisture = it
                        },
                        selectedMoisture = selectedMoisture,
                        onNextClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        },
                        onPrevClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                    )
                }

                2 -> {
                    PreviousCropSelection(
                        onSelect = {
                            prevGrownCrop = it
                        },
                        selectedCrop = prevGrownCrop,
                        onNextClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(3)
                            }
                        },
                        onPrevClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    )
                }

                3 -> {
                    IrrigationAndFYMScreen(
                        selectedIrrigation = selectedIrrigation,
                        selectedFYM = selectedFymCompost,
                        onIrrigationSelect = {
                            selectedIrrigation = it
                        },
                        onFYMSelect = {
                            selectedFymCompost = it
                        },
                        onNextClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(4)
                            }
                        },
                        onPrevClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        }
                    )
                }

                4 -> {
                    CurrentCropSelection(
                        selectedCrop = selectedCurrentCrop,
                        onSelect = {
                            selectedCurrentCrop = it
                        },
                        onNextClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(5)
                            }
                        },
                        onPrevClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(3)
                            }
                        }
                    )
                }
                5 ->{
                    FertilizerResultScreen()
                }
            }

        }
    }

}


