# Google Calendar API Setup Guide

Follow these steps to set up Google Calendar integration for Sleepy.

## Step 1: Create a Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click on the project dropdown at the top
3. Click **New Project**
4. Enter project name: "Sleepy App" (or any name you prefer)
5. Click **Create**

## Step 2: Enable Google Calendar API

1. In your new project, go to **APIs & Services** > **Library**
2. Search for "Google Calendar API"
3. Click on it and press **Enable**

## Step 3: Configure OAuth Consent Screen

1. Go to **APIs & Services** > **OAuth consent screen**
2. Select **External** (unless you have a Google Workspace account)
3. Click **Create**
4. Fill in the required fields:
   - App name: "Sleepy"
   - User support email: your email
   - Developer contact email: your email
5. Click **Save and Continue**
6. On the Scopes page, click **Add or Remove Scopes**
7. Search for "Google Calendar API" and select:
   - `.../auth/calendar` (See, edit, share, and permanently delete all calendars)
8. Click **Update** then **Save and Continue**
9. On Test users page, add your Gmail address as a test user
10. Click **Save and Continue**

## Step 4: Create OAuth 2.0 Credentials

1. Go to **APIs & Services** > **Credentials**
2. Click **Create Credentials** > **OAuth client ID**
3. Application type: **Desktop app**
4. Name: "Sleepy Desktop Client"
5. Click **Create**
6. Click **Download JSON** on the popup dialog
7. Save the downloaded file as `credentials.json` in the `config/` directory of your Sleepy project

## Step 5: Verify Setup

Your `config/` directory should now contain:
```
config/
├── __init__.py
├── settings.py
├── credentials.json       ← Downloaded from Google Cloud Console
├── token.json            ← Will be created automatically on first run
└── GOOGLE_SETUP.md       ← This file
```

## Step 6: First-Time Authentication

1. Run the Sleepy application:
   ```bash
   python main.py
   ```

2. The first time you use Google Calendar features, a browser window will open
3. Sign in with your Google account
4. Click **Allow** to grant permissions
5. The `token.json` file will be created automatically
6. Future runs will use the saved token

## Troubleshooting

### "Error: credentials.json not found"
- Make sure you downloaded the credentials file from Google Cloud Console
- Verify it's named exactly `credentials.json` (not `client_secret_xxx.json`)
- Ensure it's in the `config/` directory

### "Access blocked: This app hasn't been verified"
- Click **Advanced** > **Go to Sleepy (unsafe)**
- This is normal for apps in testing mode
- Once you publish the app, you can go through Google's verification process

### Token expired
- Delete `token.json`
- Run the app again to re-authenticate

## Security Notes

- **Never commit `credentials.json` or `token.json` to Git!**
- These files are already in `.gitignore`
- Keep these files secure - they provide access to your Google Calendar
- For production deployment, use service accounts instead

## For Android App

For the Android app, you'll need to create additional OAuth credentials:
1. Go back to **Credentials**
2. Create another OAuth client ID
3. This time select **Android** as the application type
4. Follow Android-specific setup instructions in `ANDROID_GUIDE.md`

## API Quota

Google Calendar API has the following quotas:
- 1,000,000 queries per day
- 10 queries per second per user

For personal use, this is more than enough!

## Additional Resources

- [Google Calendar API Documentation](https://developers.google.com/calendar/api/guides/overview)
- [OAuth 2.0 for Desktop Apps](https://developers.google.com/identity/protocols/oauth2/native-app)
- [Python Quickstart](https://developers.google.com/calendar/api/quickstart/python)
