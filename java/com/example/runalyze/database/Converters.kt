package com.example.runalyze.database

import androidx.room.TypeConverter
import java.sql.Date

// To convert Long value to Date and vice versa
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}