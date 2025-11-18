from typing import Optional
from datetime import datetime
from src.models.prayer_times import LocationInfo
from src.models.database import UserLocation, get_session
from config.settings import DEFAULT_TIMEZONE


class LocationService:
    """Service for managing user location"""

    # List of major cities in Uzbekistan with coordinates
    UZBEKISTAN_CITIES = {
        'Tashkent': (41.2995, 69.2401),
        'Samarkand': (39.6542, 66.9597),
        'Bukhara': (39.7681, 64.4549),
        'Andijan': (40.7821, 72.3442),
        'Namangan': (40.9983, 71.6726),
        'Fergana': (40.3864, 71.7864),
        'Nukus': (42.4531, 59.6103),
        'Karshi': (38.8606, 65.7975),
        'Termez': (37.2242, 67.2783),
        'Urgench': (41.5500, 60.6333),
        'Khiva': (41.3775, 60.3639),
        'Jizzakh': (40.1158, 67.8422),
        'Guliston': (40.4897, 68.7842),
        'Margilan': (40.4708, 71.7247)
    }

    def __init__(self):
        self.session = get_session()

    def get_location_from_gps(self, latitude: float, longitude: float) -> LocationInfo:
        """
        Convert GPS coordinates to location information

        In a real implementation, this would use a reverse geocoding service.
        For now, we'll match to nearest Uzbekistan city or return generic info.

        Args:
            latitude: GPS latitude
            longitude: GPS longitude

        Returns:
            LocationInfo object
        """
        # Check if in Uzbekistan (approximate bounding box)
        is_uzbekistan = (
            37.0 <= latitude <= 45.5 and
            55.5 <= longitude <= 73.2
        )

        if is_uzbekistan:
            # Find nearest city
            city = self._find_nearest_city(latitude, longitude)
            country = "Uzbekistan"
            timezone = "Asia/Tashkent"
        else:
            # For non-Uzbekistan, you'd normally use a geocoding API
            # This is a placeholder
            city = "Unknown"
            country = "Unknown"
            timezone = DEFAULT_TIMEZONE

        location = LocationInfo(
            city=city,
            country=country,
            latitude=latitude,
            longitude=longitude,
            timezone=timezone,
            is_uzbekistan=is_uzbekistan
        )

        # Save to database
        self._save_location(location)

        return location

    def get_location_by_city_name(self, city_name: str) -> Optional[LocationInfo]:
        """
        Get location information by city name

        Args:
            city_name: Name of the city

        Returns:
            LocationInfo object or None if not found
        """
        # Check Uzbekistan cities
        if city_name in self.UZBEKISTAN_CITIES:
            lat, lon = self.UZBEKISTAN_CITIES[city_name]
            return LocationInfo(
                city=city_name,
                country="Uzbekistan",
                latitude=lat,
                longitude=lon,
                timezone="Asia/Tashkent",
                is_uzbekistan=True
            )

        return None

    def get_last_known_location(self) -> Optional[LocationInfo]:
        """
        Retrieve the last known location from database

        Returns:
            LocationInfo object or None if no location saved
        """
        try:
            last_location = self.session.query(UserLocation).order_by(
                UserLocation.last_updated.desc()
            ).first()

            if last_location:
                is_uzbekistan = last_location.country == "Uzbekistan"
                return LocationInfo(
                    city=last_location.city,
                    country=last_location.country,
                    latitude=last_location.latitude,
                    longitude=last_location.longitude,
                    timezone=last_location.timezone,
                    is_uzbekistan=is_uzbekistan
                )
            return None
        except Exception as e:
            print(f"Error retrieving last known location: {e}")
            return None

    def _find_nearest_city(self, latitude: float, longitude: float) -> str:
        """Find nearest Uzbekistan city to given coordinates"""
        min_distance = float('inf')
        nearest_city = "Tashkent"  # Default

        for city, (city_lat, city_lon) in self.UZBEKISTAN_CITIES.items():
            # Simple Euclidean distance (good enough for rough matching)
            distance = ((latitude - city_lat) ** 2 + (longitude - city_lon) ** 2) ** 0.5

            if distance < min_distance:
                min_distance = distance
                nearest_city = city

        return nearest_city

    def _save_location(self, location: LocationInfo):
        """Save location to database"""
        try:
            # Check if location for this city already exists
            existing = self.session.query(UserLocation).filter_by(
                city=location.city
            ).first()

            if existing:
                # Update existing
                existing.latitude = location.latitude
                existing.longitude = location.longitude
                existing.country = location.country
                existing.timezone = location.timezone
                existing.last_updated = datetime.utcnow()
            else:
                # Create new
                user_location = UserLocation(
                    city=location.city,
                    country=location.country,
                    latitude=location.latitude,
                    longitude=location.longitude,
                    timezone=location.timezone
                )
                self.session.add(user_location)

            self.session.commit()
        except Exception as e:
            print(f"Error saving location: {e}")
            self.session.rollback()
