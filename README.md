# Sleepy

A prayer-aware sleep schedule optimizer that calculates optimal sleep times based on Islamic prayer times (especially Fajr and Isha), with Google Calendar integration and Android widget support.

## Features

- **Prayer Times Integration**
  - Uses namozvaqti.uz for Uzbekistan locations
  - Aladhan API (Hanafi method) for other locations
  - Offline caching for reliability
  - GPS-based location detection

- **Smart Sleep Optimization**
  - 30-minute buffer after Isha before sleep
  - Wake time pivots around 4 AM for maximum productivity
  - Flexible duration: 6-7.5 hours (targets 7 hours)
  - Automatic daily recalculation as prayer times change

- **Google Calendar Integration**
  - Automatic sleep block creation
  - Conflict detection with existing events
  - Displays optimal sleep schedule

- **Android Widget**
  - Shows next sleep time
  - Displays prayer times
  - Shows sleep duration
  - Time until sleep countdown

- **Creative Notifications**
  - 100 unique sleep reminder quotes
  - Mix of supportive and playfully urgent (Duolingo-style)
  - Teen-friendly content

## Architecture

```
Sleepy/
├── config/                 # Configuration files
│   ├── settings.py        # App settings
│   ├── credentials.json   # Google OAuth credentials (not in repo)
│   └── token.json         # Google OAuth token (not in repo)
├── src/
│   ├── api/               # FastAPI REST API
│   │   └── main.py        # API endpoints
│   ├── models/            # Data models
│   │   ├── prayer_times.py
│   │   └── database.py
│   ├── services/          # Business logic
│   │   ├── prayer_times_service.py
│   │   ├── sleep_optimizer.py
│   │   ├── location_service.py
│   │   └── google_calendar_service.py
│   └── utils/             # Utilities
│       └── sleep_quotes.py
├── data/                  # SQLite database
├── android/               # Android app (coming soon)
└── main.py               # Application entry point
```

## Setup

### Prerequisites

- Python 3.8+
- Virtual environment (venv)
- Google Cloud Platform account (for Calendar API)

### Installation

1. **Clone the repository**
   ```bash
   cd Sleepy
   ```

2. **Create and activate virtual environment**
   ```bash
   python -m venv venv

   # Windows
   venv\Scripts\activate

   # Linux/Mac
   source venv/bin/activate
   ```

3. **Install dependencies**
   ```bash
   pip install -r requirements.txt
   ```

4. **Set up Google Calendar API**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project
   - Enable Google Calendar API
   - Create OAuth 2.0 credentials (Desktop app)
   - Download credentials.json
   - Place it in `config/credentials.json`

5. **Run the application**
   ```bash
   python main.py
   ```

   The API will start at `http://localhost:8000`
   API documentation: `http://localhost:8000/docs`

## API Endpoints

### Location
- `GET /location/gps?latitude={lat}&longitude={lon}` - Get location from GPS
- `GET /location/city/{city_name}` - Get location by city name
- `GET /location/last` - Get last known location

### Prayer Times
- `POST /prayer-times` - Get prayer times for location and date

### Sleep Schedule
- `POST /sleep-schedule` - Calculate optimal sleep schedule
- `POST /sleep-schedule/full` - Get complete schedule (location + prayers + optimization)
- `GET /time-until-sleep` - Get time remaining until sleep

### Quotes
- `GET /quotes/random` - Random sleep quote
- `GET /quotes/supportive` - Supportive quote
- `GET /quotes/urgent` - Urgent quote

### Google Calendar
- `POST /calendar/authenticate` - Authenticate with Google
- `POST /calendar/create-sleep-block` - Create sleep event
- `DELETE /calendar/event/{event_id}` - Delete event

## Usage Example

### Python/cURL

```bash
# Get full sleep schedule for Tashkent
curl -X POST "http://localhost:8000/sleep-schedule/full?latitude=41.2995&longitude=69.2401&create_calendar_event=true"
```

### Response
```json
{
  "location": {
    "city": "Tashkent",
    "country": "Uzbekistan",
    "latitude": 41.2995,
    "longitude": 69.2401,
    "timezone": "Asia/Tashkent",
    "is_uzbekistan": true
  },
  "prayer_times": {
    "date": "2025-01-15",
    "fajr": "05:30",
    "isha": "19:00",
    ...
  },
  "sleep_schedule": {
    "date": "2025-01-15",
    "sleep_start": "19:30",
    "sleep_end": "04:00",
    "duration_hours": 7.0,
    "notes": "Wake early at 4:00 AM for maximum productivity before Fajr"
  },
  "time_until_sleep": "3 hours 15 minutes",
  "notification_quote": "Your brain cells are begging for a reboot. Give them what they deserve!"
}
```

## Configuration

Edit `config/settings.py` to customize:

- `DEFAULT_SLEEP_DURATION_HOURS`: Target sleep duration (default: 7.0)
- `OPTIMAL_WAKE_TIME_HOUR`: Pivot wake time hour (default: 4)
- `ISHA_BUFFER_MINUTES`: Wait time after Isha (default: 30)
- `PRAYER_TIMES_CACHE_DAYS`: Cache duration (default: 30)

## Android App (Coming Soon)

The Android app will include:
- Native Kotlin implementation
- Home screen widget
- Sleep time notifications with creative quotes
- Offline mode support
- GPS location detection

## Database

SQLite database stores:
- Cached prayer times (30-day rolling window)
- Last known GPS location
- Sleep schedule history

## Contributing

This is a personal project, but suggestions are welcome!

## License

MIT License - see LICENSE file

## Acknowledgments

- Prayer times from [namozvaqti.uz](https://namozvaqti.uz/)
- International prayer times from [Aladhan API](https://aladhan.com/prayer-times-api)
- Inspired by productivity principles of early risers like Demis Hassabis
