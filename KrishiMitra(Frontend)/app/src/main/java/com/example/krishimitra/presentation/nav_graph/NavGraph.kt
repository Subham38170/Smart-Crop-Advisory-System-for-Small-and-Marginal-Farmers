package com.example.krishimitra.presentation.nav_graph


import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.krishimitra.R
import com.example.krishimitra.presentation.assistant_screen.AssistantScreen
import com.example.krishimitra.presentation.assistant_screen.AssistantScreenViewModel
import com.example.krishimitra.presentation.auth_screen.AuthScreen
import com.example.krishimitra.presentation.auth_screen.AuthViewModel
import com.example.krishimitra.presentation.buy_sell_screen.BuySellScreen
import com.example.krishimitra.presentation.buy_sell_screen.BuySellScreenViewModel
import com.example.krishimitra.presentation.community_screen.ComunityMainScreen
import com.example.krishimitra.presentation.community_screen.StateCommunityScreen
import com.example.krishimitra.presentation.disease_prediction_screen.DiseasePredictionScreen
import com.example.krishimitra.presentation.disease_prediction_screen.DiseasePredictionViewModel
import com.example.krishimitra.presentation.feedback_screen.FeedbackScreen
import com.example.krishimitra.presentation.feedback_screen.FeedbackScreenViewModel
import com.example.krishimitra.presentation.fertilizer_recommendation.FertilizerRecommendationScreen
import com.example.krishimitra.presentation.home_screen.HomeScreen
import com.example.krishimitra.presentation.home_screen.HomeScreenViewModel
import com.example.krishimitra.presentation.mandi_screen.MandiScreen
import com.example.krishimitra.presentation.mandi_screen.MandiScreenViewModel
import com.example.krishimitra.presentation.notification_screen.NotificationScreen
import com.example.krishimitra.presentation.notification_screen.NotificationScreenViewModel
import com.example.krishimitra.presentation.profile_screen.ProfileScreen
import com.example.krishimitra.presentation.profile_screen.ProfileScreenViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    activity: Activity
) {


    val firebaseAuth = FirebaseAuth.getInstance()

    val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()


    val backstack =
        remember { mutableStateListOf(if (firebaseAuth.uid == null) Routes.AuthScreen else Routes.HomeScreen) }
    val shouldShowBottomBar = backstack.lastOrNull() in listOf(
        Routes.HomeScreen,
        Routes.BuySellScreen,
        Routes.MandiScreen,
        Routes.ProfileScreen
    )


    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {


            AnimatedVisibility(
                visible = firebaseAuth.uid != null && shouldShowBottomBar,

                ) {
                CustomizedBottomAppBar(
                    currentRoute = backstack.last(),
                    navigateTo = {
                        if (backstack.lastOrNull() != it) {
                            backstack.apply {
                                clear()
                                add(it)
                            }

                        }
                    }
                )
            }

        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = backstack.lastOrNull() == Routes.HomeScreen,

                ) {

                FloatingActionButton(
                    onClick = {
                        if (backstack.lastOrNull() != Routes.AssistantScreen) backstack.add(Routes.AssistantScreen())

                    },
                    containerColor = colorResource(id = R.color.slight_dark_green),
                    contentColor = Color.White
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sharp_support_agent_24),
                        contentDescription = "Chat bot agent"
                    )
                }
            }
        }
    ) { innerpadding ->
        NavDisplay(
            entryDecorators = listOf(
                // Add the default decorators for managing scenes and saving state
                rememberSaveableStateHolderNavEntryDecorator(),
                // Then add the view model store decorator
                rememberViewModelStoreNavEntryDecorator()

            ),
            backStack = backstack,
            onBack = {
                backstack.removeLastOrNull()
            },
            entryProvider = entryProvider {
                entry<Routes.AuthScreen> {
                    val authViewModel = hiltViewModel<AuthViewModel>()
                    AuthScreen(
                        state = authViewModel.state.collectAsStateWithLifecycle().value,
                        changeLanguage = authViewModel::changeLanguage,
                        signIn = authViewModel::signIn,
                        signUp = authViewModel::signUp,
                        moveToHomeScreen = {
                            backstack.clear()
                            backstack.add(Routes.HomeScreen)
                        },
                        getLocation = authViewModel::getLocation,
                        onEnableLocationPermission = {
                            authViewModel.onEnableLocationPermission(activity)
                        },
                        errorFlow = authViewModel.error
                    )
                }
                entry<Routes.HomeScreen> {
                    val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
                    HomeScreen(
                        state = homeScreenViewModel.state.collectAsStateWithLifecycle().value,
                        onEvent = homeScreenViewModel::onEvent,
                        moveToMandiScreen = {
                            if (backstack.lastOrNull() != Routes.MandiScreen) backstack.add(Routes.MandiScreen)
                        },
                        moveToDiseasePredictionScreen = { uri ->
                            backstack.apply {
                                clear()
                                add(Routes.DiseasePredictionScreen(uri.toString()))
                            }


                        },
                        scrollBehavior = scrollBehavior,
                        moveToKrishiBazar = {

                            backstack.apply {
                                backstack.clear()
                                add(Routes.BuySellScreen)
                            }
                        },
                        moveToNotificationScreen = {
                            backstack.apply {
                                clear()
                                add(
                                    Routes.NotificationScreen
                                )
                            }
                        },
                        moveToFertilizerRecommendationScreen = {
                            if (backstack.lastOrNull() != Routes.FertilizerRecommendationScreen) backstack.add(
                                Routes.FertilizerRecommendationScreen
                            )
                        }
                    )

                }
                entry<Routes.ProfileScreen> {
                    val profileScreenViewModel = hiltViewModel<ProfileScreenViewModel>()
                    ProfileScreen(
                        logOut = {
                            firebaseAuth.signOut()
                            backstack.apply {
                                clear()
                                add(Routes.AuthScreen)
                            }
                        },
                        state = profileScreenViewModel.state.collectAsStateWithLifecycle().value,
                        moveToFeedbackScreen = {
                            if (backstack.lastOrNull() != Routes.FeedbackScreen) backstack.add(
                                Routes.FeedbackScreen
                            )
                        }
                    )
                }
                entry<Routes.BuySellScreen> {

                    val buySellViewModel = hiltViewModel<BuySellScreenViewModel>()
                    BuySellScreen(
                        buyScreenState = buySellViewModel.buyScreenState.collectAsStateWithLifecycle().value,
                        onEvent = buySellViewModel::onEvent,
                        sellScreenState = buySellViewModel.sellScreenState.collectAsStateWithLifecycle().value,
                        event = buySellViewModel.event,
                        scrollBahavior = scrollBehavior
                    )
                }
                entry<Routes.MandiScreen> {
                    val mandiViewModel = hiltViewModel<MandiScreenViewModel>()
                    MandiScreen(
                        state = mandiViewModel.state.collectAsStateWithLifecycle().value,
                        mandiPrice = mandiViewModel.pagingData.collectAsLazyPagingItems(),
                        onEvent = mandiViewModel::onEvent,
                        scrollBehavior = scrollBehavior
                    )
                }
                entry<Routes.DiseasePredictionScreen> {
                    val diseasePredictionViewModel = hiltViewModel<DiseasePredictionViewModel>()
                    DiseasePredictionScreen(
                        imageUri = it.imageUri?.toUri(),
                        moveBackToScreen = {
                            backstack.removeLastOrNull()
                        },
                        state = diseasePredictionViewModel.state.collectAsStateWithLifecycle().value,
                        event = diseasePredictionViewModel.event,
                        onEvent = diseasePredictionViewModel::onEvent
                    )
                }
                entry<Routes.CommunityMainScreen> {
                    ComunityMainScreen(
                        moveToMessageScreen = { name ->
                            if (backstack.lastOrNull() != Routes.CommunityMainScreen) backstack.add(
                                Routes.StateCommunityScreen(name)
                            )
                        }
                    )
                }
                entry<Routes.StateCommunityScreen> {

                    StateCommunityScreen(
                        state = it.state,
                        onBackClick = {
                            backstack.removeLastOrNull()
                        }
                    )
                }
                entry<Routes.AssistantScreen> {
                    val assistantViewModel = hiltViewModel<AssistantScreenViewModel>()

                    AssistantScreen(
                        onEvent = assistantViewModel::onEvent,
                        state = assistantViewModel.state.collectAsStateWithLifecycle().value,
                        moveBackToHomeScreen = {
                            backstack.removeLastOrNull()
                        },
                        event = assistantViewModel.event
                    )
                }

                entry<Routes.NotificationScreen> {

//                   composable<Routes.NotificationScreen>(
//                        deepLinks = listOf(
//                            navDeepLink {
//                                uriPattern =
//                                    "app://krishimitra.com/notifications?title={title}&body={body}&imageUrl={imageUrl}&webLink={webLink}"
//                                action = Intent.ACTION_VIEW
//
//                            }
//                        )
//                    ) {
//
//                    }
                    val notificationViewModel = hiltViewModel<NotificationScreenViewModel>()
                    NotificationScreen(
                        moveBackToHomeScreen = {
                            backstack.removeLastOrNull()
                        },
                        state = notificationViewModel.state.collectAsStateWithLifecycle().value,
                        onEvent = notificationViewModel::onEvent
                    )
                }
                entry<Routes.FeedbackScreen> {
                    val feedbackScreenViewModel = hiltViewModel<FeedbackScreenViewModel>()
                    FeedbackScreen(
                        state = feedbackScreenViewModel.state.collectAsStateWithLifecycle().value,
                        onEvent = feedbackScreenViewModel::onEvent,
                        event = feedbackScreenViewModel.event,
                        snackbarHostState = snackbarHostState,
                        moveBackToProfileScreen = {
                            backstack.removeLastOrNull()
                        }
                    )
                }
                entry<Routes.FertilizerRecommendationScreen> {
                    FertilizerRecommendationScreen(
                        onBackClick = {
                            backstack.removeLastOrNull()
                        }
                    )
                }
            }
        )
    }

}


@Composable
fun CustomizedBottomAppBar(
    currentRoute: Routes,
    navigateTo: (Routes) -> Unit
) {
    BottomAppBar(
        containerColor = colorResource(id = R.color.light_green)
    ) {
        BottomNavBar.entries.forEach { bottomBarInfo ->
            val isSelected = bottomBarInfo.route == currentRoute
            NavigationBarItem(
                selected = isSelected,
                icon = {
                    Icon(
                        imageVector = bottomBarInfo.icon,
                        contentDescription = bottomBarInfo.name
                    )

                },
                onClick = {
                    navigateTo(bottomBarInfo.route)

                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(id = R.color.slight_dark_green),
                    unselectedIconColor = Color.Black
                )
            )


        }
    }
}


