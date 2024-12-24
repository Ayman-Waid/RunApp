package com.example.runalyze.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.runalyze.R


@Composable
// Running status item component
fun RunningStatItem(
    painter: Painter,
    value: String,
    unit: String
) {
    Row(modifier = Modifier.padding(4.dp)) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 4.dp)
                .size(20.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(
            modifier = Modifier
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = unit,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RunningStatItemPreview() {
    RunningStatItem(painter = painterResource(id = R.drawable.logo), value = "6", unit = "km")
}