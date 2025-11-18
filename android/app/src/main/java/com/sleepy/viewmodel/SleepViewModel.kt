package com.sleepy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleepy.data.api.RetrofitClient
import com.sleepy.data.models.FullScheduleResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing sleep schedule state
 */
class SleepViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    private val _scheduleState = MutableStateFlow<ScheduleState>(ScheduleState.Loading)
    val scheduleState: StateFlow<ScheduleState> = _scheduleState.asStateFlow()

    /**
     * Fetch sleep schedule from backend
     */
    fun fetchSleepSchedule(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _scheduleState.value = ScheduleState.Loading

            try {
                val response = apiService.getFullSleepSchedule(
                    latitude = latitude,
                    longitude = longitude,
                    createCalendarEvent = false
                )
                _scheduleState.value = ScheduleState.Success(response)
            } catch (e: Exception) {
                _scheduleState.value = ScheduleState.Error(
                    e.message ?: "Failed to fetch sleep schedule"
                )
            }
        }
    }

    /**
     * Test API connection
     */
    fun testConnection() {
        viewModelScope.launch {
            try {
                val response = apiService.healthCheck()
                println("API Health Check: $response")
            } catch (e: Exception) {
                println("API Connection Failed: ${e.message}")
            }
        }
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
