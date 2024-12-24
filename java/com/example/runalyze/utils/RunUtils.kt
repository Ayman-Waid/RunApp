package com.example.runalyze.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.runalyze.service.location.models.PathPoint
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

// object contains a variety of utility functions for managing permissions,
// formatting time, and other tasks related to running and tracking
object RunUtils {
    val locationPermissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).toTypedArray()

    @RequiresApi(Build.VERSION_CODES.S)
    val bluetoothPermission = Manifest.permission.BLUETOOTH

    @RequiresApi(Build.VERSION_CODES.S)
    val notificationPermission = Manifest.permission.POST_NOTIFICATIONS

    val allPermissions = mutableListOf<String>().apply {
        addAll(locationPermissions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(notificationPermission)
            add(bluetoothPermission)
        }
    }.toTypedArray()

    fun Context.hasNotificationPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                notificationPermission,
            ) == PackageManager.PERMISSION_GRANTED
        } else false

    @RequiresApi(Build.VERSION_CODES.S)
    fun Context.hasBluetoothPermission() =
        ContextCompat.checkSelfPermission(
            this,
            bluetoothPermission,
        ) == PackageManager.PERMISSION_GRANTED

    fun Context.hasLocationPermission() =
        locationPermissions.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    fun Context.hasAllPermission() =
        allPermissions.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    fun Context.openAppSetting() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).also(::startActivity)
    }

    // Function to format time in hours, minutes, and seconds
    fun getFormattedStopwatchTime(ms: Long, includeMillis: Boolean = false): String {
        var milliseconds = ms
        val hrs = TimeUnit.MILLISECONDS.toHours(ms)
        milliseconds -= TimeUnit.HOURS.toMillis(hrs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        val formattedString =
            "${if (hrs < 10) "0" else ""}$hrs:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"

        return if (!includeMillis) {
            formattedString
        } else {
            milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
            milliseconds /= 10
            formattedString + ":" +
                    "${if (milliseconds < 10) "0" else ""}$milliseconds"
        }
    }

    // Calculates the distance between two location points.
    fun getDistanceBetweenPathPoints(
        pathPoint1: PathPoint,
        pathPoint2: PathPoint
    ): Int {
        return if (pathPoint1 is PathPoint.LocationPoint && pathPoint2 is PathPoint.LocationPoint) {
            val result = FloatArray(1)
            Location.distanceBetween(
                pathPoint1.latLng.latitude,
                pathPoint1.latLng.longitude,
                pathPoint2.latLng.latitude,
                pathPoint2.latLng.longitude,
                result
            )
            result[0].roundToInt()
        } else 0
    }

    // Function to find the last point in a list of location points.
    fun List<PathPoint>.lastLocationPoint(): PathPoint.LocationPoint? {
        for (i in lastIndex downTo 0)
            if (get(i) is PathPoint.LocationPoint)
                return get(i) as PathPoint.LocationPoint
        return null
    }

   // Function to find the first point in a list of location points.
    fun List<PathPoint>.firstLocationPoint() =
        find { it is PathPoint.LocationPoint } as? PathPoint.LocationPoint


    // Convert time from milliseconds to hours.
    fun getRunTimeInHours(time: Long): Double {
        val millisecondsInMinute = 60000.0
        return time / millisecondsInMinute
    }

}
