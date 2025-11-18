from sqlalchemy import create_engine, Column, Integer, String, Float, Date, DateTime
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from datetime import datetime
import os
from config.settings import DATABASE_PATH

Base = declarative_base()


class CachedPrayerTimes(Base):
    """Database model for cached prayer times"""
    __tablename__ = "prayer_times_cache"

    id = Column(Integer, primary_key=True, autoincrement=True)
    date = Column(Date, nullable=False, index=True)
    city = Column(String, nullable=False, index=True)
    country = Column(String, nullable=False)
    latitude = Column(Float, nullable=False)
    longitude = Column(Float, nullable=False)
    fajr = Column(String, nullable=False)
    sunrise = Column(String, nullable=False)
    dhuhr = Column(String, nullable=False)
    asr = Column(String, nullable=False)
    maghrib = Column(String, nullable=False)
    isha = Column(String, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)


class UserLocation(Base):
    """Database model for last known user location"""
    __tablename__ = "user_location"

    id = Column(Integer, primary_key=True, autoincrement=True)
    city = Column(String, nullable=False)
    country = Column(String, nullable=False)
    latitude = Column(Float, nullable=False)
    longitude = Column(Float, nullable=False)
    timezone = Column(String, nullable=False)
    last_updated = Column(DateTime, default=datetime.utcnow)


class SleepScheduleHistory(Base):
    """Database model for sleep schedule history"""
    __tablename__ = "sleep_schedule_history"

    id = Column(Integer, primary_key=True, autoincrement=True)
    date = Column(Date, nullable=False, unique=True, index=True)
    sleep_start = Column(String, nullable=False)
    sleep_end = Column(String, nullable=False)
    duration_hours = Column(Float, nullable=False)
    isha_time = Column(String, nullable=False)
    fajr_time = Column(String, nullable=False)
    notes = Column(String, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)


# Database initialization
def init_db():
    """Initialize the database and create tables"""
    # Ensure data directory exists
    os.makedirs(os.path.dirname(DATABASE_PATH), exist_ok=True)

    # Create engine and tables
    engine = create_engine(f"sqlite:///{DATABASE_PATH}", echo=False)
    Base.metadata.create_all(engine)
    return engine


def get_session():
    """Get a database session"""
    engine = init_db()
    Session = sessionmaker(bind=engine)
    return Session()
