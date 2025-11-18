# Android App Development Guide

This guide outlines the steps to build the native Android app for Sleepy.

## Overview

The Android app will:
- Communicate with the Python FastAPI backend (running locally or on a server)
- Use GPS to detect location
- Display prayer times and optimal sleep schedule
- Show a home screen widget with sleep information
- Send notifications at sleep time with creative quotes

## Architecture

```
Android App (Kotlin)
    ↓ HTTP/REST
Python FastAPI Backend (localhost:8000 or deployed)
    ↓
Prayer Times APIs + Google Calendar + SQLite Cache
```

## Development Steps

### 1. Setup Android Project

1. **Create new Android project in Android Studio**
   - Language: Kotlin
   - Minimum SDK: API 26 (Android 8.0) or higher
   - Build configuration: Jetpack Compose (modern UI)

2. **Add dependencies** (in `build.gradle.kts`):
   ```kotlin
   dependencies {
       // Networking
       implementation("com.squareup.retrofit2:retrofit:2.9.0")
       implementation("com.squareup.retrofit2:converter-gson:2.9.0")
       implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

       // Coroutines
       implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

       // ViewModel & LiveData
       implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
       implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

       // Location services
       implementation("com.google.android.gms:play-services-location:21.0.1")

       // WorkManager for notifications
       implementation("androidx.work:work-runtime-ktx:2.8.1")

       // Compose
       implementation("androidx.compose.ui:ui:1.5.4")
       implementation("androidx.compose.material3:material3:1.1.2")
       implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
   }
   ```

### 2. API Client

Create a Retrofit interface to communicate with the backend:

```kotlin
// ApiService.kt
interface SleepyApiService {
    @GET("location/gps")
    suspend fun getLocationFromGPS(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): LocationInfo

    @POST("sleep-schedule/full")
    suspend fun getFullSleepSchedule(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("create_calendar_event") createCalendarEvent: Boolean = false
    ): FullScheduleResponse

    @GET("quotes/random")
    suspend fun getRandomQuote(): QuoteResponse
}

// Data models
data class LocationInfo(
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val is_uzbekistan: Boolean
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
    val sleep_start: String,
    val sleep_end: String,
    val duration_hours: Double,
    val isha_time: String,
    val fajr_time: String,
    val notes: String?
)

data class FullScheduleResponse(
    val location: LocationInfo,
    val prayer_times: PrayerTimes,
    val sleep_schedule: SleepSchedule,
    val time_until_sleep: String?,
    val notification_quote: String
)

data class QuoteResponse(val quote: String)
```

### 3. GPS Location Service

```kotlin
// LocationManager.kt
class LocationManager(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Location? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        return suspendCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
}
```

### 4. Main Screen UI

```kotlin
// MainActivity.kt
@Composable
fun SleepScheduleScreen(viewModel: SleepViewModel) {
    val scheduleState by viewModel.scheduleState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Sleepy",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (scheduleState) {
            is ScheduleState.Loading -> {
                CircularProgressIndicator()
            }
            is ScheduleState.Success -> {
                val data = (scheduleState as ScheduleState.Success).data

                // Location & Prayer Times
                Card(modifier = Modifier.padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Location: ${data.location.city}, ${data.location.country}")
                        Text("Fajr: ${data.prayer_times.fajr}")
                        Text("Isha: ${data.prayer_times.isha}")
                    }
                }

                // Sleep Schedule
                Card(modifier = Modifier.padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Sleep Time: ${data.sleep_schedule.sleep_start}")
                        Text("Wake Time: ${data.sleep_schedule.sleep_end}")
                        Text("Duration: ${data.sleep_schedule.duration_hours} hours")
                        data.time_until_sleep?.let {
                            Text("Time until sleep: $it")
                        }
                    }
                }

                // Quote
                Card {
                    Text(
                        text = data.notification_quote,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            is ScheduleState.Error -> {
                Text("Error: ${(scheduleState as ScheduleState.Error).message}")
            }
        }
    }
}
```

### 5. Home Screen Widget

```kotlin
// SleepWidget.kt
class SleepWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            SleepWidgetContent()
        }
    }
}

@Composable
fun SleepWidgetContent() {
    // Fetch data from backend or local cache
    val schedule = /* load from cache or API */

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text("Sleep Time")
        Text(schedule.sleep_start, style = TextStyle(fontSize = 24.sp))

        Text("Prayer Times", modifier = GlanceModifier.padding(top = 8.dp))
        Text("Fajr: ${schedule.fajr}")
        Text("Isha: ${schedule.isha}")

        Text("Duration: ${schedule.duration_hours} hrs")
    }
}
```

### 6. Notification System

```kotlin
// NotificationWorker.kt
class SleepNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Get today's schedule
        val schedule = fetchSleepSchedule()

        // Get random quote
        val quote = fetchRandomQuote()

        // Show notification
        showNotification(schedule.sleep_start, quote)

        return Result.success()
    }

    private fun showNotification(sleepTime: String, quote: String) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sleep)
            .setContentTitle("Time to Sleep! ($sleepTime)")
            .setContentText(quote)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID, notification)
    }
}
```

### 7. Backend Configuration

**Option 1: Local Backend (Development)**
- Run Python backend on your computer: `python main.py`
- Connect Android emulator or phone to same WiFi
- Use computer's local IP (e.g., `http://192.168.1.100:8000`)

**Option 2: Deployed Backend (Production)**
- Deploy Python backend to a cloud service:
  - Heroku
  - Google Cloud Run
  - AWS EC2
  - DigitalOcean
- Update API base URL in Android app

### 8. Permissions

Add to `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## Testing

1. **Test API connection**:
   - Run backend: `python main.py`
   - Test endpoint: `http://localhost:8000/docs`

2. **Test Android app**:
   - Connect to backend via local network
   - Test GPS location detection
   - Verify prayer times display correctly
   - Test sleep schedule calculation
   - Verify notifications work

## Next Steps

1. Implement offline caching in Android (Room database)
2. Add settings screen for customization
3. Implement widget auto-refresh
4. Add dark mode support
5. Publish to Google Play Store

## UI Design System - Studio Ghibli Aesthetic

### Design Philosophy

The Sleepy app features a unique, beautiful UI inspired by:
- **Studio Ghibli films**: Warm, dreamy color palettes and organic aesthetics
- **Solarized color scheme**: Eye-comfortable, carefully balanced contrasts
- **Canvas/painting texture**: Subtle paper grain and watercolor-like backgrounds
- **Twitter/X layout**: Full-screen, text-first information density

### Theme Implementation

The app includes **two gorgeous themes** already implemented:

#### Light Theme - "Ghibli Meadow"
- Base: Warm cream background (#FDF6E3) like aged parchment
- Accents: Soft sage greens, gentle sky blues, warm peach tones
- Feel: Sunny afternoon in a Ghibli meadow

#### Dark Theme - "Ghibli Twilight"
- Base: Deep forest blue-green (#1A1F1E) like moonlit woods
- Accents: Moonlit greens, twilight blues, warm amber glows
- Feel: Cozy Ghibli night scene

### Custom UI Components (Already Built!)

All UI components are in `android/app/src/main/java/com/sleepy/ui/`:

#### 1. **GhibliColors.kt** - Color Palette
- Complete light/dark color schemes
- Prayer-time specific colors
- Gradient definitions for watercolor effects

#### 2. **GhibliTheme.kt** - Theme System
- Material 3 integration
- Custom typography with generous line-heights (1.6-1.8)
- Composition locals for theme access

#### 3. **CanvasTexture.kt** - Texture Layers
- `CanvasTextureLayer`: Subtle canvas weave pattern
- `WatercolorOverlay`: Organic gradient wash
- `OrganicDivider`: Hand-drawn style separators
- Perlin-noise based texture generation

#### 4. **TexturedBackground.kt** - Background System
- Multi-layer texture composition
- Vignette effect (darker edges like framed paintings)
- `PaintedText`: Text with subtle shadows for depth

#### 5. **MainScreen.kt** - Full-Screen Layout
- Twitter/X-style edge-to-edge design
- Large, readable typography
- No cards/boxes - just text on textured canvas
- Sections: Sleep time, prayer times, wake time, quote

### Using the Theme

```kotlin
import com.sleepy.ui.theme.GhibliTheme

@Composable
fun MyApp() {
    GhibliTheme(darkTheme = false) { // or true for dark theme
        MainScreen(
            sleepTime = "19:30",
            wakeTime = "04:00",
            fajrTime = "05:30",
            ishaTime = "19:00",
            duration = 7.0f,
            quote = "Your brain cells are begging for a reboot!",
            location = "Tashkent, Uzbekistan"
        )
    }
}
```

### Accessing Custom Colors

```kotlin
import com.sleepy.ui.theme.GhibliTheme

@Composable
fun MyComponent() {
    val fajrColor = GhibliTheme.colors.fajrColor
    val ishaColor = GhibliTheme.colors.ishaColor
    val sleepColor = GhibliTheme.colors.sleepTimeColor

    Text("Fajr: 05:30", color = fajrColor)
}
```

### Texture Intensity Customization

Adjust texture subtlety in `TexturedBackground.kt`:

```kotlin
CanvasTextureLayer(
    textureColor = textureColor,
    intensity = 0.04f,  // 0.02-0.06 range recommended
    seed = 42
)
```

### Typography System

Pre-configured with excellent readability:
- **Display Large**: 72sp for main sleep time
- **Headline**: 24-32sp for prayer names
- **Body Large**: 18sp with 32sp line-height for quotes
- All with generous spacing and warm, readable tones

### Design Principles

1. **Full-screen text**: No wasted space with excessive chrome
2. **Organic elements**: Hand-drawn dividers, subtle imperfections
3. **Canvas texture**: 3-5% opacity, pixel-level variation (not literal paper images)
4. **Painting vibes**: Watercolor washes, vignettes, soft shadows
5. **Eye comfort**: Solarized-based colors tested for long reading sessions
6. **Information hierarchy**: Typography size/weight, not boxes/cards

## Resources

- [Android Developer Docs](https://developer.android.com/)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [App Widgets Overview](https://developer.android.com/guide/topics/appwidgets/overview)
- [Material 3 Design](https://m3.material.io/)
- [Solarized Color Scheme](https://ethanschoonover.com/solarized/)
