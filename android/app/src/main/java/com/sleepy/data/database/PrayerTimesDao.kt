package com.sleepy.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PrayerTimesDao {
    @Query("SELECT * FROM prayer_times WHERE city = :city AND date = :date LIMIT 1")
    suspend fun getPrayerTimes(city: String, date: String): PrayerTimesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prayerTimes: PrayerTimesEntity)

    @Query("DELETE FROM prayer_times WHERE cachedAt < :cutoffTimestamp")
    suspend fun deleteOldCache(cutoffTimestamp: Long)

    @Query("SELECT COUNT(*) FROM prayer_times")
    suspend fun getCount(): Int
}
