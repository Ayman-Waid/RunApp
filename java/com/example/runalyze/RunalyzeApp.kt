package com.example.runalyze

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.runalyze.service.notification.NotificationHelper

// Sets up the core components of the application when it is created.
class RunalyzeApp : Application() {
    private val tag = "Running Track App"

    companion object {
        lateinit var appContext: Context
        lateinit var appModule: AppModule
        lateinit var notificationHelper: NotificationHelper
    }

    override fun onCreate() {
        super.onCreate()
        // create an instance of app module
        appModule = AppModuleImplement(this)
        // create a notification channel
        notificationHelper = appModule.notificationHelper
        notificationHelper.createNotificationChannel()
        Log.d(tag, "Running Track App onCreate()")
        appContext = applicationContext
    }
}