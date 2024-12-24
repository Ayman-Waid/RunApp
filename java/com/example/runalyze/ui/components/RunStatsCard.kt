package com.example.runalyze.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.runalyze.R
import com.example.runalyze.service.location.models.CurrentRunState
import java.math.RoundingMode

@Composable
// Running status card
fun RunStatsCard(
    modifier: Modifier,
    durationInMillis: Long = 0L,
    runState: CurrentRunState,
    heartRate: Int,
    onStartPauseButtonClick: () -> Unit,
    onFinish: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 24.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        RunTimeCard(
            modifier = Modifier
                .padding(
                    top = 24.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 16.dp
                )
                .fillMaxWidth(),
            durationInMillis = durationInMillis,
            isRunning = runState.isTracking,
            onStartPauseButtonClick = onStartPauseButtonClick,
            onFinish = onFinish
        )
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .padding(bottom = 10.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            RunningStatItem(
                painter = painterResource(id = R.drawable.distance),
                value = String.format("%.2f",(runState.distanceInMeters / 1000f)),
                unit = stringResource(id = R.string.distance_unit)
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .padding(vertical = 5.dp)
                    .align(Alignment.CenterVertically)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
            )
            RunningStatItem(
                painter = painterResource(id = R.drawable.speed),
                value = String.format("%.2f",runState.speedInKMH),
                unit = stringResource(id = R.string.speed_unit)
            )
            if (heartRate != 0) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterVertically)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        )
                )
                RunningStatItem(
                    painter = painterResource(id = R.drawable.heart_beat),
                    value = heartRate.toString(),
                    unit = stringResource(id = R.string.heart_rate_unit)
                )
            }

        }
    }
}

@Composable
@Preview(showBackground = true)
private fun RunStatsCardPreview() {

    RunStatsCard(
        modifier = Modifier,
        durationInMillis = 5400000,
        runState = CurrentRunState(
            distanceInMeters = 600,
            speedInKMH = (6.935 /* m/s */ * 3.6).toBigDecimal()
                .setScale(2, RoundingMode.HALF_UP)
                .toFloat(),
            isTracking = false
        ),
        heartRate = 70,
        onStartPauseButtonClick = {},
        onFinish = {}
    )
}