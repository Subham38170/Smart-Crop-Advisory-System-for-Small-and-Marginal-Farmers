package com.example.krishimitra.presentation.nav_graph

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavBar(
    val title: String,
    val icon: ImageVector,
    val route: Routes
) {
    Home(
        title = "Home",
        icon = Icons.Default.Home,
        route = Routes.HomeScreen
    ),

    BuySell(
        title = "BuySell",
        icon = Icons.Default.ShoppingCart,
        route = Routes.BuySellScreen
    ),
    MandiPrice(
        title = "MandiPrice",
        icon = Icons.Default.MailOutline,
        route = Routes.MandiScreen
    ),
    Profile(
        title = "Profile",
        icon = Icons.Default.Person,
        route = Routes.ProfileScreen
    )

}