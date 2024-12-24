package com.example.runalyze

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.ui.graphics.vector.ImageVector

//Represents different navigation items for a bottom navigation bar
sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : BottomNavItem("home", icon = Icons.Outlined.Home, title = "Home")
    object Planning :
        BottomNavItem("plan", icon = Icons.AutoMirrored.Outlined.DirectionsRun, title = "Plan")
    object Statistic :
        BottomNavItem(route = "activity", icon = Icons.Outlined.QueryStats, title = "Statistics")
}
