package com.example.runalyze.ui.screen.runningPlan

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.runalyze.ui.componentLibrary.RunningPlanItemDetail
import com.example.runalyze.service.RunningPlan

@Composable
// Display running plan list fetch from network
fun RunningPlanList(runningPlanList: List<RunningPlan>, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        if (runningPlanList.isNotEmpty()) {
            runningPlanList.forEach { runningPlan ->
                RunningPlanListItem(runningPlan = runningPlan, navController = navController)
            }
        } else {
            Text("Loading")
        }
    }

}

@Composable
// View for item in running plan list
fun RunningPlanListItem(runningPlan: RunningPlan, navController: NavController) {
    val planId = runningPlan.planId
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("runningPlanDetail/${planId}")
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = runningPlan.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                RunningPlanItemDetail(Icons.Filled.CalendarMonth, "Duration", runningPlan.duration)
                RunningPlanItemDetail(
                    Icons.AutoMirrored.Filled.DirectionsRun,
                    "Frequency",
                    runningPlan.frequency
                )
                RunningPlanItemDetail(Icons.Filled.StackedLineChart, "Level", runningPlan.level)

            }
            AsyncImage(
                model = runningPlan.imageUrl,
                contentDescription = "Running illustration",
                modifier = Modifier
                    .weight(1f)
                    .size(width = 150.dp, height = 150.dp)
            )
        }
    }
}

