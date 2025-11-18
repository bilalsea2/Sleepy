from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from datetime import date, datetime
from typing import Optional

from src.models.prayer_times import PrayerTimes, SleepSchedule, LocationInfo
from src.models.database import init_db
from src.services.prayer_times_service import PrayerTimesService
from src.services.sleep_optimizer import SleepOptimizer
from src.services.location_service import LocationService
from src.services.google_calendar_service import GoogleCalendarService
from src.utils.sleep_quotes import get_random_sleep_quote, get_supportive_quote, get_urgent_quote

# Initialize database
init_db()

# Create FastAPI app
app = FastAPI(
    title="Sleepy API",
    description="Prayer-aware sleep schedule optimization API",
    version="1.0.0"
)

# Enable CORS for Android app
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, specify your Android app's origin
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize services
prayer_service = PrayerTimesService()
sleep_optimizer = SleepOptimizer()
location_service = LocationService()
calendar_service = GoogleCalendarService()


@app.get("/")
def read_root():
    """API health check"""
    return {
        "status": "ok",
        "message": "Sleepy API is running",
        "version": "1.0.0"
    }


@app.get("/location/gps", response_model=LocationInfo)
def get_location_from_gps(
    latitude: float = Query(..., description="GPS latitude"),
    longitude: float = Query(..., description="GPS longitude")
):
    """
    Get location information from GPS coordinates
    """
    return location_service.get_location_from_gps(latitude, longitude)


@app.get("/location/city/{city_name}", response_model=LocationInfo)
def get_location_by_city(city_name: str):
    """
    Get location information by city name
    """
    location = location_service.get_location_by_city_name(city_name)
    if not location:
        raise HTTPException(status_code=404, detail="City not found")
    return location


@app.get("/location/last", response_model=Optional[LocationInfo])
def get_last_known_location():
    """
    Get the last known location from database
    """
    location = location_service.get_last_known_location()
    if not location:
        raise HTTPException(status_code=404, detail="No location history found")
    return location


@app.post("/prayer-times", response_model=PrayerTimes)
def get_prayer_times(
    location: LocationInfo,
    target_date: Optional[str] = Query(None, description="Date in YYYY-MM-DD format")
):
    """
    Get prayer times for a location and date
    """
    date_obj = None
    if target_date:
        try:
            date_obj = datetime.strptime(target_date, '%Y-%m-%d').date()
        except ValueError:
            raise HTTPException(status_code=400, detail="Invalid date format. Use YYYY-MM-DD")

    prayer_times = prayer_service.get_prayer_times(location, date_obj)
    if not prayer_times:
        raise HTTPException(status_code=500, detail="Failed to fetch prayer times")

    return prayer_times


@app.post("/sleep-schedule", response_model=SleepSchedule)
def calculate_sleep_schedule(prayer_times: PrayerTimes):
    """
    Calculate optimal sleep schedule based on prayer times
    """
    return sleep_optimizer.calculate_optimal_schedule(prayer_times)


@app.post("/sleep-schedule/full")
def get_full_sleep_schedule(
    latitude: float = Query(..., description="GPS latitude"),
    longitude: float = Query(..., description="GPS longitude"),
    target_date: Optional[str] = Query(None, description="Date in YYYY-MM-DD format"),
    create_calendar_event: bool = Query(False, description="Create Google Calendar event")
):
    """
    Get complete sleep schedule (location + prayer times + optimization)
    """
    # Get location
    location = location_service.get_location_from_gps(latitude, longitude)

    # Parse date
    date_obj = None
    if target_date:
        try:
            date_obj = datetime.strptime(target_date, '%Y-%m-%d').date()
        except ValueError:
            raise HTTPException(status_code=400, detail="Invalid date format. Use YYYY-MM-DD")

    # Get prayer times
    prayer_times = prayer_service.get_prayer_times(location, date_obj)
    if not prayer_times:
        raise HTTPException(status_code=500, detail="Failed to fetch prayer times")

    # Calculate sleep schedule
    sleep_schedule = sleep_optimizer.calculate_optimal_schedule(prayer_times)

    # Optionally create calendar event
    calendar_event_id = None
    if create_calendar_event:
        calendar_event_id = calendar_service.create_sleep_block(sleep_schedule)

    # Get time until sleep
    time_until_sleep = sleep_optimizer.get_time_until_sleep(sleep_schedule)

    return {
        "location": location,
        "prayer_times": prayer_times,
        "sleep_schedule": sleep_schedule,
        "time_until_sleep": time_until_sleep,
        "calendar_event_id": calendar_event_id,
        "notification_quote": get_random_sleep_quote()
    }


@app.get("/quotes/random")
def get_random_quote():
    """Get a random sleep notification quote"""
    return {"quote": get_random_sleep_quote()}


@app.get("/quotes/supportive")
def get_supportive_sleep_quote():
    """Get a supportive/encouraging sleep quote"""
    return {"quote": get_supportive_quote()}


@app.get("/quotes/urgent")
def get_urgent_sleep_quote():
    """Get a playfully threatening/urgent sleep quote"""
    return {"quote": get_urgent_quote()}


@app.post("/calendar/authenticate")
def authenticate_google_calendar():
    """
    Authenticate with Google Calendar
    """
    success = calendar_service.authenticate()
    if success:
        return {"status": "success", "message": "Successfully authenticated with Google Calendar"}
    else:
        raise HTTPException(
            status_code=500,
            detail="Failed to authenticate with Google Calendar. Check credentials."
        )


@app.post("/calendar/create-sleep-block")
def create_calendar_sleep_block(sleep_schedule: SleepSchedule):
    """
    Create a sleep block event in Google Calendar
    """
    event_id = calendar_service.create_sleep_block(sleep_schedule)
    if event_id:
        return {
            "status": "success",
            "event_id": event_id,
            "message": "Sleep block created in Google Calendar"
        }
    else:
        raise HTTPException(status_code=500, detail="Failed to create calendar event")


@app.delete("/calendar/event/{event_id}")
def delete_calendar_event(event_id: str):
    """
    Delete a calendar event
    """
    success = calendar_service.delete_event(event_id)
    if success:
        return {"status": "success", "message": "Event deleted"}
    else:
        raise HTTPException(status_code=500, detail="Failed to delete event")


@app.get("/time-until-sleep")
def get_time_until_sleep_endpoint(sleep_schedule: SleepSchedule):
    """
    Get human-readable time remaining until sleep time
    """
    time_remaining = sleep_optimizer.get_time_until_sleep(sleep_schedule)
    return {
        "time_until_sleep": time_remaining,
        "sleep_time": sleep_schedule.sleep_start
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
