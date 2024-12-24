package com.example.runalyze.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.runalyze.R
import com.example.runalyze.ui.componentLibrary.RunStats
import com.example.runalyze.utils.Destination
import com.example.runalyze.utils.RunUtils
import com.example.runalyze.viewmodel.RunViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
// Run result screen
fun RunResult(navController: NavController, viewModel: RunViewModel) {
    val durationInMinutes = viewModel.currentRunResult?.timeInMillis?.div(60000.0)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.finish_run),
                contentDescription = stringResource(id = R.string.finish_image),
                contentScale = ContentScale.Fit,
            )
            Text(
                text = stringResource(id = R.string.congrats),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 24.dp),
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                RunStats(
                    text = stringResource(id = R.string.distance),
                    value = String.format(
                        "%.2f",
                        (viewModel.currentRunResult?.distanceInMeters?.div(1000f))
                    ),
                    unit = stringResource(id = R.string.distance_unit)
                )
                RunStats(
                    text = stringResource(id = R.string.avgSpeed),
                    value = (viewModel.currentRunResult?.speedInKMH).toString(),
                    unit = stringResource(id = R.string.speed_unit)
                )
                if (durationInMinutes != null) {
                    RunStats(
                        text = stringResource(id = R.string.duration),
                        value = if (durationInMinutes <= 60) {
                            String.format("%.2f", durationInMinutes)
                        } else
                            String.format(
                                "%.2f",
                                RunUtils.getRunTimeInHours(viewModel.currentRunResult?.timeInMillis!!)
                            ),
                        unit = stringResource(id = R.string.duration_unit)
                    )
                }
                RunStats(
                    text = stringResource(id = R.string.heart_rate),
                    value = if (viewModel.currentRunResult?.avgHeartRate == 0.0) "Not available" else
                        String.format("%.2f", (viewModel.currentRunResult?.avgHeartRate)),
                    unit = if (viewModel.currentRunResult?.avgHeartRate != 0.0) "bpm" else ""
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        Destination.RunResultDisplay.navigateToHome(navController)
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.home_button),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        Destination.RunResultDisplay.navigateToGraph(navController)
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.stats_button),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}




