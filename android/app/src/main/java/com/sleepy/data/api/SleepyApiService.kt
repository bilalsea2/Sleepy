package com.sleepy.data.api

import com.sleepy.data.models.FullScheduleResponse
import com.sleepy.data.models.QuoteResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit API interface for Sleepy backend
 */
interface SleepyApiService {

    @POST("sleep-schedule/full")
    suspend fun getFullSleepSchedule(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("create_calendar_event") createCalendarEvent: Boolean = false
    ): FullScheduleResponse

    @GET("quotes/random")
    suspend fun getRandomQuote(): QuoteResponse

    @GET("quotes/supportive")
    suspend fun getSupportiveQuote(): QuoteResponse

    @GET("quotes/urgent")
    suspend fun getUrgentQuote(): QuoteResponse

    @GET("/")
    suspend fun healthCheck(): Map<String, Any>
}
