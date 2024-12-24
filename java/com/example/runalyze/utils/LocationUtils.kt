package com.example.runalyze.utils

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient

//  Object provides utility functions related to location and location settings.
object LocationUtils {
    private const val LOCATION_UPDATE_INTERVAL = 5000L

    val locationRequestBuilder
        get() = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        )

    private const val LOCATION_ENABLE_REQUEST_CODE = 5234

    fun checkAndRequestLocationSetting(activity: Context) {
        val locationRequest = locationRequestBuilder.build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(activity)

        client.checkLocationSettings(builder.build())
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(
                            activity as Activity,
                            LOCATION_ENABLE_REQUEST_CODE
                        )
                    } catch (_: IntentSender.SendIntentException) {
                    }
                }
            }
    }
}