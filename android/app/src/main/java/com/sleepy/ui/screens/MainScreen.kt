package com.sleepy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sleepy.ui.components.OrganicSectionDivider
import com.sleepy.ui.components.PaintedText
import com.sleepy.ui.components.TexturedBackground
import com.sleepy.ui.theme.GhibliTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Main screen - Full-screen, text-focused layout
 * Inspired by Twitter/X's information density and readability
 */
@Composable
fun MainScreen(
    sleepTime: String = "19:30",
    wakeTime: String = "04:00",
    fajrTime: String = "05:30",
    ishaTime: String = "19:00",
    duration: Float = 7.0f,
    timeUntilSleep: String? = "3 hours 15 minutes",
    quote: String = "Your brain cells are begging for a reboot. Give them what they deserve!",
    location: String = "Tashkent, Uzbekistan"
) {
    TexturedBackground(enableWatercolor = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp), // Comfortable reading margins
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Current time + location (small, top)
            CurrentTimeHeader(location = location)

            Spacer(modifier = Modifier.height(48.dp))

            // Sleep time (LARGE, prominent)
            SleepTimeSection(
                sleepTime = sleepTime,
                timeUntilSleep = timeUntilSleep
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Prayer times (medium, grouped)
            PrayerTimesSection(
                fajrTime = fajrTime,
                ishaTime = ishaTime
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Wake time and duration
            WakeTimeSection(
                wakeTime = wakeTime,
                duration = duration
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Organic divider
            OrganicSectionDivider(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(2.dp)
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Quote section (full-width text)
            QuoteSection(quote = quote)

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun CurrentTimeHeader(location: String) {
    val currentTime = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentTime,
            style = MaterialTheme.typography.titleMedium,
            color = GhibliTheme.colors.textTertiary,
            fontWeight = FontWeight.Light
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = location,
            style = MaterialTheme.typography.labelMedium,
            color = GhibliTheme.colors.textTertiary.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SleepTimeSection(
    sleepTime: String,
    timeUntilSleep: String?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Label
        Text(
            text = "SLEEP TIME",
            style = MaterialTheme.typography.labelLarge,
            color = GhibliTheme.colors.textSecondary,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main sleep time (HUGE)
        PaintedText(
            text = sleepTime,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Light
            ),
            color = GhibliTheme.colors.sleepTimeColor,
            shadowIntensity = 0.2f
        )

        // Countdown
        if (timeUntilSleep != null) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "in $timeUntilSleep",
                style = MaterialTheme.typography.bodyLarge,
                color = GhibliTheme.colors.textSecondary,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun PrayerTimesSection(
    fajrTime: String,
    ishaTime: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Isha (evening prayer)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Isha",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Normal
            )

            Text(
                text = ishaTime,
                style = MaterialTheme.typography.headlineMedium,
                color = GhibliTheme.colors.ishaColor,
                fontWeight = FontWeight.Light
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Fajr (morning prayer)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fajr",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Normal
            )

            Text(
                text = fajrTime,
                style = MaterialTheme.typography.headlineMedium,
                color = GhibliTheme.colors.fajrColor,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun WakeTimeSection(
    wakeTime: String,
    duration: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wake time
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "WAKE",
                    style = MaterialTheme.typography.labelMedium,
                    color = GhibliTheme.colors.textTertiary,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = wakeTime,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Light
                )
            }

            // Duration
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "DURATION",
                    style = MaterialTheme.typography.labelMedium,
                    color = GhibliTheme.colors.textTertiary,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${duration}h",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

@Composable
private fun QuoteSection(quote: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = quote,
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 36.sp  // Extra generous for quote
            ),
            color = GhibliTheme.colors.textSecondary,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "— Sleepy",
            style = MaterialTheme.typography.bodyMedium,
            color = GhibliTheme.colors.textTertiary,
            fontWeight = FontWeight.Light
        )
    }
}
