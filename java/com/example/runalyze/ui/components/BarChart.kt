package com.example.runalyze.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.runalyze.database.Converters
import com.example.runalyze.database.Run
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

// Component for using processed data from and draw the bar graph
@Composable
fun BarChart(data: List<Run>, key: String, screenWidth: Dp) {
    var maxValue by remember { mutableFloatStateOf(0.0F) }
    val textColor = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.Black
    }

    data.map {
        val graphFilter: Float = when (key) {
            "distance" -> it.distanceInMeters.div(1000).toFloat()
            "average heart rate" -> it.avgHeartRate.toFloat()
            else -> it.avgSpeedInKMH
        }
        maxValue = maxOf(graphFilter, maxValue)
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(8.dp)
    ) {
        val barSpacing = 2.dp.toPx()
        val barCount = data.size
        val maxLabelWidth = (maxValue.toString().length)
        val barWidth =
            (screenWidth.toPx() - maxLabelWidth - (barSpacing * (barCount + 1))) / (barCount + 1)
        val maxBarHeight = size.height - 32.dp.toPx()
        val yLabelStep = (maxValue / 5).roundToInt()

        for (i in 0..5) {
            val label = (yLabelStep * i).toString()
            val yLabelPosition = size.height - (i * maxBarHeight / 5)

            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    label,
                    maxLabelWidth.toFloat(),
                    yLabelPosition,
                    Paint().apply {
                        color = textColor.hashCode()
                        textSize = 16.dp.toPx()
                    }
                )
            }
        }

        data.forEachIndexed { index, trainingDetail ->
            val graphFilter: Float = when (key) {
                "distance" -> trainingDetail.distanceInMeters / 1000.0F
                "average heart rate" -> trainingDetail.avgHeartRate.toFloat()
                else -> trainingDetail.avgSpeedInKMH
            }
            val barHeight = (graphFilter.div(maxValue)).times(maxBarHeight)
            val x = (index + 0.75) * (barWidth + barSpacing)
            val y = size.height - barHeight

            drawRect(
                color = Color(255, 59, 48),
                topLeft = Offset(x.toFloat(), y),
                size = Size(barWidth, barHeight),
            )

            val dateFormatter = SimpleDateFormat("dd/MM", Locale.getDefault())
            val dateTime =
                Converters().fromTimestamp(trainingDetail.timestamp)
                    ?.let { dateFormatter.format(it) }
            val textWidth = dateTime?.length?.times(2.dp.toPx())

            drawIntoCanvas { canvas ->
                if (dateTime != null) {
                    if (textWidth != null) {
                        canvas.nativeCanvas.drawText(
                            dateTime,
                            ((x - textWidth / 2).toFloat()),
                            size.height + 16.dp.toPx(),
                            Paint().apply {
                                color = textColor.hashCode()
                                textSize = 16.dp.toPx()
                            }
                        )
                    }
                }
            }
        }
    }
}


