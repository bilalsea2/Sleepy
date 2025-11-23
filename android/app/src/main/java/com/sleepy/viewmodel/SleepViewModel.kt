package com.sleepy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sleepy.data.models.FullScheduleResponse
import com.sleepy.data.models.LocationInfo
import com.sleepy.services.PrayerTimesService
import com.sleepy.services.SleepOptimizer
import com.sleepy.utils.SleepQuotes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing sleep schedule state
 * Now uses native Kotlin services - no backend server needed!
 */
class SleepViewModel(application: Application) : AndroidViewModel(application) {

    private val prayerTimesService = PrayerTimesService(application)
    private val sleepOptimizer = SleepOptimizer()

    private val _scheduleState = MutableStateFlow<ScheduleState>(ScheduleState.Loading)
    val scheduleState: StateFlow<ScheduleState> = _scheduleState.asStateFlow()

    /**
     * Fetch sleep schedule using native Kotlin services
     */
    fun fetchSleepSchedule(
        city: String,
        country: String,
        latitude: Double,
        longitude: Double,
        timezone: String = "Asia/Tashkent"
    ) {
        viewModelScope.launch {
            _scheduleState.value = ScheduleState.Loading

            try {
                // Determine if in Uzbekistan (simple check based on country)
                val isUzbekistan = country.equals("Uzbekistan", ignoreCase = true) ||
                        country.equals("UZ", ignoreCase = true)

                // Create location info
                val locationInfo = LocationInfo(
                    city = city,
                    country = country,
                    latitude = latitude,
                    longitude = longitude,
                    timezone = timezone,
                    isUzbekistan = isUzbekistan
                )

                // Fetch prayer times from Aladhan API (or cache)
                val prayerTimes = prayerTimesService.getPrayerTimes(
                    city = city,
                    country = country,
                    latitude = latitude,
                    longitude = longitude
                )

                if (prayerTimes == null) {
                    _scheduleState.value = ScheduleState.Error(
                        "Unable to fetch prayer times. Please check your internet connection."
                    )
                    return@launch
                }

                // Calculate optimal sleep schedule (pure Kotlin, no network needed)
                val sleepSchedule = sleepOptimizer.calculateOptimalSchedule(prayerTimes)

                // Get time until sleep
                val timeUntilSleep = sleepOptimizer.getTimeUntilSleep(sleepSchedule)

                // Get random quote
                val quote = SleepQuotes.getRandomQuote()

                // Build full response
                val response = FullScheduleResponse(
                    location = locationInfo,
                    prayerTimes = prayerTimes,
                    sleepSchedule = sleepSchedule,
                    timeUntilSleep = timeUntilSleep,
                    notificationQuote = quote
                )

                _scheduleState.value = ScheduleState.Success(response)
            } catch (e: Exception) {
                _scheduleState.value = ScheduleState.Error(
                    e.message ?: "Failed to generate sleep schedule"
                )
            }
        }
    }

    /**
     * Fetch with just coordinates (for initial GPS-based lookup)
     */
    fun fetchSleepSchedule(latitude: Double, longitude: Double) {
        // Default to Tashkent for now - in production, you'd use reverse geocoding
        fetchSleepSchedule(
            city = "Tashkent",
            country = "Uzbekistan",
            latitude = latitude,
            longitude = longitude
        )
    }
}

/**
 * UI State sealed class
 */
sealed class ScheduleState {
    object Loading : ScheduleState()
    data class Success(val data: FullScheduleResponse) : ScheduleState()
    data class Error(val message: String) : ScheduleState()
}
