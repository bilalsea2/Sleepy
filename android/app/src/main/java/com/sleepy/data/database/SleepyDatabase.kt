package com.sleepy.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PrayerTimesEntity::class], version = 1, exportSchema = false)
abstract class SleepyDatabase : RoomDatabase() {
    abstract fun prayerTimesDao(): PrayerTimesDao

    companion object {
        @Volatile
        private var INSTANCE: SleepyDatabase? = null

        fun getDatabase(context: Context): SleepyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SleepyDatabase::class.java,
                    "sleepy_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
