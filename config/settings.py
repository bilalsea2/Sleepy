import os
from pathlib import Path

# Base directory
BASE_DIR = Path(__file__).resolve().parent.parent

# Database
DATABASE_PATH = os.path.join(BASE_DIR, "data", "sleepy.db")

# Timezone
DEFAULT_TIMEZONE = "Asia/Tashkent"

# Prayer times cache duration (days)
PRAYER_TIMES_CACHE_DAYS = 30

# Sleep preferences
DEFAULT_SLEEP_DURATION_HOURS = 7.0
MIN_SLEEP_DURATION_HOURS = 6.0
MAX_SLEEP_DURATION_HOURS = 7.5
OPTIMAL_WAKE_TIME_HOUR = 4  # 4 AM pivot point

# Prayer time buffers (minutes)
PRAYER_TIME_SAFETY_BUFFER = 15  # Add 15 min to all prayer times for safety
ISHA_BUFFER_MINUTES = 30  # Wait 30 min after Isha before sleeping
FAJR_BUFFER_MINUTES = 0  # Wake at Fajr or earlier

# API endpoints
NAMOZVAQTI_UZ_BASE_URL = "https://namozvaqti.uz/api"
ALADHAN_API_BASE_URL = "http://api.aladhan.com/v1"

# Google Calendar
GOOGLE_CALENDAR_SCOPES = ['https://www.googleapis.com/auth/calendar']
GOOGLE_CREDENTIALS_PATH = os.path.join(BASE_DIR, "config", "credentials.json")
GOOGLE_TOKEN_PATH = os.path.join(BASE_DIR, "config", "token.json")

# Aladhan API Prayer Calculation Settings
# Method: MWL (Muslim World League) - method=3
# School: Hanafi - school=1 (for Asr calculation)
# Midnight Mode: Standard - midnightMode=0
ALADHAN_CALCULATION_METHOD = 3  # MWL (Muslim World League)
ALADHAN_SCHOOL = 1  # Hanafi jurisprudence for Asr
ALADHAN_MIDNIGHT_MODE = 0  # Standard midnight calculation
