#!/usr/bin/env python3
"""
Sleepy - Prayer-aware sleep schedule optimizer
Main entry point for the application
"""

import sys
import uvicorn
from src.models.database import init_db


def main():
    """Main entry point"""
    print("="* 60)
    print("Sleepy - Prayer-Aware Sleep Schedule Optimizer")
    print("="* 60)

    # Initialize database
    print("\nInitializing database...")
    init_db()
    print("Database initialized successfully!")

    # Start the API server
    print("\nStarting API server on http://localhost:8000")
    print("API documentation available at http://localhost:8000/docs")
    print("\nPress CTRL+C to stop the server\n")

    try:
        uvicorn.run(
            "src.api.main:app",
            host="0.0.0.0",
            port=8000,
            reload=True
        )
    except KeyboardInterrupt:
        print("\nShutting down gracefully...")
        sys.exit(0)


if __name__ == "__main__":
    main()
