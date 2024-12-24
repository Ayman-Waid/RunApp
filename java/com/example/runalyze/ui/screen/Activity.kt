package com.example.runalyze.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runalyze.R
import com.example.runalyze.database.Converters
import com.example.runalyze.database.Goal
import com.example.runalyze.database.Run
import com.example.runalyze.ui.componentLibrary.ScreenHeader
import com.example.runalyze.ui.components.BarChart
import com.example.runalyze.viewmodel.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.round

// Create view for Statistics tab
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity(viewModel: ActivityViewModel) {
    val allData by viewModel.allTrainings.observeAsState(emptyList())
    val goal = viewModel.getNewestGoal().observeAsState(null)
    var summarizedData by remember { mutableStateOf(emptyList<Run>()) }
    var filteredData by remember { mutableStateOf(emptyList<Run>()) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var displayedView by remember {
        mutableStateOf("summary")
    }
    var selectedButton by remember { mutableIntStateOf(1) }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ScreenHeader(stringResource(id = R.string.activity_header))
        }) { values ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(values),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 64.dp, end = 64.dp, top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = {
                        displayedView = "summary"
                        selectedButton = 1
                        filteredData = summarizedData.filter {
                            val day = Converters().fromTimestamp(it.timestamp).toString().split("-")
                            val today = Calendar.getInstance()
                            day[2].toInt() == today.get(Calendar.DAY_OF_MONTH) && day[1].toInt() == today.get(
                                Calendar.MONTH
                            ) + 1 && day[0].toInt() == today.get(Calendar.YEAR)
                        }
                    }, modifier = Modifier.background(
                            if (selectedButton == 1) Color(255, 59, 48) else Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Text(
                        text = "Today",
                        color = if (selectedButton == 1) Color.White else Color(255, 59, 48),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(
                    onClick = {
                        displayedView = "graph"
                        selectedButton = 2
                        filteredData = summarizedData.filter {
                            val month = Converters().fromTimestamp(it.timestamp).toString()
                                .split("-")[1].toInt()
                            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)

                            month == currentMonth + 1
                        }
                    }, modifier = Modifier.background(
                            if (selectedButton == 2) Color(255, 59, 48) else Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Text(
                        text = "Month",
                        color = if (selectedButton == 2) Color.White else Color(255, 59, 48),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(
                    onClick = {
                        displayedView = "graph"
                        selectedButton = 3
                        filteredData = summarizedData.filter {
                            val year = Converters().fromTimestamp(it.timestamp).toString()
                                .split("-")[0].toInt()
                            val currentYear = Calendar.getInstance().get(Calendar.YEAR)

                            year == currentYear
                        }
                    }, modifier = Modifier.background(
                            if (selectedButton == 3) Color(255, 59, 48) else Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Text(
                        text = "Year",
                        color = if (selectedButton == 3) Color.White else Color(255, 59, 48),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            summarizedData = prepareRunSummary(allData)
            if (displayedView == "graph") {
                GraphView(allData = filteredData, screenWidth = screenWidth)
            } else {
                displayedView = "summary"
                filteredData = summarizedData.filter {
                    val day = Converters().fromTimestamp(it.timestamp).toString().split("-")
                    val today = Calendar.getInstance()

                    day[2].toInt() == today.get(Calendar.DAY_OF_MONTH) && day[1].toInt() == today.get(
                        Calendar.MONTH
                    ) + 1 && day[0].toInt() == today.get(Calendar.YEAR)
                }
                SummaryView(filteredData, goal.value)
            }
        }
    }
}

// Prepare data for today training summary
fun prepareRunSummary(data: List<Run>): List<Run> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val groupedRuns = mutableMapOf<String, MutableList<Run>>()

    for (run in data) {
        val dateString = sdf.format(Date(run.timestamp))
        if (!groupedRuns.containsKey(dateString)) {
            groupedRuns[dateString] = mutableListOf()
        }
        groupedRuns[dateString]?.add(run)
    }

    val summarizedRuns = mutableListOf<Run>()

    for ((dateString, runs) in groupedRuns) {
        val totalDistance: Int = runs.sumOf { it.distanceInMeters }
        val totalDuration: Long = runs.sumOf { it.durationInMillis }
        val avgSpeed: Double = runs.sumOf { it.avgSpeedInKMH.toDouble() } / runs.size
        val avgHeartRate: Double = runs.sumOf { it.avgHeartRate } / runs.size

        summarizedRuns.add(
            Run(
                0,
                timestamp = sdf.parse(dateString)?.time ?: 0,
                avgSpeedInKMH = avgSpeed.toFloat(),
                distanceInMeters = totalDistance,
                durationInMillis = totalDuration,
                avgHeartRate = avgHeartRate
            )
        )
    }
    return summarizedRuns
}

// Component to render graphs for month and year buttons
@Composable
fun GraphView(allData: List<Run>, screenWidth: Dp) {
    Text(text = "Distance (Km)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Box {
        BarChart(data = allData, screenWidth = screenWidth, key = "distance")
    }
    Spacer(modifier = Modifier.height(32.dp))
    Text(text = "Average Speed (Km/h)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Box {
        BarChart(data = allData, screenWidth = screenWidth, key = "speed")
    }
    Spacer(modifier = Modifier.height(32.dp))
    Text(text = "Average Heart Rate (bpm)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Box {
        BarChart(data = allData, screenWidth = screenWidth, key = "average heart rate")
    }
    Spacer(modifier = Modifier.height(50.dp))
}

// Progress component for today training summary
@Composable
fun SummaryView(data: List<Run>, goal: Goal?) {
    Column(modifier = Modifier.padding(16.dp)) {
        CurrentGoalView(data = goal)
        Spacer(modifier = Modifier.height(32.dp))
        if (data.isNotEmpty()) {
            TrainingSummary(run = data[0], goal)
        } else {
            Text(text = "No recorded training session for today")
        }
    }
}

// Render the latest goal in the database.
// The goal is have the most recent createdAt timestamp will be displayed as the current goal.
@Composable
fun CurrentGoalView(data: Goal?) {
    if (data == null) {
        Text(text = "No goal is recorded")
    } else {
        Column {
            Text(text = "Target", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Distance")
                    Text(
                        text = "${data.targetDistanceInKm}",
                        fontSize = 30.sp,
                        color = Color(159, 220, 177)
                    )
                    Text(text = "Km")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Speed")
                    Text(
                        text = "${data.targetSpeedInKmh}",
                        fontSize = 30.sp,
                        color = Color(159, 220, 177)
                    )
                    Text(text = "Km/h")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Heart Rate")
                    Text(
                        text = "${data.targetHeartRateInBpm}",
                        fontSize = 30.sp,
                        color = Color(159, 220, 177)
                    )
                    Text(text = "Bpm")
                }
            }
        }
    }
}

// Combine the SummaryView and CurrentGoalView into TrainingSummary Screen for Today button
@Composable
fun TrainingSummary(run: Run, goal: Goal?) {
    Column {
        Text(text = "Progress", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (goal != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val progress =
                        round((run.distanceInMeters / 1000) / goal.targetDistanceInKm * 100).toInt()
                    Text(
                        text = "$progress%", fontSize = 50.sp, color = Color(241, 78, 47)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Distance")
                Text(
                    text = "${run.distanceInMeters / 1000}",
                    fontSize = 30.sp,
                    color = Color(241, 78, 47)
                )
                Text(text = "Km")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Speed")
                Text(
                    text = "${round(run.avgSpeedInKMH).toDouble()}",
                    fontSize = 30.sp,
                    color = Color(241, 78, 47)
                )
                Text(text = "Km/h")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Heart Rate")
                Text(
                    text = "${round(run.avgHeartRate).toInt()}",
                    fontSize = 30.sp,
                    color = Color(241, 78, 47)
                )
                Text("Bpm")
            }
        }
    }
}
