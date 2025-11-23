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

    // Configuration
    private val aladhanMethod = 3 // MWL (Muslim World League)
    private val aladhanSchool = 1 // Hanafi jurisprudence
    private val aladhanMidnightMode = 0 // Standard midnight calculation
    private val cacheDays = 30 // Cache for 30 days

    // Safety buffers (minutes)
    private val safetyBufferMinutes = 10  // +10 minutes to all prayer times except sunrise
    private val sunriseBufferMinutes = -10 // -10 minutes to sunrise

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

            // Choose API based on location
            Log.d("PrayerTimesService", "Fetching prayer times for $city on $date")
            val prayerTimes = if (city.equals("Tashkent", ignoreCase = true) ||
                                   city.equals("Toshkent", ignoreCase = true)) {
                // Use namoz-vaqti.uz for Tashkent
                fetchFromNamozVaqti(date) ?: fetchFromAladhan(city, country, latitude, longitude, date)
            } else {
                // Use Aladhan for other locations
                fetchFromAladhan(city, country, latitude, longitude, date)
            }

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
                fajr = addTimeBuffer(timings.get("Fajr").asString.split(" ")[0], safetyBufferMinutes),
                sunrise = addTimeBuffer(timings.get("Sunrise").asString.split(" ")[0], sunriseBufferMinutes),
                dhuhr = addTimeBuffer(timings.get("Dhuhr").asString.split(" ")[0], safetyBufferMinutes),
                asr = addTimeBuffer(timings.get("Asr").asString.split(" ")[0], safetyBufferMinutes),
                maghrib = addTimeBuffer(timings.get("Maghrib").asString.split(" ")[0], safetyBufferMinutes),
                isha = addTimeBuffer(timings.get("Isha").asString.split(" ")[0], safetyBufferMinutes),
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

    private suspend fun fetchFromNamozVaqti(date: String): PrayerTimes? = withContext(Dispatchers.IO) {
        try {
            // namoz-vaqti.uz API for Tashkent
            val url = "https://namoz-vaqti.uz/?format=json&lang=lotin&period=week&region=toshkent-shahri"

            Log.d("PrayerTimesService", "Namoz-vaqti.uz URL: $url")

            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e("PrayerTimesService", "Namoz-vaqti.uz API error: ${response.code}")
                return@withContext null
            }

            val responseBody = response.body?.string()
            if (responseBody == null) {
                Log.e("PrayerTimesService", "Empty response from namoz-vaqti.uz")
                return@withContext null
            }

            val jsonObject = JsonParser.parseString(responseBody).asJsonObject

            // Find today's times
            val today = jsonObject.getAsJsonObject("today")
            val times = today.getAsJsonObject("times")

            // Extract prayer times with buffers
            // Mapping: bomdod=Fajr, quyosh=Sunrise, peshin=Dhuhr, asr=Asr, shom=Maghrib, xufton=Isha
            PrayerTimes(
                date = date,
                fajr = addTimeBuffer(times.get("bomdod").asString, safetyBufferMinutes),
                sunrise = addTimeBuffer(times.get("quyosh").asString, sunriseBufferMinutes),
                dhuhr = addTimeBuffer(times.get("peshin").asString, safetyBufferMinutes),
                asr = addTimeBuffer(times.get("asr").asString, safetyBufferMinutes),
                maghrib = addTimeBuffer(times.get("shom").asString, safetyBufferMinutes),
                isha = addTimeBuffer(times.get("xufton").asString, safetyBufferMinutes),
                city = "Tashkent",
                country = "Uzbekistan",
                latitude = 41.2995,
                longitude = 69.2401
            )
        } catch (e: Exception) {
            Log.e("PrayerTimesService", "Error fetching from namoz-vaqti.uz", e)
            null
        }
    }

    private fun addTimeBuffer(timeStr: String, bufferMinutes: Int = safetyBufferMinutes): String {
        return try {
            val parts = timeStr.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                add(Calendar.MINUTE, bufferMinutes)
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
