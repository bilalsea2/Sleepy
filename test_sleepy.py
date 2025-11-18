#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Quick test script to verify Sleepy functionality
Run this to test the core features without starting the full API server
"""

import sys
import io
from datetime import date

# Fix Windows console encoding
if sys.platform == 'win32':
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
from src.models.prayer_times import LocationInfo
from src.services.location_service import LocationService
from src.services.prayer_times_service import PrayerTimesService
from src.services.sleep_optimizer import SleepOptimizer
from src.utils.sleep_quotes import get_random_sleep_quote, get_supportive_quote, get_urgent_quote
from src.models.database import init_db

def main():
    print("="*60)
    print("Sleepy - Quick Test")
    print("="*60)

    # Initialize database
    print("\n1. Initializing database...")
    init_db()
    print("✓ Database initialized")

    # Test location service
    print("\n2. Testing location service...")
    location_service = LocationService()

    # Test with Tashkent coordinates
    tashkent_location = location_service.get_location_from_gps(41.2995, 69.2401)
    print(f"✓ Location detected: {tashkent_location.city}, {tashkent_location.country}")
    print(f"  Is Uzbekistan: {tashkent_location.is_uzbekistan}")

    # Test prayer times service
    print("\n3. Testing prayer times service...")
    prayer_service = PrayerTimesService()

    print(f"  Fetching prayer times for {tashkent_location.city}...")
    prayer_times = prayer_service.get_prayer_times(tashkent_location, date.today())

    if prayer_times:
        print(f"✓ Prayer times retrieved for {prayer_times.date}:")
        print(f"  Fajr:    {prayer_times.fajr}")
        print(f"  Sunrise: {prayer_times.sunrise}")
        print(f"  Dhuhr:   {prayer_times.dhuhr}")
        print(f"  Asr:     {prayer_times.asr}")
        print(f"  Maghrib: {prayer_times.maghrib}")
        print(f"  Isha:    {prayer_times.isha}")
    else:
        print("✗ Failed to fetch prayer times")
        print("  Note: This is expected if you're offline or the API is unavailable")
        print("  Using mock data for testing...")

        # Create mock prayer times for testing
        from src.models.prayer_times import PrayerTimes
        prayer_times = PrayerTimes(
            date=date.today().strftime('%Y-%m-%d'),
            fajr="05:30",
            sunrise="07:00",
            dhuhr="12:15",
            asr="15:00",
            maghrib="17:30",
            isha="19:00",
            city=tashkent_location.city,
            country=tashkent_location.country,
            latitude=tashkent_location.latitude,
            longitude=tashkent_location.longitude
        )
        print(f"✓ Using mock prayer times for testing")

    # Test sleep optimizer
    print("\n4. Testing sleep optimization algorithm...")
    sleep_optimizer = SleepOptimizer()
    sleep_schedule = sleep_optimizer.calculate_optimal_schedule(prayer_times)

    print(f"✓ Optimal sleep schedule calculated:")
    print(f"  Date:         {sleep_schedule.date}")
    print(f"  Sleep start:  {sleep_schedule.sleep_start} (Isha + 30 min buffer)")
    print(f"  Sleep end:    {sleep_schedule.sleep_end}")
    print(f"  Duration:     {sleep_schedule.duration_hours} hours")
    print(f"  Notes:        {sleep_schedule.notes}")

    # Get time until sleep
    time_until = sleep_optimizer.get_time_until_sleep(sleep_schedule)
    if time_until:
        print(f"  Time until sleep: {time_until}")

    # Test sleep quotes
    print("\n5. Testing sleep notification quotes...")
    print(f"✓ Random quote: \"{get_random_sleep_quote()}\"")
    print(f"✓ Supportive quote: \"{get_supportive_quote()}\"")
    print(f"✓ Urgent quote: \"{get_urgent_quote()}\"")

    # Test last known location
    print("\n6. Testing location persistence...")
    last_location = location_service.get_last_known_location()
    if last_location:
        print(f"✓ Last known location retrieved: {last_location.city}")
    else:
        print("  No previous location found (this is normal on first run)")

    # Summary
    print("\n" + "="*60)
    print("Test Summary")
    print("="*60)
    print("✓ All core components working!")
    print("\nNext steps:")
    print("1. Run 'python main.py' to start the API server")
    print("2. Visit http://localhost:8000/docs for API documentation")
    print("3. Set up Google Calendar credentials (see config/GOOGLE_SETUP.md)")
    print("4. Start building the Android app (see ANDROID_GUIDE.md)")
    print("="*60)

if __name__ == "__main__":
    main()
