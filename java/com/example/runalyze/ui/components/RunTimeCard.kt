package com.example.runalyze.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.runalyze.utils.RunUtils

@Composable
// Running time card
fun RunTimeCard(
    modifier: Modifier,
    durationInMillis: Long,
    isRunning: Boolean,
    onStartPauseButtonClick: () -> Unit,
    onFinish: () -> Unit
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Running Time",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = RunUtils.getFormattedStopwatchTime(durationInMillis),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        }
        if (!isRunning && durationInMillis > 0) {
            IconButton(
                onClick = onFinish,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = MaterialTheme.shapes.medium
                    )
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    Icons.Outlined.Save,
                    contentDescription = "Save run",
                    modifier = Modifier
                        .size(16.dp),
                    tint = MaterialTheme.colorScheme.onError
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
        IconButton(
            onClick = onStartPauseButtonClick,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                if (isRunning) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                contentDescription = if (isRunning) "Pause" else "Start",
                modifier = Modifier
                    .size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

