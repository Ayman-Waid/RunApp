package com.example.runalyze.ui.componentLibrary

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
// View for detail in running plan item
fun RunningPlanItemDetail(icon: ImageVector, contentDescription: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp)
        )
        TextModifiedWithPaddingStart(label, size = 16)
    }
}

// TextView
@Composable
fun TextModifiedWithPaddingStart(
    string: String,
    size: Int = 12,
    paddingStart: Int = 5,
) {
    Text(
        text = string,
        fontSize = size.sp,
        modifier = Modifier.padding(start = paddingStart.dp)
    )
}

@Composable
// Run status component
fun RunStats(text: String, value: String, unit: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(5.dp)
        )
        Text(
            text = value,
            modifier = Modifier.padding(5.dp)
        )
        Text(
            text = unit,
            modifier = Modifier.padding(5.dp)
        )
    }
}




