package com.sleepy.services

import android.util.Log
import com.sleepy.data.models.PrayerTimes
import com.sleepy.data.models.SleepSchedule
import java.text.SimpleDateFormat
import java.util.*

class SleepOptimizer {
    // Configuration matching Python backend
    private val defaultDuration = 7.0
    private val minDuration = 6.0
    private val maxDuration = 7.5
    private val optimalWakeHour = 4 // 4 AM pivot
    private val ishaBufferMinutes = 30 // 30 minutes after Isha
    private val fajrBufferMinutes = 0 // Wake at Fajr or earlier

    fun calculateOptimalSchedule(prayerTimes: PrayerTimes): SleepSchedule {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.US)

            val targetDate = dateFormat.parse(prayerTimes.date) ?: Date()
            val calendar = Calendar.getInstance()

            // Parse Isha time
            val ishaParts = prayerTimes.isha.split(":")
            calendar.time = targetDate
            calendar.set(Calendar.HOUR_OF_DAY, ishaParts[0].toInt())
            calendar.set(Calendar.MINUTE, ishaParts[1].toInt())
            calendar.set(Calendar.SECOND, 0)

            // Calculate sleep start (Isha + buffer)
            calendar.add(Calendar.MINUTE, ishaBufferMinutes)
            val sleepStartTime = timeFormat.format(calendar.time)
            val sleepStartCal = calendar.clone() as Calendar

            // Parse Fajr time (next day)
            val fajrParts = prayerTimes.fajr.split(":")
            calendar.time = targetDate
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, fajrParts[0].toInt())
            calendar.set(Calendar.MINUTE, fajrParts[1].toInt())
            calendar.set(Calendar.SECOND, 0)
            val fajrCal = calendar.clone() as Calendar

            // Calculate available sleep hours
            val availableHours = (fajrCal.timeInMillis - sleepStartCal.timeInMillis) / (1000.0 * 60 * 60)

            // Determine optimal wake time
            val notes: String
            val wakeCal: Calendar

            if (availableHours >= 7.5) {
                // Plenty of time
                if (fajrParts[0].toInt() > optimalWakeHour) {
                    // Fajr is after 4 AM, wake at 4 AM
                    wakeCal = sleepStartCal.clone() as Calendar
                    wakeCal.add(Calendar.DAY_OF_MONTH, 1)
                    wakeCal.set(Calendar.HOUR_OF_DAY, optimalWakeHour)
                    wakeCal.set(Calendar.MINUTE, 0)
                    notes = "Wake early at $optimalWakeHour:00 AM for maximum productivity before Fajr"
                } else {
                    // Fajr is before 4 AM, wake at Fajr
                    wakeCal = fajrCal.clone() as Calendar
                    wakeCal.add(Calendar.MINUTE, -fajrBufferMinutes)
                    notes = "Wake at Fajr time"
                }
            } else if (availableHours >= minDuration) {
                // Use all available time up to Fajr
                wakeCal = fajrCal.clone() as Calendar
                wakeCal.add(Calendar.MINUTE, -fajrBufferMinutes)
                notes = "Wake at Fajr time"
            } else {
                // Not enough sleep time
                wakeCal = fajrCal.clone() as Calendar
                wakeCal.add(Calendar.MINUTE, -fajrBufferMinutes)
                notes = "Warning: Less than minimum sleep duration available"
            }

            val sleepEndTime = timeFormat.format(wakeCal.time)

            // Calculate actual duration
            var durationHours = (wakeCal.timeInMillis - sleepStartCal.timeInMillis) / (1000.0 * 60 * 60)

            // Round to 2 decimal places
            durationHours = Math.round(durationHours * 100.0) / 100.0

            return SleepSchedule(
                date = prayerTimes.date,
                sleepStart = sleepStartTime,
                sleepEnd = sleepEndTime,
                durationHours = durationHours,
                ishaTime = prayerTimes.isha,
                fajrTime = prayerTimes.fajr,
                notes = notes
            )
        } catch (e: Exception) {
            Log.e("SleepOptimizer", "Error calculating sleep schedule", e)
            // Return a fallback schedule
            return SleepSchedule(
                date = prayerTimes.date,
                sleepStart = "22:00",
                sleepEnd = "05:00",
                durationHours = 7.0,
                ishaTime = prayerTimes.isha,
                fajrTime = prayerTimes.fajr,
                notes = "Error calculating optimal schedule - using defaults"
            )
        }
    }

    fun getTimeUntilSleep(sleepSchedule: SleepSchedule): String? {
        try {
            val now = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.US)

            val scheduleDate = dateFormat.parse(sleepSchedule.date) ?: return null
            val sleepTime = timeFormat.parse(sleepSchedule.sleepStart) ?: return null

            val sleepCal = Calendar.getInstance().apply {
                time = scheduleDate
                set(Calendar.HOUR_OF_DAY, sleepTime.hours)
                set(Calendar.MINUTE, sleepTime.minutes)
                set(Calendar.SECOND, 0)
            }

            // If sleep time is in the past, it might be for tomorrow
            if (sleepCal.before(now)) {
                sleepCal.add(Calendar.DAY_OF_MONTH, 1)
            }

            val diffMillis = sleepCal.timeInMillis - now.timeInMillis

            if (diffMillis < 0) {
                return null
            }

            val hours = (diffMillis / (1000 * 60 * 60)).toInt()
            val minutes = ((diffMillis % (1000 * 60 * 60)) / (1000 * 60)).toInt()

            return if (hours > 0) {
                "$hours hour${if (hours != 1) "s" else ""} $minutes minute${if (minutes != 1) "s" else ""}"
            } else {
                "$minutes minute${if (minutes != 1) "s" else ""}"
            }
        } catch (e: Exception) {
            Log.e("SleepOptimizer", "Error calculating time until sleep", e)
            return null
        }
    }
}
