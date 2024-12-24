package com.example.runalyze.service.location

import android.content.Context
import android.content.Intent
import android.os.Build

// Provides methods to start and stop a tracking service
interface TrackingServiceManager {
    fun startService()
    fun stopService()
}

class DefaultTrackingServiceManager(private val context: Context) : TrackingServiceManager {
    override fun startService() {
        Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_START_SERVICE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(this)
            } else context.startService(this)
        }
    }

    override fun stopService() {
        Intent(context, TrackingService::class.java).apply {
            context.stopService(this)
        }
    }
}