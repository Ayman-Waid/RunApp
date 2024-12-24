package com.example.runalyze.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.runalyze.MainActivity
import com.example.runalyze.R
import com.example.runalyze.service.location.TrackingService
import com.example.runalyze.service.notification.NotificationHelper.Companion.TRACKING_NOTIFICATION_ID
import com.example.runalyze.utils.Destination
import com.example.runalyze.utils.RunUtils

// An implementation of the NotificationHelper interface
class DefaultNotificationHelper(private val context: Context) : NotificationHelper {
    companion object {
        private const val TRACKING_NOTIFICATION_CHANNEL_ID = "tracking_notification"
        private const val TRACKING_NOTIFICATION_CHANNEL_NAME = "Runalyze Tracking Status"
    }

    // An intent opens the MainActivity when the notification is tapped.
    private val intentToRunScreen = TaskStackBuilder.create(context).run {
        addNextIntent(
            Intent(
                Intent.ACTION_VIEW,
                Destination.CurrentRun.currentRunUriPattern.toUri(),
                context,
                MainActivity::class.java
            )
        )
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)!!
    }

    // Provides a basic NotificationCompat.Builder with settings like the small icon, auto-cancel behavior,
    // ongoing status, content title, and initial content text. It also sets the content intent to the intentToRunScreen.
    override val baseNotificationBuilder
        get() = NotificationCompat.Builder(
            context,
            TRACKING_NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.runalyze_logo_large)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentTitle("Running Time")
            .setContentText("00:00:00")
            .setContentIntent(intentToRunScreen)

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    // updating the tracking notification. It takes the elapsed time (durationInMillis)
    // and a flag (isTracking) to determine whether tracking is active.
    override fun updateTrackingNotification(durationInMillis: Long, isTracking: Boolean) {
        val notification = baseNotificationBuilder
            .setContentText(RunUtils.getFormattedStopwatchTime(durationInMillis))
            .clearActions()
            .addAction(getTrackingNotificationAction(isTracking))
            .build()

        notificationManager.notify(TRACKING_NOTIFICATION_ID, notification)
    }

    //  generates a notification action based on whether tracking is active or not.
    //  It creates a "Pause" or "Resume" action button, along with the associated PendingIntent to perform the corresponding tracking action.
    private fun getTrackingNotificationAction(isTracking: Boolean): NotificationCompat.Action {
        return NotificationCompat.Action(
            if (isTracking) R.drawable.ic_pause else R.drawable.ic_play,
            if (isTracking) "Pause" else "Resume",
            PendingIntent.getService(
                context,
                2234,
                Intent(
                    context,
                    TrackingService::class.java
                ).apply {
                    action =
                        if (isTracking) TrackingService.ACTION_PAUSE_TRACKING else TrackingService.ACTION_RESUME_TRACKING
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    // Cancels the tracking notification, removing it from the notification tray.
    override fun removeTrackingNotification() {
        notificationManager.cancel(TRACKING_NOTIFICATION_ID)
    }

    // create the notification channel for tracking notifications.
    override fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return

        val notificationChannel = NotificationChannel(
            TRACKING_NOTIFICATION_CHANNEL_ID,
            TRACKING_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }
}


