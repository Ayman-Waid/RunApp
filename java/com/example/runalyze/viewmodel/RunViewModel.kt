package com.example.runalyze.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.runalyze.RunalyzeApp
import com.example.runalyze.database.AppDb
import com.example.runalyze.database.Run
import com.example.runalyze.service.location.TrackingManager
import com.example.runalyze.service.location.models.CurrentRunResult
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.util.Date

// Responsible for managing data related to tracking run feature in the app.
class RunViewModel(
    private val trackingManager: TrackingManager,
    private val roomDb: AppDb,
) : ViewModel() {

    private val tag = "Runalyze Debug"
    val currentRunState = trackingManager.currentRunState
    val runningDurationInMillis = trackingManager.trackingDurationInMs

    // Heart rate
    val mBPM = MutableLiveData(0)
    val highmBPM = MutableLiveData(0)
    val lowmBPM = MutableLiveData(300)
    val listBPM = MutableLiveData(mutableListOf<Int>())
    var currentRunResult: CurrentRunResult? = null

    // Function toggles between starting or pausing the tracking session based on the current state.
    fun playPauseTracking() {
        if (currentRunState.value.isTracking) {
            trackingManager.pauseTracking()
        } else {
            trackingManager.startResumeTracking()
        }
    }

    //  Function finalizes the running session, saves the relevant data, and stops tracking.
    fun finishRun(averageHeartRate: Double) {
        trackingManager.pauseTracking()
        currentRunResult = CurrentRunResult(
            distanceInMeters = currentRunState.value.distanceInMeters,
            timeInMillis = runningDurationInMillis.value,
            avgHeartRate = if (!averageHeartRate.isNaN()) averageHeartRate else 0.0,
            speedInKMH = currentRunState.value.speedInKMH
        )
        saveRun(
            Run(
                avgSpeedInKMH = currentRunState.value.distanceInMeters
                    .toBigDecimal()
                    .multiply(3600.toBigDecimal())
                    .divide(runningDurationInMillis.value.toBigDecimal(), 2, RoundingMode.HALF_UP)
                    .toFloat(),
                distanceInMeters = currentRunState.value.distanceInMeters,
                durationInMillis = runningDurationInMillis.value,
                timestamp = Date().time,
                avgHeartRate = if (!averageHeartRate.isNaN()) averageHeartRate else 0.0
            )
        )
        trackingManager.stop()
    }

    // Save the run into database
    private fun saveRun(run: Run) {
        viewModelScope.launch {
            Log.d(tag, "Save run $run")
            roomDb.runDao.addRun(run)
        }
    }

    // Define ViewModel factory in a companion object
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return RunViewModel(
                    RunalyzeApp.appModule.trackingManager,
                    RunalyzeApp.appModule.runDb
                ) as T
            }
        }
    }
}
