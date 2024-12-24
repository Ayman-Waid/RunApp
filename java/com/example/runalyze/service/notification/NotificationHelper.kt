package com.example.runalyze.service.notification

import androidx.core.app.NotificationCompat

// An abstraction layer for working with notifications
interface NotificationHelper {
    val baseNotificationBuilder: NotificationCompat.Builder

    fun createNotificationChannel()
    fun updateTrackingNotification(durationInMillis: Long, isTracking: Boolean)
    fun removeTrackingNotification()

    companion object {
        const val TRACKING_NOTIFICATION_ID = 3
    }
}