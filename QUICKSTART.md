# Sleepy - Quick Start Guide

Get your prayer-aware sleep optimization app running in minutes!

## 1. Start Python Backend (2 minutes)

```bash
# Navigate to project
cd Sleepy

# Activate virtual environment
venv\Scripts\activate     # Windows
source venv/bin/activate  # Mac/Linux

# Run backend
python main.py
```

**Expected output:**
```
============================================================
Sleepy - Prayer-Aware Sleep Schedule Optimizer
============================================================

Initializing database...
Database initialized successfully!

Starting API server on http://localhost:8000
API documentation available at http://localhost:8000/docs
```

**Test it works:**
- Open browser: http://localhost:8000/docs
- You should see FastAPI interactive docs

---

## 2. Build Android App (5-10 minutes)

### Prerequisites
- **Android Studio** (download from [developer.android.com](https://developer.android.com/studio))
- Backend running (from step 1)

### Build Steps

1. **Open Android Studio**
   - Click **Open**
   - Navigate to `Sleepy/android` folder
   - Click **OK**

2. **Wait for Gradle Sync**
   - First time: 5-15 minutes
   - Watch progress bar at bottom
   - If errors: Click "Try Again"

3. **Create Emulator**
   - Click **Device Manager** (phone icon, right sidebar)
   - **Create Device** → **Pixel 6** → **API 34**
   - Click **Finish** and start emulator

4. **Run App**
   - Click green **Run** button (play icon)
   - Wait for build (2-5 minutes first time)
   - App launches in emulator!

5. **Grant Location Permission**
   - Tap **Allow** when prompted
   - App fetches your location and displays beautiful sleep schedule!

---

## 3. What You'll See ✨

### Beautiful Ghibli-Inspired UI
- **Large sleep time** display (e.g., "19:30")
- **Prayer times** (Fajr and Isha)
- **Wake time** and sleep duration
- **Creative quote** ("Your brain cells are begging for a reboot!")
- **Textured canvas background** with Studio Ghibli colors
- **Smooth, organic UI** elements

### Features Working
- ✅ GPS location detection (Tashkent, Uzbekistan or your city)
- ✅ Real-time prayer times from API
- ✅ Smart sleep optimization (4 AM wake pivot)
- ✅ 7-hour sleep calculation (flexible 6-7.5 hrs)
- ✅ Time until sleep countdown
- ✅ Gorgeous light/dark themes

---

## Troubleshooting

### "Connection Error" in App
1. **Check backend is running**: Look for "Starting API server..." message
2. **Test backend**: Open http://localhost:8000/ in browser
3. **Emulator connection**: Should work automatically (uses `10.0.2.2`)

### Gradle Sync Failed
1. **File → Invalidate Caches → Restart**
2. Try sync again

### Can't Find Android SDK
1. **Tools → SDK Manager**
2. Install **Android SDK Platform 34**

---

## Testing Different Features

### Test Different Cities
In emulator:
1. Click **...** (Extended Controls)
2. **Location** tab
3. Enter coordinates:
   - Tashkent: `41.2995, 69.2401`
   - Samarkand: `39.6542, 66.9597`
   - Bukhara: `39.7681, 64.4549`

### Switch to Dark Theme
Beautiful twilight theme! Edit `MainActivity.kt`:
```kotlin
GhibliTheme(darkTheme = true) {
    // ...
}
```

### View API Logs
- **Logcat** (bottom of Android Studio)
- Filter: `okhttp` to see API requests
- Filter: `System.out` to see app logs

---

## Next Steps

### Customize UI
- **Colors**: Edit `android/app/src/main/java/com/sleepy/ui/theme/GhibliColors.kt`
- **Texture**: Adjust intensity in `TexturedBackground.kt`
- **Typography**: Modify `GhibliTheme.kt`

### Add Features
- **Widget**: See `ANDROID_GUIDE.md`
- **Notifications**: Implement sleep reminders
- **Settings**: Add user preferences
- **Google Calendar**: Enable auto-blocking

### Deploy
- **Test on Phone**: Connect via USB, enable USB debugging
- **Build APK**: See `android/BUILD_GUIDE.md`
- **Production**: Deploy Python backend to cloud

---

## File Structure

```
Sleepy/
├── main.py                  # Python backend entry point
├── test_sleepy.py          # Test script (no Android needed)
├── requirements.txt        # Python dependencies
│
├── src/                    # Python backend
│   ├── api/               # FastAPI endpoints
│   ├── services/          # Business logic
│   ├── models/            # Data models
│   └── utils/             # Sleep quotes, helpers
│
├── android/               # Android app
│   ├── BUILD_GUIDE.md    # Detailed build instructions
│   └── app/src/main/java/com/sleepy/
│       ├── MainActivity.kt          # App entry point
│       ├── ui/theme/                # Ghibli UI theme
│       ├── ui/components/           # Canvas textures
│       ├── ui/screens/              # Main screen
│       ├── data/                    # API client
│       └── viewmodel/               # State management
│
└── config/                # Configuration
    ├── settings.py       # Backend settings
    └── GOOGLE_SETUP.md   # Calendar API setup
```

---

## Documentation

- **README.md**: Project overview and features
- **ANDROID_GUIDE.md**: Complete Android development guide
- **android/BUILD_GUIDE.md**: Step-by-step build instructions
- **config/GOOGLE_SETUP.md**: Google Calendar integration

---

## Support

Built with ❤️ using:
- Python + FastAPI (backend)
- Kotlin + Jetpack Compose (Android)
- Studio Ghibli-inspired design
- Solarized color scheme

Enjoy your optimized sleep schedule! 😴✨
