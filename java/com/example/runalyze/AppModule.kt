package com.example.runalyze

import android.content.Context
import com.example.runalyze.database.AppDb
import com.example.runalyze.service.TimeTracker
import com.example.runalyze.service.location.DefaultLocationTrackingManager
import com.example.runalyze.service.location.DefaultTrackingServiceManager
import com.example.runalyze.service.location.LocationTrackingManager
import com.example.runalyze.service.location.TrackingManager
import com.example.runalyze.service.notification.DefaultNotificationHelper
import com.example.runalyze.service.notification.NotificationHelper
import com.example.runalyze.utils.LocationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

// dependencies container
interface AppModule {
    val fusedLocationProviderClient: FusedLocationProviderClient
    val defaultLocationTrackingManager: LocationTrackingManager
    val trackingServiceManager: DefaultTrackingServiceManager
    val runDb: AppDb
    val trackingManager: TrackingManager
    val notificationHelper: NotificationHelper
}

// Implementation
class AppModuleImplement(
    private val appContext: Context
) : AppModule {

    override val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(appContext)
    }

    override val defaultLocationTrackingManager: DefaultLocationTrackingManager by lazy {
        DefaultLocationTrackingManager(
            fusedLocationProviderClient = fusedLocationProviderClient,
            context = appContext,
            locationRequest = LocationUtils.locationRequestBuilder.build()
        )
    }

    override val trackingServiceManager: DefaultTrackingServiceManager by lazy {
        DefaultTrackingServiceManager(appContext)
    }
    override val runDb: AppDb by lazy {
        AppDb.getInstance(appContext)
    }
    override val trackingManager: TrackingManager by lazy {
        TrackingManager(
            locationTrackingManager = defaultLocationTrackingManager,
            timeTracker = TimeTracker(
                applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
                defaultDispatcher = Dispatchers.Default
            ),
            trackingServiceManager = trackingServiceManager
        )
    }
    override val notificationHelper: NotificationHelper by lazy {
        DefaultNotificationHelper(appContext)
    }

}