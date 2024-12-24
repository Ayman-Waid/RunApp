package com.example.runalyze.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Goal::class, Run::class],
    version = 11,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract val goalDao: GoalDao
    abstract val runDao: RunDao

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null

        fun getInstance(context: Context): AppDb {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        AppDb::class.java,
                        "app_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}