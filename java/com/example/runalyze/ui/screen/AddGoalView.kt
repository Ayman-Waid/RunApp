package com.example.runalyze.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.runalyze.database.Goal
import com.example.runalyze.service.RunningPlan
import com.example.runalyze.ui.componentLibrary.TopNavigation
import com.example.runalyze.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Set a goal screen
@SuppressLint("RememberReturnType", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalView(
    plan: RunningPlan?,
    viewModel: GoalViewModel,
    navController: NavController
) {
    val openDateRangePickerDialog = remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()
    val formattedStartDate = dateRangePickerState.selectedStartDateMillis?.let {
        val date = Date(it)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dateFormat.format(date)
    }
    val formattedEndDate = dateRangePickerState.selectedEndDateMillis?.let {
        val date = Date(it)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dateFormat.format(date)
    }
    var openTimePickerDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    val isReminderTimeSet = remember { mutableStateOf(false) }
    var targetDistance by rememberSaveable { mutableStateOf("") }
    var targetSpeed by rememberSaveable { mutableStateOf("") }
    var targetHeartRate by rememberSaveable { mutableStateOf("") }
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember { mutableStateOf("") }
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val selectedDays =
        remember { mutableStateListOf(false, false, false, false, false, false, false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    if (plan != null) {
        targetDistance = plan.targetDistance.toString()
        targetSpeed = plan.targetSpeed.toString()
        targetHeartRate = plan.targetHeartRate.toString()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopNavigation(
                text = "Set a goal",
                navController = navController,
            )
        }
    ) { values ->
        Column(
            modifier = Modifier
                .padding(values)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Date picker
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Duration: ",
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { openDateRangePickerDialog.value = true },
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = if (formattedStartDate != null && formattedEndDate != null) {
                            "From $formattedStartDate to $formattedEndDate"
                        } else {
                            "Choose training period"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            // Day-of-the-week checkbox
            Column {
                Text(
                    text = "Repeat: ",
                    fontWeight = FontWeight.Bold
                )
                DayOfWeekSelection(daysOfWeek, selectedDays)
            }

            val cal = Calendar.getInstance()

            cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
            cal.set(Calendar.MINUTE, timePickerState.minute)
            cal.isLenient = false

            Spacer(modifier = Modifier.size(8.dp))

            // Reminder time picker
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Reminder: ",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    if (isReminderTimeSet.value) {
                        formattedTime.value = formatter.format(cal.time)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .border(1.dp, Color.Gray, shape = CircleShape)
                                .padding(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = formattedTime.value)
                                Spacer(modifier = Modifier.size(8.dp))
                                IconButton(
                                    modifier = Modifier.size(20.dp),
                                    onClick = {
                                        isReminderTimeSet.value = false
                                    }) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "Delete reminder"
                                    )
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = { openTimePickerDialog = true },
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(text = "Add Reminder")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            // Target distance text field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Target Distance (km): ", fontWeight = FontWeight.Bold)
                TextField(
                    value = targetDistance,
                    modifier = Modifier
                        .padding(8.dp)
                        .border(1.dp, Color.DarkGray),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    onValueChange = {
                        if (it.toDoubleOrNull() != null) {
                            targetDistance = it
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            // Target speed text field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Target Speed (km/h): ", fontWeight = FontWeight.Bold)
                TextField(
                    value = targetSpeed,
                    modifier = Modifier
                        .padding(8.dp)
                        .border(1.dp, Color.DarkGray),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    onValueChange = {
                        if (it.toDoubleOrNull() != null) {
                            targetSpeed = it
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            // Target heart rate text field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Target Heart Rate (bpm): ", fontWeight = FontWeight.Bold)
                TextField(
                    value = targetHeartRate,
                    modifier = Modifier
                        .padding(8.dp)
                        .border(1.dp, Color.DarkGray),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        if (it.toIntOrNull() != null) {
                            targetHeartRate = it
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            // Save and exit to home screen button
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val reminderTime: String? =
                            if (isReminderTimeSet.value) formattedTime.value else null

                        viewModel.addGoalLiveData(
                            Goal(
                                0,
                                dateRangePickerState.selectedStartDateMillis,
                                dateRangePickerState.selectedEndDateMillis,
                                selectedDays.toList().toString(),
                                reminderTime,
                                targetDistance.toDouble(),
                                targetSpeed.toDouble(),
                                targetHeartRate.toInt(),
                                System.currentTimeMillis()
                            )
                        )
                        navController.navigate("Home")
                    },
                    enabled = targetDistance != "" && targetSpeed != "" && targetHeartRate != ""
                ) {
                    Text(text = "Save and exit to home screen")
                }
            }
        }
    }

    if (openDateRangePickerDialog.value) {
        DateRangePickerDialog(
            state = dateRangePickerState,
            onDismissRequest = { openDateRangePickerDialog.value = false },
            onSaveClick = { _, _ ->
                openDateRangePickerDialog.value = false
            }
        )
    }

    if (openTimePickerDialog) {
        TimePickerDialog(
            state = timePickerState,
            onDismissRequest = { openTimePickerDialog = false },
            onSaveClick = {
                isReminderTimeSet.value = true
                openTimePickerDialog = false
            }
        )
    }
}

// Date picker Dialog: to pick the start date and end date
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    state: DateRangePickerState,
    onDismissRequest: () -> Unit,
    onSaveClick: (startDateMillis: Long, endDateMillis: Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            tonalElevation = AlertDialogDefaults.TonalElevation,
            modifier = Modifier.requiredWidth(LocalConfiguration.current.screenWidthDp.dp * 0.96f)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = {
                            onSaveClick(
                                state.selectedStartDateMillis ?: return@TextButton,
                                state.selectedEndDateMillis ?: return@TextButton
                            )
                            onDismissRequest()
                        },
                        enabled = state.selectedEndDateMillis != null
                    ) {
                        Text("Save")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                DateRangePicker(
                    state = state,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// To render the label for date of the week and their checkboxes
@Composable
fun DayOfWeekSelection(
    daysOfWeek: List<String>,
    selectedDays: MutableList<Boolean>
) {
    LazyRow(
        modifier = Modifier
            .height(75.dp)
            .fillMaxWidth()
    ) {
        itemsIndexed(daysOfWeek) { index, day ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(2.dp)
            ) {
                Text(text = day, modifier = Modifier.weight(1f))
                Checkbox(
                    checked = selectedDays[index],
                    onCheckedChange = { isChecked ->
                        selectedDays[index] = isChecked
                    }
                )
            }
        }
    }

}

// To open the time picker when the add reminder button is clicked
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    state: TimePickerState,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            tonalElevation = AlertDialogDefaults.TonalElevation,
            modifier = Modifier
                .requiredWidth(LocalConfiguration.current.screenWidthDp.dp * 0.96f)
                .fillMaxHeight()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = {
                            onSaveClick()
                            onDismissRequest()
                        },
                        enabled = true
                    ) {
                        Text("Save")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TimePicker(
                    state = state,
                )
            }
        }
    }
}








