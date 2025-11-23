package com.sleepy.services

import android.content.Context
import android.util.Log
import com.google.gson.JsonParser
import com.sleepy.data.database.PrayerTimesEntity
import com.sleepy.data.database.SleepyDatabase
import com.sleepy.data.models.PrayerTimes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PrayerTimesService(context: Context) {
    private val database = SleepyDatabase.getDatabase(context)
    private val prayerTimesDao = database.prayerTimesDao()

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // Configuration matching Python backend
    private val aladhanMethod = 3 // MWL (Muslim World League)
    private val aladhanSchool = 1 // Hanafi jurisprudence
    private val aladhanMidnightMode = 0 // Standard midnight calculation
    private val safetyBufferMinutes = 15 // 15-minute safety buffer
    private val cacheDays = 30 // Cache for 30 days

    suspend fun getPrayerTimes(
        city: String,
        country: String,
        latitude: Double,
        longitude: Double,
        date: String = getCurrentDate(),
        useCache: Boolean = true
    ): PrayerTimes? = withContext(Dispatchers.IO) {
        try {
            // Try cache first
            if (useCache) {
                val cached = prayerTimesDao.getPrayerTimes(city, date)
                if (cached != null) {
                    Log.d("PrayerTimesService", "Using cached prayer times for $city on $date")
                    return@withContext entityToModel(cached)
                }
            }

            // Fetch from Aladhan API
            Log.d("PrayerTimesService", "Fetching prayer times from Aladhan API for $city on $date")
            val prayerTimes = fetchFromAladhan(city, country, latitude, longitude, date)

            if (prayerTimes != null) {
                // Save to cache
                prayerTimesDao.insert(modelToEntity(prayerTimes))

                // Cleanup old cache
                cleanupOldCache()
            }

            prayerTimes
        } catch (e: Exception) {
            Log.e("PrayerTimesService", "Error getting prayer times", e)
            // Fallback to cache
            val cached = prayerTimesDao.getPrayerTimes(city, date)
            cached?.let { entityToModel(it) }
        }
    }

    private suspend fun fetchFromAladhan(
        city: String,
        country: String,
        latitude: Double,
        longitude: Double,
        date: String
    ): PrayerTimes? = withContext(Dispatchers.IO) {
        try {
            // Convert date from YYYY-MM-DD to DD-MM-YYYY for Aladhan
            val dateFormatInput = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dateFormatOutput = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val parsedDate = dateFormatInput.parse(date)
            val formattedDate = dateFormatOutput.format(parsedDate!!)

            val url = "https://api.aladhan.com/v1/timings/$formattedDate" +
                    "?latitude=$latitude" +
                    "&longitude=$longitude" +
                    "&method=$aladhanMethod" +
                    "&school=$aladhanSchool" +
                    "&midnightMode=$aladhanMidnightMode"

            Log.d("PrayerTimesService", "Aladhan URL: $url")

            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e("PrayerTimesService", "Aladhan API error: ${response.code}")
                return@withContext null
            }

            val responseBody = response.body?.string()
            if (responseBody == null) {
                Log.e("PrayerTimesService", "Empty response from Aladhan API")
                return@withContext null
            }

            val jsonObject = JsonParser.parseString(responseBody).asJsonObject

            if (jsonObject.get("code")?.asInt != 200) {
                Log.e("PrayerTimesService", "Aladhan API returned non-200 code")
                return@withContext null
            }

            val data = jsonObject.getAsJsonObject("data")
            val timings = data.getAsJsonObject("timings")

            // Extract times and add safety buffer
            PrayerTimes(
                date = date,
                fajr = addTimeBuffer(timings.get("Fajr").asString.split(" ")[0]),
                sunrise = addTimeBuffer(timings.get("Sunrise").asString.split(" ")[0]),
                dhuhr = addTimeBuffer(timings.get("Dhuhr").asString.split(" ")[0]),
                asr = addTimeBuffer(timings.get("Asr").asString.split(" ")[0]),
                maghrib = addTimeBuffer(timings.get("Maghrib").asString.split(" ")[0]),
                isha = addTimeBuffer(timings.get("Isha").asString.split(" ")[0]),
                city = city,
                country = country,
                latitude = latitude,
                longitude = longitude
            )
        } catch (e: Exception) {
            Log.e("PrayerTimesService", "Error fetching from Aladhan", e)
            null
        }
    }

    private fun addTimeBuffer(timeStr: String): String {
        return try {
            val parts = timeStr.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                add(Calendar.MINUTE, safetyBufferMinutes)
            }

            String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        } catch (e: Exception) {
            Log.e("PrayerTimesService", "Error adding time buffer", e)
            timeStr
        }
    }

    private suspend fun cleanupOldCache() = withContext(Dispatchers.IO) {
        try {
            val cutoffTimestamp = System.currentTimeMillis() - (cacheDays * 24 * 60 * 60 * 1000L)
            prayerTimesDao.deleteOldCache(cutoffTimestamp)
        } catch (e: Exception) {
            Log.e("PrayerTimesService", "Error cleaning up cache", e)
        }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private fun entityToModel(entity: PrayerTimesEntity): PrayerTimes {
        return PrayerTimes(
            date = entity.date,
            fajr = entity.fajr,
            sunrise = entity.sunrise,
            dhuhr = entity.dhuhr,
            asr = entity.asr,
            maghrib = entity.maghrib,
            isha = entity.isha,
            city = entity.city,
            country = entity.country,
            latitude = entity.latitude,
            longitude = entity.longitude
        )
    }

    private fun modelToEntity(model: PrayerTimes): PrayerTimesEntity {
        return PrayerTimesEntity(
            date = model.date,
            city = model.city,
            country = model.country,
            latitude = model.latitude,
            longitude = model.longitude,
            fajr = model.fajr,
            sunrise = model.sunrise,
            dhuhr = model.dhuhr,
            asr = model.asr,
            maghrib = model.maghrib,
            isha = model.isha
        )
    }
}
