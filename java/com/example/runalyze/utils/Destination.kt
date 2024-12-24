package com.example.runalyze.utils

import androidx.navigation.NavController
import androidx.navigation.navDeepLink
import com.example.runalyze.BottomNavItem

// Navigation destination
sealed class Destination(val route: String) {
    object CurrentRun : Destination("training") {
        val currentRunUriPattern = "https://runalyze.example.com/$route"
        val deepLinks = listOf(
            navDeepLink {
                uriPattern = currentRunUriPattern
            }
        )
    }

    object AddGoal : Destination("goal?runningPlanId=")

    object RunResultDisplay : Destination("run_result") {
        fun navigateToHome(navController: NavController) {
            navController.navigate(BottomNavItem.Home.route) {
                popUpTo(route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }

        fun navigateToGraph(navController: NavController) {
            navController.navigate(BottomNavItem.Statistic.route) {
                popUpTo(route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    // Navigation
    companion object {
        fun navigateToRunScreen(navController: NavController) {
            navController.navigate(CurrentRun.route)
        }
    }
}