import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
// Top navigation for running screen
fun TopBar(
    modifier: Modifier = Modifier,
    onUpButtonClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LargeFloatingActionButton(
            onClick = onUpButtonClick,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = CircleShape,
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBackIos,
                "Return",
                Modifier
                    .size(32.dp)
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp)
            )
        }
    }
}