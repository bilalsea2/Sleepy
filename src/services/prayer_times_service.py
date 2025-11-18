import requests
from datetime import datetime, timedelta, date
from typing import Optional
from src.models.prayer_times import PrayerTimes, LocationInfo
from src.models.database import CachedPrayerTimes, get_session
from config.settings import (
    NAMOZVAQTI_UZ_BASE_URL,
    ALADHAN_API_BASE_URL,
    ALADHAN_CALCULATION_METHOD,
    ALADHAN_SCHOOL,
    ALADHAN_MIDNIGHT_MODE,
    PRAYER_TIME_SAFETY_BUFFER,
    PRAYER_TIMES_CACHE_DAYS
)


class PrayerTimesService:
    """Service for fetching and caching prayer times"""

    def __init__(self):
        self.session = get_session()

    def get_prayer_times(
        self,
        location: LocationInfo,
        target_date: Optional[date] = None,
        use_cache: bool = True
    ) -> Optional[PrayerTimes]:
        """
        Get prayer times for a specific location and date

        Args:
            location: User's location information
            target_date: Date to get prayer times for (default: today)
            use_cache: Whether to use cached data if available

        Returns:
            PrayerTimes object or None if unavailable
        """
        if target_date is None:
            target_date = date.today()

        # Try to get from cache first
        if use_cache:
            cached = self._get_from_cache(location.city, target_date)
            if cached:
                return cached

        # Fetch from API
        # Note: namozvaqti.uz doesn't have a public API, using Aladhan for all locations
        try:
            prayer_times = self._fetch_from_aladhan(location, target_date)

            # Cache the result
            if prayer_times:
                self._save_to_cache(prayer_times)

            return prayer_times
        except Exception as e:
            print(f"Error fetching prayer times: {e}")
            # Try to get from cache as fallback
            return self._get_from_cache(location.city, target_date)

    def _fetch_from_namozvaqti(
        self,
        location: LocationInfo,
        target_date: date
    ) -> Optional[PrayerTimes]:
        """
        Fetch prayer times from namozvaqti.uz API

        Note: This is a placeholder implementation. The actual API structure
        needs to be verified by examining namozvaqti.uz's API documentation.
        """
        try:
            # The namozvaqti.uz API might use different endpoints
            # This is a generic implementation that needs to be tested
            month = target_date.month
            year = target_date.year

            # Example endpoint (needs verification)
            url = f"{NAMOZVAQTI_UZ_BASE_URL}/monthly/{location.city}/{year}/{month:02d}"

            response = requests.get(url, timeout=10)
            response.raise_for_status()

            data = response.json()

            # Extract prayer times for specific day
            # This structure depends on actual API response
            day_data = None
            if isinstance(data, list):
                for day in data:
                    if day.get('day') == target_date.day:
                        day_data = day
                        break

            if not day_data:
                return None

            return PrayerTimes(
                date=target_date.strftime('%Y-%m-%d'),
                fajr=day_data.get('fajr', ''),
                sunrise=day_data.get('sunrise', ''),
                dhuhr=day_data.get('dhuhr', ''),
                asr=day_data.get('asr', ''),
                maghrib=day_data.get('maghrib', ''),
                isha=day_data.get('isha', ''),
                city=location.city,
                country=location.country,
                latitude=location.latitude,
                longitude=location.longitude
            )
        except Exception as e:
            print(f"Error fetching from namozvaqti.uz: {e}")
            return None

    def _fetch_from_aladhan(
        self,
        location: LocationInfo,
        target_date: date
    ) -> Optional[PrayerTimes]:
        """
        Fetch prayer times from Aladhan API
        Using MWL method with Hanafi school (angle-based)
        """
        try:
            # Format date as DD-MM-YYYY for Aladhan API
            date_str = target_date.strftime('%d-%m-%Y')

            url = f"{ALADHAN_API_BASE_URL}/timings/{date_str}"
            params = {
                'latitude': location.latitude,
                'longitude': location.longitude,
                'method': ALADHAN_CALCULATION_METHOD,  # MWL (Muslim World League)
                'school': ALADHAN_SCHOOL,  # Hanafi jurisprudence
                'midnightMode': ALADHAN_MIDNIGHT_MODE  # Standard midnight calculation
            }

            response = requests.get(url, params=params, timeout=15)
            response.raise_for_status()

            data = response.json()

            if data.get('code') != 200 or 'data' not in data:
                return None

            timings = data['data']['timings']

            # Add 15-minute safety buffer to all prayer times
            return PrayerTimes(
                date=target_date.strftime('%Y-%m-%d'),
                fajr=self._add_time_buffer(timings.get('Fajr', '').split()[0]),
                sunrise=self._add_time_buffer(timings.get('Sunrise', '').split()[0]),
                dhuhr=self._add_time_buffer(timings.get('Dhuhr', '').split()[0]),
                asr=self._add_time_buffer(timings.get('Asr', '').split()[0]),
                maghrib=self._add_time_buffer(timings.get('Maghrib', '').split()[0]),
                isha=self._add_time_buffer(timings.get('Isha', '').split()[0]),
                city=location.city,
                country=location.country,
                latitude=location.latitude,
                longitude=location.longitude
            )
        except Exception as e:
            print(f"Error fetching from Aladhan API: {e}")
            return None

    def _get_from_cache(self, city: str, target_date: date) -> Optional[PrayerTimes]:
        """Retrieve prayer times from cache"""
        try:
            cached = self.session.query(CachedPrayerTimes).filter_by(
                city=city,
                date=target_date
            ).first()

            if cached:
                return PrayerTimes(
                    date=cached.date.strftime('%Y-%m-%d'),
                    fajr=cached.fajr,
                    sunrise=cached.sunrise,
                    dhuhr=cached.dhuhr,
                    asr=cached.asr,
                    maghrib=cached.maghrib,
                    isha=cached.isha,
                    city=cached.city,
                    country=cached.country,
                    latitude=cached.latitude,
                    longitude=cached.longitude
                )
            return None
        except Exception as e:
            print(f"Error reading from cache: {e}")
            return None

    def _save_to_cache(self, prayer_times: PrayerTimes):
        """Save prayer times to cache"""
        try:
            # Check if already exists
            existing = self.session.query(CachedPrayerTimes).filter_by(
                city=prayer_times.city,
                date=datetime.strptime(prayer_times.date, '%Y-%m-%d').date()
            ).first()

            if existing:
                # Update existing
                existing.fajr = prayer_times.fajr
                existing.sunrise = prayer_times.sunrise
                existing.dhuhr = prayer_times.dhuhr
                existing.asr = prayer_times.asr
                existing.maghrib = prayer_times.maghrib
                existing.isha = prayer_times.isha
            else:
                # Create new
                cached = CachedPrayerTimes(
                    date=datetime.strptime(prayer_times.date, '%Y-%m-%d').date(),
                    city=prayer_times.city,
                    country=prayer_times.country,
                    latitude=prayer_times.latitude,
                    longitude=prayer_times.longitude,
                    fajr=prayer_times.fajr,
                    sunrise=prayer_times.sunrise,
                    dhuhr=prayer_times.dhuhr,
                    asr=prayer_times.asr,
                    maghrib=prayer_times.maghrib,
                    isha=prayer_times.isha
                )
                self.session.add(cached)

            self.session.commit()
        except Exception as e:
            print(f"Error saving to cache: {e}")
            self.session.rollback()

    def _add_time_buffer(self, time_str: str) -> str:
        """
        Add safety buffer to prayer time

        Args:
            time_str: Time in HH:MM format

        Returns:
            Time with buffer added in HH:MM format
        """
        try:
            # Parse the time
            hour, minute = map(int, time_str.split(':'))

            # Create a datetime object for today with this time
            time_obj = datetime.now().replace(hour=hour, minute=minute, second=0, microsecond=0)

            # Add the safety buffer
            buffered_time = time_obj + timedelta(minutes=PRAYER_TIME_SAFETY_BUFFER)

            # Return formatted time
            return buffered_time.strftime('%H:%M')
        except Exception as e:
            print(f"Error adding time buffer: {e}")
            return time_str  # Return original if error

    def cleanup_old_cache(self):
        """Remove cached prayer times older than configured days"""
        try:
            cutoff_date = date.today() - timedelta(days=PRAYER_TIMES_CACHE_DAYS)
            self.session.query(CachedPrayerTimes).filter(
                CachedPrayerTimes.date < cutoff_date
            ).delete()
            self.session.commit()
        except Exception as e:
            print(f"Error cleaning up cache: {e}")
            self.session.rollback()
