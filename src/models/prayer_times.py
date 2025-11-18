from datetime import datetime
from typing import Optional
from pydantic import BaseModel


class PrayerTimes(BaseModel):
    """Prayer times for a specific day"""
    date: str  # YYYY-MM-DD format
    fajr: str  # HH:MM format
    sunrise: str
    dhuhr: str
    asr: str
    maghrib: str
    isha: str
    city: str
    country: str
    latitude: float
    longitude: float

    class Config:
        json_schema_extra = {
            "example": {
                "date": "2025-01-15",
                "fajr": "05:30",
                "sunrise": "07:00",
                "dhuhr": "12:15",
                "asr": "15:00",
                "maghrib": "17:30",
                "isha": "19:00",
                "city": "Tashkent",
                "country": "Uzbekistan",
                "latitude": 41.2995,
                "longitude": 69.2401
            }
        }


class SleepSchedule(BaseModel):
    """Optimal sleep schedule for a day"""
    date: str  # YYYY-MM-DD format
    sleep_start: str  # HH:MM format
    sleep_end: str  # HH:MM format
    duration_hours: float
    isha_time: str
    fajr_time: str
    notes: Optional[str] = None

    class Config:
        json_schema_extra = {
            "example": {
                "date": "2025-01-15",
                "sleep_start": "19:30",
                "sleep_end": "04:00",
                "duration_hours": 7.0,
                "isha_time": "19:00",
                "fajr_time": "05:30",
                "notes": "Wake early for maximum productivity"
            }
        }


class LocationInfo(BaseModel):
    """User location information"""
    city: str
    country: str
    latitude: float
    longitude: float
    timezone: str
    is_uzbekistan: bool
