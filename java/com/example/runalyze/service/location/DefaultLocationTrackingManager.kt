package com.example.runalyze.service.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.example.runalyze.utils.RunUtils.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest

// Managing location tracking callbacks.
interface LocationTrackingManager {
    // Register a callback for receiving location updates.
    fun registerCallback(locationCallback: LocationCallback)

    // Unregister a previously registered location tracking callback.
    fun unRegisterCallback(locationCallback: LocationCallback)
}

@SuppressLint("MissingPermission")
// Default implementation of the LocationTrackingManager interface.
class DefaultLocationTrackingManager(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context,
    private val locationRequest: LocationRequest
) : LocationTrackingManager {

    // Register a callback for receiving location updates.
    override fun registerCallback(locationCallback: LocationCallback) {
        // Check if the app has permission to access location.
        if (context.hasLocationPermission()) {
            // Request location updates if permission is granted.
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    // Unregister a previously registered location tracking callback.
    override fun unRegisterCallback(locationCallback: LocationCallback) {
        // Remove location updates.
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}
