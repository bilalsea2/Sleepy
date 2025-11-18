import requests
from datetime import datetime, timedelta, date
from typing import Optional
from src.models.prayer_times import PrayerTimes, LocationInfo
from src.models.database import CachedPrayerTimes, get_session
from config.settings import (
    NAMOZVAQTI_UZ_BASE_URL,
    ALADHAN_API_BASE_URL,
    HANAFI_CALCULATION_METHOD,
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
        try:
            if location.is_uzbekistan:
                prayer_times = self._fetch_from_namozvaqti(location, target_date)
            else:
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
        Fetch prayer times from Aladhan API (Hanafi method)
        """
        try:
            timestamp = int(datetime.combine(target_date, datetime.min.time()).timestamp())

            url = f"{ALADHAN_API_BASE_URL}/timings/{timestamp}"
            params = {
                'latitude': location.latitude,
                'longitude': location.longitude,
                'method': HANAFI_CALCULATION_METHOD,
                'school': 1  # Hanafi school for Asr calculation
            }

            response = requests.get(url, params=params, timeout=10)
            response.raise_for_status()

            data = response.json()

            if data.get('code') != 200 or 'data' not in data:
                return None

            timings = data['data']['timings']

            return PrayerTimes(
                date=target_date.strftime('%Y-%m-%d'),
                fajr=timings.get('Fajr', '').split()[0],  # Remove timezone if present
                sunrise=timings.get('Sunrise', '').split()[0],
                dhuhr=timings.get('Dhuhr', '').split()[0],
                asr=timings.get('Asr', '').split()[0],
                maghrib=timings.get('Maghrib', '').split()[0],
                isha=timings.get('Isha', '').split()[0],
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
