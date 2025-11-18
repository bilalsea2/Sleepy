from datetime import datetime, timedelta, time, date
from typing import Optional
from src.models.prayer_times import PrayerTimes, SleepSchedule
from config.settings import (
    DEFAULT_SLEEP_DURATION_HOURS,
    MIN_SLEEP_DURATION_HOURS,
    MAX_SLEEP_DURATION_HOURS,
    OPTIMAL_WAKE_TIME_HOUR,
    ISHA_BUFFER_MINUTES,
    FAJR_BUFFER_MINUTES
)


class SleepOptimizer:
    """Optimizes sleep schedule based on prayer times"""

    def __init__(self):
        self.default_duration = DEFAULT_SLEEP_DURATION_HOURS
        self.min_duration = MIN_SLEEP_DURATION_HOURS
        self.max_duration = MAX_SLEEP_DURATION_HOURS
        self.optimal_wake_hour = OPTIMAL_WAKE_TIME_HOUR
        self.isha_buffer = ISHA_BUFFER_MINUTES
        self.fajr_buffer = FAJR_BUFFER_MINUTES

    def calculate_optimal_schedule(
        self,
        prayer_times: PrayerTimes,
        target_date: Optional[date] = None
    ) -> SleepSchedule:
        """
        Calculate optimal sleep schedule based on prayer times

        Logic:
        1. Sleep start: Isha + 30 minutes buffer
        2. Sleep end:
           - If enough time for >7 hrs, wake at 4 AM (or before Fajr if Fajr < 4 AM)
           - Otherwise, wake at Fajr or slightly before
        3. Duration: 6-7.5 hours flexible, prefer 7 hours

        Args:
            prayer_times: Prayer times for the day
            target_date: Target date (defaults to prayer_times.date)

        Returns:
            Optimal sleep schedule
        """
        if target_date is None:
            target_date = datetime.strptime(prayer_times.date, '%Y-%m-%d').date()

        # Parse prayer times
        isha_time = self._parse_time(prayer_times.isha)
        fajr_time = self._parse_time(prayer_times.fajr)

        # Calculate sleep start (Isha + buffer)
        sleep_start_dt = datetime.combine(target_date, isha_time) + timedelta(minutes=self.isha_buffer)
        sleep_start_time = sleep_start_dt.time()

        # Calculate sleep end
        # Convert times to datetime for easier calculation
        next_day = target_date + timedelta(days=1)
        fajr_dt = datetime.combine(next_day, fajr_time)
        optimal_wake_dt = datetime.combine(next_day, time(hour=self.optimal_wake_hour, minute=0))

        # Calculate available sleep window
        available_hours = (fajr_dt - sleep_start_dt).total_seconds() / 3600

        # Determine optimal wake time
        if available_hours >= 7.5:
            # Plenty of time, use 4 AM pivot if Fajr is after 4 AM
            if fajr_time.hour > self.optimal_wake_hour:
                wake_dt = optimal_wake_dt
                notes = f"Wake early at {self.optimal_wake_hour}:00 AM for maximum productivity before Fajr"
            else:
                # Fajr is before 4 AM, wake at Fajr
                wake_dt = fajr_dt - timedelta(minutes=self.fajr_buffer)
                notes = "Wake at Fajr time"
        elif available_hours >= self.min_duration:
            # Use all available time up to Fajr
            wake_dt = fajr_dt - timedelta(minutes=self.fajr_buffer)
            notes = "Wake at Fajr time"
        else:
            # Not enough sleep time - this shouldn't happen normally
            wake_dt = fajr_dt - timedelta(minutes=self.fajr_buffer)
            notes = "Warning: Less than minimum sleep duration available"

        sleep_end_time = wake_dt.time()

        # Calculate actual duration
        duration_seconds = (wake_dt - sleep_start_dt).total_seconds()
        duration_hours = round(duration_seconds / 3600, 2)

        # Adjust if needed to stay within min/max bounds
        if duration_hours < self.min_duration:
            # Shift sleep start earlier
            sleep_start_dt = wake_dt - timedelta(hours=self.min_duration)
            sleep_start_time = sleep_start_dt.time()
            duration_hours = self.min_duration
            notes = f"Adjusted to minimum {self.min_duration} hours (may overlap with Isha buffer)"
        elif duration_hours > self.max_duration:
            # Shift wake time later
            wake_dt = sleep_start_dt + timedelta(hours=self.max_duration)
            sleep_end_time = wake_dt.time()
            duration_hours = self.max_duration
            notes = f"Capped at maximum {self.max_duration} hours"

        return SleepSchedule(
            date=target_date.strftime('%Y-%m-%d'),
            sleep_start=sleep_start_time.strftime('%H:%M'),
            sleep_end=sleep_end_time.strftime('%H:%M'),
            duration_hours=duration_hours,
            isha_time=prayer_times.isha,
            fajr_time=prayer_times.fajr,
            notes=notes
        )

    def _parse_time(self, time_str: str) -> time:
        """
        Parse time string in HH:MM format

        Args:
            time_str: Time string in format "HH:MM" or "HH:MM:SS"

        Returns:
            time object
        """
        # Handle both HH:MM and HH:MM:SS formats
        parts = time_str.strip().split(':')
        hour = int(parts[0])
        minute = int(parts[1])
        return time(hour=hour, minute=minute)

    def get_time_until_sleep(self, sleep_schedule: SleepSchedule) -> Optional[str]:
        """
        Get human-readable time remaining until sleep time

        Args:
            sleep_schedule: The sleep schedule

        Returns:
            String like "2 hours 30 minutes" or None if sleep time has passed
        """
        now = datetime.now()
        schedule_date = datetime.strptime(sleep_schedule.date, '%Y-%m-%d').date()
        sleep_time = self._parse_time(sleep_schedule.sleep_start)
        sleep_dt = datetime.combine(schedule_date, sleep_time)

        # If sleep time is in the past for today, it might be for tomorrow
        if sleep_dt < now:
            sleep_dt = datetime.combine(schedule_date + timedelta(days=1), sleep_time)

        time_diff = sleep_dt - now

        if time_diff.total_seconds() < 0:
            return None

        hours = int(time_diff.total_seconds() // 3600)
        minutes = int((time_diff.total_seconds() % 3600) // 60)

        if hours > 0:
            return f"{hours} hour{'s' if hours != 1 else ''} {minutes} minute{'s' if minutes != 1 else ''}"
        else:
            return f"{minutes} minute{'s' if minutes != 1 else ''}"
