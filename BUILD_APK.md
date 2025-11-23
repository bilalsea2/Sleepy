# Building Standalone APK for Sleepy

## Good News! 🎉

Your app is now **100% standalone** - no server needed! The entire backend has been converted to native Kotlin code that runs directly in the app.

## What Changed

### Before:
```
Your Phone → Retrofit → FastAPI Backend (Python) → Aladhan API
                ↓
         (Needs server running)
```

### Now:
```
Your Phone → Native Kotlin Services → Aladhan API
                ↓
        (No server needed!)
```

All calculations happen on-device. Prayer times are cached in SQLite for 30 days.

## How to Build the APK

### Option 1: Using Android Studio (Easiest)

1. **Open the Project**
   - Open Android Studio
   - Open project: `c:\Users\ASUS\Documents\Bilol\eecs\coding\projects\Sleepy\android`

2. **Sync Gradle**
   - Click "Sync Now" if prompted (to download Room dependencies)
   - Wait for sync to complete (will download Room, KSP, etc.)

3. **Build APK**
   - Go to **Build → Build Bundle(s) / APK(s) → Build APK(s)**
   - Wait 1-2 minutes for build to complete
   - Click "locate" in the notification to find the APK

4. **Find Your APK**
   - Location: `android/app/build/outputs/apk/debug/app-debug.apk`
   - Size: ~15-20 MB

5. **Install on Your Phone**
   - Transfer APK to your phone (USB, email, cloud, etc.)
   - Install and open
   - Grant location permission
   - **That's it!** No server setup needed

### Option 2: Command Line Build

```bash
cd android
./gradlew assembleDebug

# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

## Testing the APK

1. **Install on Phone**
   - Make sure "Install from Unknown Sources" is enabled
   - Install `app-debug.apk`

2. **First Launch**
   - App requests location permission → Grant it
   - App fetches prayer times from Aladhan API
   - Sleep schedule calculated instantly (on-device)
   - See your sleep time, prayer times, and creative quote!

3. **Offline Test**
   - Turn off WiFi/data
   - Close and reopen app
   - Should work from cache (for dates already fetched)

## What Works Offline

✅ Sleep schedule calculation (pure Kotlin math)
✅ Sleep quotes (100 quotes stored in app)
✅ Prayer times (if already cached)
✅ UI and all features

❌ Initial prayer time fetch (needs internet once per day)

## Architecture

### Native Services (No Server!)

1. **PrayerTimesService.kt**
   - Calls Aladhan API directly using OkHttp
   - Caches in Room database (SQLite)
   - MWL method, Hanafi school, 15-min safety buffer
   - 30-day automatic cache cleanup

2. **SleepOptimizer.kt**
   - Pure Kotlin calculation (no network)
   - Isha+30min → Fajr or 4AM pivot
   - 6-7.5 hour flexible duration
   - All logic matches original Python backend

3. **SleepQuotes.kt**
   - 100 creative quotes stored in code
   - Supportive + playfully urgent variations
   - Random selection on each refresh

4. **Room Database**
   - SQLite for prayer times caching
   - Entities: PrayerTimesEntity
   - DAOs: PrayerTimesDao
   - Auto-cleanup of old cache

### Configuration (Same as Python Backend)

```kotlin
// Aladhan API settings
method = 3              // MWL (Muslim World League)
school = 1              // Hanafi jurisprudence
midnightMode = 0        // Standard midnight
safetyBuffer = 15 min   // 15-minute safety buffer

// Sleep settings
optimalWake = 4 AM      // 4 AM pivot point
minDuration = 6.0 hrs   // Minimum sleep
maxDuration = 7.5 hrs   // Maximum sleep
ishaBuffer = 30 min     // Wait after Isha
```

## Performance Benefits

✅ **Faster**: No network latency for calculations
✅ **Battery**: Minimal API calls (only prayer times, cached 30 days)
✅ **Reliable**: Works offline after first fetch
✅ **Simple**: No server deployment, no backend maintenance
✅ **Native**: Kotlin runs faster than network + Python

## File Sizes

- **Debug APK**: ~15-20 MB
- **Release APK** (after minification): ~8-12 MB

## Troubleshooting

### Build Errors

**"Cannot find symbol: Room"**
- Solution: Sync Gradle (File → Sync Project with Gradle Files)

**"KSP plugin not found"**
- Solution: Make sure you're using Kotlin 1.9.20+ in `build.gradle.kts`

**"AndroidViewModel constructor"**
- Fixed in latest commit - ViewModel now takes Application context

### Runtime Errors

**"Unable to fetch prayer times"**
- Check internet connection (needed for first fetch)
- Check if Aladhan API is accessible: https://api.aladhan.com

**"App crashes on launch"**
- Check logcat for errors
- Ensure location permission granted
- Room database might be initializing

## Next Steps

1. **Build the APK** using steps above
2. **Install on your phone**
3. **Test it** - grant permissions, check sleep schedule
4. **Share the APK** - no backend needed, just install!

## Optional: Google Calendar Integration

Note: Google Calendar integration from original plan still requires setup:
- OAuth credentials
- Calendar API permissions
- Not implemented in current version

Can be added later if needed!

## Summary

Your app is now **fully self-contained**:
- ✅ No Python backend needed
- ✅ No server deployment needed
- ✅ No localhost or remote URL setup
- ✅ Just build APK → install → use!

The Python backend code remains in the repo for reference, but the Android app doesn't use it anymore.

Happy coding! 🌙✨
