package com.example.runalyze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.runalyze.database.AppDb
import com.example.runalyze.database.Goal
import com.example.runalyze.database.Run

// View model for Statistics tab
class ActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDb.getInstance(application)
    val allTrainings: LiveData<List<Run>> = db.runDao.getTrainingDetails()

    // Method to get the latest goal (based on createdAt time stamp) from the database
    fun getNewestGoal(): LiveData<Goal?> = db.goalDao.getLatestGoal()
}



