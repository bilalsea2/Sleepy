# Sleepy Android App - Build & Test Guide

## Quick Start

### Prerequisites

1. **Install Android Studio**
   - Download from [developer.android.com](https://developer.android.com/studio)
   - Minimum version: Android Studio Hedgehog (2023.1.1) or newer

2. **Install JDK**
   - JDK 17 or newer required
   - Usually bundled with Android Studio

3. **Python Backend Running**
   - Make sure your Python backend is running on port 8000
   - Open terminal in project root: `python main.py`
   - Should show: "Starting API server on http://localhost:8000"

### Step-by-Step Build Instructions

#### 1. Open Project in Android Studio

```bash
cd Sleepy/android
```

Open Android Studio → **Open** → Select the `android` folder

#### 2. Wait for Gradle Sync

First time opening:
- Android Studio will automatically sync Gradle dependencies
- This may take 5-15 minutes
- You'll see progress in the bottom status bar
- If errors occur, click "Try Again" or "Sync Project with Gradle Files"

#### 3. Configure API URL

The app needs to connect to your Python backend:

**For Android Emulator (default - already configured):**
- Uses `http://10.0.2.2:8000/`
- This special IP maps emulator to host's localhost
- No changes needed!

**For Physical Device:**
- Find your computer's local IP address:
  ```bash
  # Windows
  ipconfig
  # Look for IPv4 Address (e.g., 192.168.1.100)

  # Linux/Mac
  ifconfig
  # Look for inet (e.g., 192.168.1.100)
  ```

- Edit `android/app/build.gradle.kts`:
  ```kotlin
  buildConfigField("String", "API_BASE_URL", "\"http://YOUR_IP:8000/\"")
  // Example: "http://192.168.1.100:8000/"
  ```

- Make sure your phone and computer are on the **same WiFi network**

#### 4. Start Python Backend

In project root directory:

```bash
# Activate virtual environment
venv\Scripts\activate  # Windows
source venv/bin/activate  # Linux/Mac

# Run backend
python main.py
```

You should see:
```
============================================================
Sleepy - Prayer-Aware Sleep Schedule Optimizer
============================================================

Initializing database...
Database initialized successfully!

Starting API server on http://localhost:8000
API documentation available at http://localhost:8000/docs
```

**Test the backend** in your browser:
- Open http://localhost:8000/docs
- You should see the FastAPI interactive documentation

#### 5. Run the App

**Option A: Android Emulator (Recommended for testing)**

1. In Android Studio, click **Device Manager** (phone icon on right sidebar)
2. Click **Create Device**
3. Select: **Pixel 6** or any recent phone
4. System Image: **API 34 (Android 14)** or newer
5. Click **Finish**
6. Start the emulator (click play button)
7. Wait for emulator to boot (may take 2-3 minutes first time)

**Option B: Physical Device**

1. Enable Developer Options on your phone:
   - Settings → About Phone → Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging → ON
3. Connect phone via USB cable
4. Allow USB debugging prompt on phone
5. Phone should appear in Android Studio's device dropdown

**Build and Run:**

1. Click the green **Run** button (play icon) in Android Studio toolbar
2. OR: Press `Shift + F10` (Windows/Linux) or `Ctrl + R` (Mac)
3. Select your emulator or device
4. App will compile and install (first time: 2-5 minutes)

#### 6. Grant Location Permission

When app first opens:
1. It will request location permission
2. Tap **"Allow"** or **"While using the app"**
3. If you tap "Deny", it will use default Tashkent location

### What You Should See

#### Loading Screen
- "Loading sleep schedule..." with spinning indicator
- This appears while fetching data from backend

#### Success Screen (Beautiful Ghibli UI!)
- Large sleep time display (e.g., "19:30")
- Prayer times (Isha and Fajr)
- Wake time and duration
- Creative sleep quote at bottom
- Gorgeous textured background with Studio Ghibli colors

#### Error Screen
- If backend isn't running or unreachable
- Shows error message and connection help

### Troubleshooting

#### "Connection Error" / "Failed to fetch sleep schedule"

**Check Python Backend:**
```bash
# Make sure it's running
python main.py

# Test directly
curl http://localhost:8000/
# Should return: {"status":"ok","message":"Sleepy API is running"...}
```

**Check Network:**
- Emulator: Should work automatically with `10.0.2.2`
- Physical device:
  - Computer and phone on same WiFi
  - Windows Firewall not blocking port 8000
  - Correct IP in `build.gradle.kts`

**Check Firewall (Windows):**
```powershell
# Allow Python through firewall
netsh advfirewall firewall add rule name="Python FastAPI" dir=in action=allow program="C:\Path\To\Your\venv\Scripts\python.exe" enable=yes
```

#### "Gradle Sync Failed"

1. **File → Invalidate Caches** → Restart
2. Delete `.gradle` folder in `android/` directory
3. Click **Sync Project with Gradle Files** again

#### "SDK Not Found"

1. **Tools → SDK Manager**
2. Install:
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - Android Emulator
   - Android SDK Platform-Tools

#### Location Permission Not Working

- Check Android Manifest has location permissions
- In emulator: **Settings → Location → ON**
- Mock location in emulator: **Extended Controls (...)** → **Location** → Set custom GPS

### Testing Features

#### Test Different Locations

**In Emulator:**
1. Click **...** (Extended Controls)
2. **Location** tab
3. Enter coordinates:
   - Tashkent: 41.2995, 69.2401
   - Samarkand: 39.6542, 66.9597
   - Bukhara: 39.7681, 64.4549

**In Code (temporary):**
Edit `MainActivity.kt`:
```kotlin
// Replace fetchScheduleWithLocation() call with:
viewModel.fetchSleepSchedule(41.2995, 69.2401) // Tashkent
```

#### Test Dark Theme

Add to `MainActivity.kt`:
```kotlin
setContent {
    GhibliTheme(darkTheme = true) {  // Force dark theme
        // ...
    }
}
```

### Logs and Debugging

**View API Requests:**
- Android Studio → **Logcat** (bottom panel)
- Filter: `okhttp` or `Retrofit`
- You'll see all HTTP requests/responses

**View App Logs:**
- Filter: `System.out`
- Shows `println()` statements from code

**Test API Directly:**
```bash
# Health check
curl http://localhost:8000/

# Get sleep schedule (Tashkent)
curl -X POST "http://localhost:8000/sleep-schedule/full?latitude=41.2995&longitude=69.2401"
```

### Next Steps After Successful Build

1. **Customize Colors**: Edit `GhibliColors.kt`
2. **Add Widget**: See main `ANDROID_GUIDE.md`
3. **Add Notifications**: Implement WorkManager scheduler
4. **Offline Mode**: Add Room database for caching
5. **Settings Screen**: Add user preferences

### Building APK for Installation

**Debug APK (for testing on other devices):**

```bash
# In android directory
./gradlew assembleDebug  # Linux/Mac
gradlew.bat assembleDebug  # Windows

# APK location:
# android/app/build/outputs/apk/debug/app-debug.apk
```

**Install on Device:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Common Success Indicators

✅ Gradle sync completed successfully
✅ Python backend running on :8000
✅ App launches in emulator/device
✅ Location permission granted
✅ Beautiful Ghibli UI displays
✅ Prayer times showing correctly
✅ Sleep schedule calculated
✅ Creative quote appears

### Need Help?

- Check `ANDROID_GUIDE.md` for detailed Android development info
- Review API docs: http://localhost:8000/docs
- Test backend independently with `test_sleepy.py`
