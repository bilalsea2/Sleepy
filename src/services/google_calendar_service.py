import os
from datetime import datetime, timedelta, date
from typing import Optional, List
from google.oauth2.credentials import Credentials
from google.auth.transport.requests import Request
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError

from src.models.prayer_times import SleepSchedule
from config.settings import (
    GOOGLE_CALENDAR_SCOPES,
    GOOGLE_CREDENTIALS_PATH,
    GOOGLE_TOKEN_PATH
)


class GoogleCalendarService:
    """Service for Google Calendar integration"""

    def __init__(self):
        self.creds = None
        self.service = None

    def authenticate(self) -> bool:
        """
        Authenticate with Google Calendar API

        Returns:
            True if authentication successful, False otherwise
        """
        try:
            # Check if token file exists
            if os.path.exists(GOOGLE_TOKEN_PATH):
                self.creds = Credentials.from_authorized_user_file(
                    GOOGLE_TOKEN_PATH,
                    GOOGLE_CALENDAR_SCOPES
                )

            # If credentials don't exist or are invalid, get new ones
            if not self.creds or not self.creds.valid:
                if self.creds and self.creds.expired and self.creds.refresh_token:
                    self.creds.refresh(Request())
                else:
                    if not os.path.exists(GOOGLE_CREDENTIALS_PATH):
                        print(f"Error: Google credentials file not found at {GOOGLE_CREDENTIALS_PATH}")
                        print("Please download credentials.json from Google Cloud Console")
                        return False

                    flow = InstalledAppFlow.from_client_secrets_file(
                        GOOGLE_CREDENTIALS_PATH,
                        GOOGLE_CALENDAR_SCOPES
                    )
                    self.creds = flow.run_local_server(port=0)

                # Save credentials for next run
                with open(GOOGLE_TOKEN_PATH, 'w') as token:
                    token.write(self.creds.to_json())

            # Build the service
            self.service = build('calendar', 'v3', credentials=self.creds)
            return True

        except Exception as e:
            print(f"Error authenticating with Google Calendar: {e}")
            return False

    def create_sleep_block(
        self,
        sleep_schedule: SleepSchedule,
        check_conflicts: bool = True
    ) -> Optional[str]:
        """
        Create a sleep block event in Google Calendar

        Args:
            sleep_schedule: The sleep schedule to block on calendar
            check_conflicts: Whether to check for existing events and avoid conflicts

        Returns:
            Event ID if successful, None otherwise
        """
        if not self.service:
            if not self.authenticate():
                return None

        try:
            # Parse date and times
            schedule_date = datetime.strptime(sleep_schedule.date, '%Y-%m-%d').date()
            sleep_start_time = datetime.strptime(sleep_schedule.sleep_start, '%H:%M').time()
            sleep_end_time = datetime.strptime(sleep_schedule.sleep_end, '%H:%M').time()

            # Create datetime objects
            start_dt = datetime.combine(schedule_date, sleep_start_time)

            # Sleep end is next day
            end_dt = datetime.combine(schedule_date + timedelta(days=1), sleep_end_time)

            # Check for conflicts if requested
            if check_conflicts:
                conflicts = self.check_for_conflicts(start_dt, end_dt)
                if conflicts:
                    print(f"Warning: Found {len(conflicts)} conflicting events")
                    # You could adjust the sleep time here or notify the user
                    # For now, we'll proceed anyway

            # Create event
            event = {
                'summary': 'Sleep Time',
                'description': f'Optimal sleep schedule (Duration: {sleep_schedule.duration_hours} hours)\n'
                              f'Isha: {sleep_schedule.isha_time} | Fajr: {sleep_schedule.fajr_time}\n'
                              f'{sleep_schedule.notes or ""}',
                'start': {
                    'dateTime': start_dt.isoformat(),
                    'timeZone': 'Asia/Tashkent',  # This should be dynamic based on location
                },
                'end': {
                    'dateTime': end_dt.isoformat(),
                    'timeZone': 'Asia/Tashkent',
                },
                'colorId': '9',  # Blue color for sleep events
                'reminders': {
                    'useDefault': False,
                    'overrides': [
                        {'method': 'notification', 'minutes': 30},
                        {'method': 'notification', 'minutes': 10},
                    ],
                },
            }

            created_event = self.service.events().insert(
                calendarId='primary',
                body=event
            ).execute()

            print(f"Sleep block created: {created_event.get('htmlLink')}")
            return created_event.get('id')

        except HttpError as error:
            print(f"An error occurred: {error}")
            return None
        except Exception as e:
            print(f"Error creating sleep block: {e}")
            return None

    def check_for_conflicts(
        self,
        start_time: datetime,
        end_time: datetime
    ) -> List[dict]:
        """
        Check for existing calendar events that conflict with sleep time

        Args:
            start_time: Start of sleep period
            end_time: End of sleep period

        Returns:
            List of conflicting events
        """
        if not self.service:
            if not self.authenticate():
                return []

        try:
            events_result = self.service.events().list(
                calendarId='primary',
                timeMin=start_time.isoformat() + 'Z',
                timeMax=end_time.isoformat() + 'Z',
                singleEvents=True,
                orderBy='startTime'
            ).execute()

            events = events_result.get('items', [])

            # Filter out "Sleep Time" events (our own events)
            conflicts = [
                event for event in events
                if event.get('summary') != 'Sleep Time'
            ]

            return conflicts

        except HttpError as error:
            print(f"An error occurred: {error}")
            return []

    def get_todays_events(self, target_date: Optional[date] = None) -> List[dict]:
        """
        Get all events for a specific day

        Args:
            target_date: Date to get events for (default: today)

        Returns:
            List of events
        """
        if not self.service:
            if not self.authenticate():
                return []

        if target_date is None:
            target_date = date.today()

        try:
            start_of_day = datetime.combine(target_date, datetime.min.time())
            end_of_day = datetime.combine(target_date, datetime.max.time())

            events_result = self.service.events().list(
                calendarId='primary',
                timeMin=start_of_day.isoformat() + 'Z',
                timeMax=end_of_day.isoformat() + 'Z',
                singleEvents=True,
                orderBy='startTime'
            ).execute()

            return events_result.get('items', [])

        except HttpError as error:
            print(f"An error occurred: {error}")
            return []

    def delete_event(self, event_id: str) -> bool:
        """
        Delete a calendar event

        Args:
            event_id: ID of the event to delete

        Returns:
            True if successful, False otherwise
        """
        if not self.service:
            if not self.authenticate():
                return False

        try:
            self.service.events().delete(
                calendarId='primary',
                eventId=event_id
            ).execute()
            return True
        except HttpError as error:
            print(f"An error occurred: {error}")
            return False

    def update_sleep_block(
        self,
        event_id: str,
        sleep_schedule: SleepSchedule
    ) -> bool:
        """
        Update an existing sleep block event

        Args:
            event_id: ID of the event to update
            sleep_schedule: New sleep schedule

        Returns:
            True if successful, False otherwise
        """
        if not self.service:
            if not self.authenticate():
                return False

        try:
            # Get existing event
            event = self.service.events().get(
                calendarId='primary',
                eventId=event_id
            ).execute()

            # Update event details
            schedule_date = datetime.strptime(sleep_schedule.date, '%Y-%m-%d').date()
            sleep_start_time = datetime.strptime(sleep_schedule.sleep_start, '%H:%M').time()
            sleep_end_time = datetime.strptime(sleep_schedule.sleep_end, '%H:%M').time()

            start_dt = datetime.combine(schedule_date, sleep_start_time)
            end_dt = datetime.combine(schedule_date + timedelta(days=1), sleep_end_time)

            event['start'] = {
                'dateTime': start_dt.isoformat(),
                'timeZone': 'Asia/Tashkent',
            }
            event['end'] = {
                'dateTime': end_dt.isoformat(),
                'timeZone': 'Asia/Tashkent',
            }
            event['description'] = f'Optimal sleep schedule (Duration: {sleep_schedule.duration_hours} hours)\n' \
                                  f'Isha: {sleep_schedule.isha_time} | Fajr: {sleep_schedule.fajr_time}\n' \
                                  f'{sleep_schedule.notes or ""}'

            updated_event = self.service.events().update(
                calendarId='primary',
                eventId=event_id,
                body=event
            ).execute()

            print(f"Sleep block updated: {updated_event.get('htmlLink')}")
            return True

        except HttpError as error:
            print(f"An error occurred: {error}")
            return False
