package com.example.runalyze.ui

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.runalyze.BottomNavItem
import com.example.runalyze.service.RunningPlan
import com.example.runalyze.ui.components.BottomNavigationMenu
import com.example.runalyze.ui.screen.Activity
import com.example.runalyze.ui.screen.AddGoalView
import com.example.runalyze.ui.screen.Home
import com.example.runalyze.ui.screen.RunResult
import com.example.runalyze.ui.screen.RunScreen
import com.example.runalyze.ui.screen.runningPlan.RunningPlanDetailScreen
import com.example.runalyze.ui.screen.runningPlan.RunningPlanListScreen
import com.example.runalyze.utils.Destination
import com.example.runalyze.viewmodel.ActivityViewModel
import com.example.runalyze.viewmodel.GoalViewModel
import com.example.runalyze.viewmodel.RunViewModel
import com.example.runalyze.viewmodel.RunningPlanViewModel

@Composable

fun RunalyzeApp(
    goalViewModel: GoalViewModel,
    activityViewModel: ActivityViewModel,
    runViewModel: RunViewModel,
    data: Uri?
) {
    val scrollState = rememberScrollState()
    val navController = rememberNavController()

    LaunchedEffect(key1 = true){
        if(data != null) Destination.navigateToRunScreen(navController)
    }
    MainScreen(
        navHostController = navController,
        scrollState = scrollState,
        goalViewModel = goalViewModel,
        activityViewModel = activityViewModel,
        runViewModel = runViewModel
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun MainScreen(
    navHostController: NavHostController,
    scrollState: ScrollState,
    goalViewModel: GoalViewModel,
    activityViewModel: ActivityViewModel,
    runViewModel: RunViewModel
) {
    var showBottomBar by remember { mutableStateOf(true) }
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    // Set conditon for showing bottom bar
    showBottomBar = when (navBackStackEntry?.destination?.route) {
        Destination.CurrentRun.route -> false
        else -> true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar)
                BottomNavigationMenu(navController = navHostController)
        }
    )
    { innerPadding ->
        Box(
            modifier = Modifier.padding(
                PaddingValues(
                    0.dp,
                    0.dp,
                    0.dp,
                    innerPadding.calculateBottomPadding()
                )
            )
        ) {
            Navigation(
                navController = navHostController,
                goalViewModel = goalViewModel,
                activityViewModel = activityViewModel,
                runViewModel = runViewModel
            )
        }
    }
}

@Composable
// Navigation for the app
fun Navigation(
    navController: NavHostController,
    goalViewModel: GoalViewModel,
    activityViewModel: ActivityViewModel,
    runViewModel: RunViewModel
) {
    val runningPlanViewModel = RunningPlanViewModel()
    runningPlanViewModel.getRunningPlanList()
    val runningPlanList: List<RunningPlan> by runningPlanViewModel.runningPlanList.observeAsState(
        mutableListOf()
    )

    NavHost(navController = navController, startDestination = BottomNavItem.Home.route) {
        bottomNavigation(navController = navController, activityViewModel, runningPlanList)
        composable(BottomNavItem.Home.route) {
            Home(navController = navController)
        }
        composable(Destination.CurrentRun.route) {
            RunScreen(navController = navController, viewModel = runViewModel)
        }
        composable(
            route = "goal?runningPlanId={runningPlanId}",
            arguments = listOf(navArgument("runningPlanId") { defaultValue = -1 })
        ) {backStackEntry ->
            val id = backStackEntry.arguments?.getInt("runningPlanId") ?: 0
            val plan = runningPlanList.find { it.planId == id }
            AddGoalView(plan, goalViewModel, navController)
        }
        composable(BottomNavItem.Planning.route) {
            RunningPlanListScreen(runningPlanList, navController = navController)
        }
        composable(
            "runningPlanDetail/{runningPlanId}",
            arguments = listOf(navArgument("runningPlanId") { type = NavType.IntType })
        ) { backStackEntry ->
            RunningPlanDetailScreen(
                backStackEntry.arguments?.getInt("runningPlanId") ?: 1,
                runningPlanList,
                navController
            )
        }
        composable(Destination.RunResultDisplay.route){
            RunResult(navController, viewModel = runViewModel)
        }
    }
}

// Build bottom navigation
fun NavGraphBuilder.bottomNavigation(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    runningPlanList: List<RunningPlan>
) {
    composable(BottomNavItem.Home.route) {
        Home(navController = navController)
    }
    composable(BottomNavItem.Planning.route) {
        RunningPlanListScreen(runningPlanList, navController = navController)
    }
    composable(BottomNavItem.Statistic.route) {
        Activity(activityViewModel)
    }
}