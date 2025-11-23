package com.sleepy

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.sleepy.ui.screens.MainScreen
import com.sleepy.ui.theme.GhibliTheme
import com.sleepy.utils.DefaultLocation
import com.sleepy.utils.LocationHelper
import com.sleepy.viewmodel.ScheduleState
import com.sleepy.viewmodel.SleepViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: SleepViewModel by viewModels {
        androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
    private lateinit var locationHelper: LocationHelper

    // Location permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchScheduleWithLocation()
        } else {
            // Use default Tashkent location
            viewModel.fetchSleepSchedule(
                DefaultLocation.TASHKENT_LAT,
                DefaultLocation.TASHKENT_LON
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationHelper = LocationHelper(this)

        // Request location and fetch schedule
        requestLocationAndFetch()

        setContent {
            GhibliTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SleepyApp(viewModel)
                }
            }
        }
    }

    private fun requestLocationAndFetch() {
        if (locationHelper.hasLocationPermission(this)) {
            fetchScheduleWithLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun fetchScheduleWithLocation() {
        lifecycleScope.launch {
            val location = locationHelper.getCurrentLocation(this@MainActivity)
                ?: locationHelper.getLastKnownLocation(this@MainActivity)

            if (location != null) {
                viewModel.fetchSleepSchedule(location.latitude, location.longitude)
            } else {
                // Fallback to Tashkent
                viewModel.fetchSleepSchedule(
                    DefaultLocation.TASHKENT_LAT,
                    DefaultLocation.TASHKENT_LON
                )
            }
        }
    }
}

@Composable
fun SleepyApp(viewModel: SleepViewModel) {
    val scheduleState by viewModel.scheduleState.collectAsState()

    when (val state = scheduleState) {
        is ScheduleState.Loading -> {
            LoadingScreen()
        }
        is ScheduleState.Success -> {
            val data = state.data
            MainScreen(
                sleepTime = data.sleepSchedule.sleepStart,
                wakeTime = data.sleepSchedule.sleepEnd,
                fajrTime = data.prayerTimes.fajr,
                ishaTime = data.prayerTimes.isha,
                duration = data.sleepSchedule.durationHours.toFloat(),
                timeUntilSleep = data.timeUntilSleep,
                quote = data.notificationQuote,
                location = "${data.location.city}, ${data.location.country}"
            )
        }
        is ScheduleState.Error -> {
            ErrorScreen(message = state.message)
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                "Loading sleep schedule...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Connection Error",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Make sure the Python backend is running on http://localhost:8000",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}
