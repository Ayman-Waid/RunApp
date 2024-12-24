package com.example.runalyze.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverters

// Goal entity: model how a goal set by user should be save to the database
@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val goalId: Long,
    @TypeConverters(Converters::class)
    val startDate: Long?,
    @TypeConverters(Converters::class)
    val endDate: Long?,
    val repeatedDays: String,
    val reminderTime: String?,
    val targetDistanceInKm: Double,
    val targetSpeedInKmh: Double,
    val targetHeartRateInBpm: Int,
    @TypeConverters(Converters::class)
    val createdDate: Long
)

// Run entity: model how a running session should be save to the database
@Entity(tableName = "run_table")
data class Run(
    @PrimaryKey(autoGenerate = true)
    val runId: Int = 0,
    @TypeConverters(Converters::class)
    val timestamp: Long = 0L,
    val avgSpeedInKMH: Float = 0f,
    val distanceInMeters: Int = 0,
    val durationInMillis: Long = 0L,
    val avgHeartRate: Double = 0.0
)

@Dao
interface GoalDao {
    // Insert a goal to database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGoal(goal: Goal): Long

    // get the newest created goal from the database
    @Query("SELECT * FROM Goal ORDER BY createdDate DESC LIMIT 1")
    fun getLatestGoal(): LiveData<Goal?>
}

@Dao
interface RunDao {
    // get all the running sessions in the database
    @Query("SELECT * FROM run_table")
    fun getTrainingDetails(): LiveData<List<Run>>

    // insert the running session to the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRun(run: Run)

    // delete a running session
    @Delete
    suspend fun deleteRun(run: Run)

    // Get all the running sessions and sort them in the descending order
    @Query("SELECT * FROM run_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<Run>>
}