package com.sleepy.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data models matching the Python FastAPI backend
 */

data class LocationInfo(
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    @SerializedName("is_uzbekistan")
    val isUzbekistan: Boolean
)

data class PrayerTimes(
    val date: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)

data class SleepSchedule(
    val date: String,
    @SerializedName("sleep_start")
    val sleepStart: String,
    @SerializedName("sleep_end")
    val sleepEnd: String,
    @SerializedName("duration_hours")
    val durationHours: Double,
    @SerializedName("isha_time")
    val ishaTime: String,
    @SerializedName("fajr_time")
    val fajrTime: String,
    val notes: String?
)

data class FullScheduleResponse(
    val location: LocationInfo,
    @SerializedName("prayer_times")
    val prayerTimes: PrayerTimes,
    @SerializedName("sleep_schedule")
    val sleepSchedule: SleepSchedule,
    @SerializedName("time_until_sleep")
    val timeUntilSleep: String?,
    @SerializedName("notification_quote")
    val notificationQuote: String
)

data class QuoteResponse(
    val quote: String
)
