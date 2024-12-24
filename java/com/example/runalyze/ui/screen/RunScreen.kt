package com.example.runalyze.ui.screen

import TopBar
import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.runalyze.R
import com.example.runalyze.service.location.models.CurrentRunResult
import com.example.runalyze.service.location.models.PathPoint
import com.example.runalyze.ui.components.RunStatsCard
import com.example.runalyze.ui.theme.md_theme_light_primary
import com.example.runalyze.utils.ComposeUtils
import com.example.runalyze.utils.Destination
import com.example.runalyze.utils.LocationUtils
import com.example.runalyze.utils.RunUtils.firstLocationPoint
import com.example.runalyze.utils.RunUtils.lastLocationPoint
import com.example.runalyze.utils.bitmapDescriptorFromVector
import com.example.runalyze.viewmodel.RunViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay

@Composable
// Displaying a user interface for tracking a run on a map
fun RunScreen(
    navController: NavController,
    viewModel: RunViewModel
) {
    val context = LocalContext.current
    var currentRunResult: CurrentRunResult? = null

    LaunchedEffect(key1 = true) {
        // check permission
        LocationUtils.checkAndRequestLocationSetting(context as Activity)
    }
    var isRunningFinished by rememberSaveable { mutableStateOf(false) }
    var shouldShowRunningCard by rememberSaveable { mutableStateOf(false) }
    // Collects and observes the current run state, running duration, heart rate, and list of heart rate data points from viewModel.
    val runState by viewModel.currentRunState.collectAsStateWithLifecycle()
    val runningDurationInMillis by viewModel.runningDurationInMillis.collectAsStateWithLifecycle()
    val heartRate: Int by viewModel.mBPM.observeAsState(0)
    val listBPM by viewModel.listBPM.observeAsState(mutableListOf(0))
    var markers by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    // Function to handle map clicks and add markers
    val onMapClick: (LatLng) -> Unit = { latLng ->
        markers = markers + latLng // Add the clicked LatLng to the markers list
    }
    LaunchedEffect(key1 = Unit) {
        delay(ComposeUtils.slideDownInDuration + 200L)
        shouldShowRunningCard = true
    }

    // Containing a map (GoogleMap) showing the running path ,a top bar, and the running statistics card.
    Box(modifier = Modifier.fillMaxSize()) {
        Map(
            pathPoints = runState.pathPoints,
            isRunningFinished = isRunningFinished,
            onMapClick = onMapClick // Pass the callback function to the Map composable
        )
        TopBar(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            navController.navigateUp()
        }
        ComposeUtils.SlideDownAnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            visible = shouldShowRunningCard
        ) {
            RunStatsCard(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                onStartPauseButtonClick = {
                    viewModel.playPauseTracking()
                },
                runState = runState,
                heartRate = heartRate,
                durationInMillis = runningDurationInMillis,
                onFinish = {
                    val averageHeartRate = listBPM.filter { it != 0 }.average()
                    isRunningFinished = true
                    viewModel.finishRun(averageHeartRate)
                    currentRunResult = CurrentRunResult(
                        avgHeartRate = averageHeartRate,
                        distanceInMeters = runState.distanceInMeters,
                        timeInMillis = runningDurationInMillis,
                        speedInKMH = runState.speedInKMH
                    )
                    navController.navigate(Destination.RunResultDisplay.route)
                }
            )
        }
    }
}

// handles the display of the map, including managing camera position,

@Composable
private fun BoxScope.Map(
    modifier: Modifier = Modifier,
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    onMapClick: (LatLng) -> Unit // Callback function for handling map clicks
) {
    var mapSize by remember { mutableStateOf(Size(0f, 0f)) }
    var mapCenter by remember { mutableStateOf(Offset(0f, 0f)) }
    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {}
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                mapToolbarEnabled = false,
                compassEnabled = true,
                zoomControlsEnabled = false
            )
        )
    }

    LaunchedEffect(key1 = pathPoints.lastLocationPoint()) {
        pathPoints.lastLocationPoint()?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(it.latLng, 15f)
                )
            )
        }

    }
    ShowMapLoadingProgressBar(isMapLoaded)
    GoogleMap(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                mapSize = size
                mapCenter = center
            },
        uiSettings = mapUiSettings,
        cameraPositionState = cameraPositionState,
        onMapLoaded = { isMapLoaded = true },
        onMapClick = { latLng -> onMapClick(latLng) } // Call the callback function on map click
    ) {
        DrawPathPoints(pathPoints = pathPoints, isRunningFinished = isRunningFinished)
    }
}

// showing a loading progress bar while the map is loading.
@Composable
private fun BoxScope.ShowMapLoadingProgressBar(
    isMapLoaded: Boolean = false
) {
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize(),
        visible = !isMapLoaded,
        enter = EnterTransition.None,
        exit = fadeOut(),
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .wrapContentSize()
        )
    }
}

@Composable
@GoogleMapComposable
private fun DrawPathPoints(
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
) {
    // Define states for the loading progress and place name
    var isLoading by remember { mutableStateOf(true) }
    var placeName by remember { mutableStateOf("") }

    // Show loading progress if the map is loading
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    // Show the map and markers when not loading
    if (!isLoading) {
        val lastMarkerState = rememberMarkerState()
        val lastLocationPoint = pathPoints.lastLocationPoint()
        lastLocationPoint?.let { lastMarkerState.position = it.latLng }

        val firstLocationPoint = pathPoints.firstLocationPoint()
        val firstPoint = remember(key1 = firstLocationPoint) { firstLocationPoint }

        val latLngList = mutableListOf<LatLng>()
        // Add the coordinates to your list of LatLng points
        val hayHassaniCoord = LatLng(33.5736, -7.6761)
        val haHassaniCoord = LatLng(32.5736, -7.6761)

        latLngList += hayHassaniCoord
        latLngList += haHassaniCoord

        // Create markers for the fixed coordinates
        Marker(
            icon = bitmapDescriptorFromVector(
                context = LocalContext.current,
                vectorResId = R.drawable.ic_circle,
                tint = Color.Red.toArgb()
            ),
            state = rememberMarkerState(position = hayHassaniCoord),
            anchor = Offset(0.5f, 0.5f),
            visible = true // Ensure the marker is always visible
        )
        Marker(
            icon = bitmapDescriptorFromVector(
                context = LocalContext.current,
                vectorResId = R.drawable.ic_circle,
                tint = Color.Red.toArgb()
            ),
            state = rememberMarkerState(position = haHassaniCoord),
            anchor = Offset(0.5f, 0.5f),
            visible = true // Ensure the marker is always visible
        )

        // Draw path points
        pathPoints.forEach { pathPoint ->
            if (pathPoint is PathPoint.EmptyLocationPoint) {
                Polyline(
                    points = latLngList.toList(),
                    color = md_theme_light_primary,
                )
                latLngList.clear()
            } else if (pathPoint is PathPoint.LocationPoint) {
                latLngList += pathPoint.latLng
            }
        }

        // Add the last path points
        if (latLngList.isNotEmpty())
            Polyline(
                points = latLngList.toList(),
                color = md_theme_light_primary
            )

        // Show loading animation for the last marker
        val infiniteTransition = rememberInfiniteTransition()
        val lastMarkerPointColor by infiniteTransition.animateColor(
            initialValue = md_theme_light_primary,
            targetValue = md_theme_light_primary.copy(alpha = 0.8f),
            animationSpec = infiniteRepeatable(
                tween(1000),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )

        // Add marker for the last location point
        Marker(
            icon = bitmapDescriptorFromVector(
                context = LocalContext.current,
                vectorResId = R.drawable.ic_circle,
                tint = (if (isRunningFinished) md_theme_light_primary else lastMarkerPointColor).toArgb()
            ),
            state = lastMarkerState,
            anchor = Offset(0.5f, 0.5f),
            visible = lastLocationPoint != null
        )

        // Add marker for the first location point
        firstPoint?.let {
            Marker(
                icon = bitmapDescriptorFromVector(
                    context = LocalContext.current,
                    vectorResId = if (isRunningFinished) R.drawable.ic_circle else R.drawable.ic_circle_hollow,
                    tint = if (isRunningFinished) md_theme_light_primary.toArgb() else null
                ),
                state = rememberMarkerState(position = it.latLng),
                anchor = Offset(0.5f, 0.5f)
            )
        }
    }
}

@Composable
fun PlaceInput(onPlaceNameChange: (String) -> Unit) {
    var placeName by remember { mutableStateOf("") }

    TextField(
        value = placeName,
        onValueChange = {
            placeName = it
            onPlaceNameChange(it)
        },
        label = { Text("Enter place name") }
    )
}
