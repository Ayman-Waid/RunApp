package com.example.runalyze.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.runalyze.BottomNavItem

@Composable
// Bottom navigation
fun BottomNavigationMenu(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Planning,
        BottomNavItem.Statistic,
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach {
            NavigationBarItem(
                label = {
                    Text(text = it.title)
                },
                alwaysShowLabel = true,
                selected = currentRoute == it.route,
                onClick = {
                    navController.navigate(it.route)
                },
                icon = { Icon(imageVector = it.icon, contentDescription = it.title) }
            )
        }
    }
}