package com.example.plantopedia

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantopedia.ui.theme.AppBackground
import com.example.plantopedia.ui.theme.BorderColor
import com.example.plantopedia.ui.theme.CardBackground
import com.example.plantopedia.ui.theme.LightGreenTint
import com.example.plantopedia.ui.theme.PrimaryGreen
import com.example.plantopedia.ui.theme.PrimaryText
import com.example.plantopedia.ui.theme.SecondaryGreen
import com.example.plantopedia.ui.theme.SecondaryText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailsScreen(
    onBack: () -> Unit,
    onSprayingGuidanceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var weatherData by remember { mutableStateOf<WeatherData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showLocationDialog by remember { mutableStateOf(false) }

    fun loadWeather(forceRefresh: Boolean = false) {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            val result = WeatherService.getWeatherData(context, forceRefresh = forceRefresh)
            isLoading = false
            if (result.isSuccess) {
                weatherData = result.getOrNull()
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Unable to fetch weather"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadWeather(forceRefresh = false)
    }

    val currentLocation = remember(weatherData) {
        WeatherService.getSelectedLocation(context)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            Surface(
                color = CardBackground,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = PrimaryGreen
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.weather_details_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Text(
                                text = stringResource(R.string.weather_details_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Refresh button
                        IconButton(onClick = { loadWeather(forceRefresh = true) }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.refresh_weather),
                                tint = PrimaryGreen
                            )
                        }
                    }
                }
            }

            // Body Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Location Bar Pill Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = CardBackground,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = currentLocation.getDisplayName(context),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val updatedTimeStr = weatherData?.let {
                                    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(it.lastUpdatedMillis))
                                } ?: formatCurrentDate(context)
                                Text(
                                    text = stringResource(R.string.last_updated, updatedTimeStr),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SecondaryText,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showLocationDialog = true },
                            color = LightGreenTint,
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Text(
                                text = stringResource(R.string.change_location),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryGreen,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Weather Content or Loading/Error State
                if (isLoading && weatherData == null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = CardBackground,
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryGreen, strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = stringResource(R.string.weather_loading),
                                style = MaterialTheme.typography.bodyMedium,
                                color = SecondaryText
                            )
                        }
                    }
                } else if (weatherData == null && errorMessage != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = CardBackground,
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.weather_error_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.weather_error_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { loadWeather(forceRefresh = true) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                } else if (weatherData != null) {
                    val data = weatherData!!
                    val current = data.current

                    // Hero Current Weather Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = CardBackground,
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = formatCurrentDate(context),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = SecondaryText,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${current.temperature.toInt()}°C",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryText,
                                        fontSize = 44.sp
                                    )
                                    Text(
                                        text = stringResource(R.string.feels_like, "${current.apparentTemperature.toInt()}°C"),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SecondaryText
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val icon = if (current.weatherCode in listOf(0, 1)) {
                                        Icons.Default.WbSunny
                                    } else {
                                        Icons.Default.Cloud
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (current.weatherCode in listOf(0, 1)) Color(0xFFE67E22) else Color(0xFF8FA7B2),
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = stringResource(WeatherService.getWeatherConditionResId(current.weatherCode)),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryText,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Grid of Key Metrics (Humidity, Wind, Rain, Rain Prob)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                WeatherMetricItem(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.WaterDrop,
                                    label = stringResource(R.string.humidity_label),
                                    value = "${current.relativeHumidity}%"
                                )
                                WeatherMetricItem(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.Air,
                                    label = stringResource(R.string.wind_label),
                                    value = "${current.windSpeed} km/h (${WeatherService.getWindDirectionCardinal(current.windDirection)})"
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val currentRainProb = data.hourly.firstOrNull()?.precipitationProbability ?: 0
                                WeatherMetricItem(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.Cloud,
                                    label = stringResource(R.string.rain_prob_label),
                                    value = "$currentRainProb%"
                                )
                                WeatherMetricItem(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.WaterDrop,
                                    label = stringResource(R.string.rainfall_label),
                                    value = "${current.precipitation} mm"
                                )
                            }
                        }
                    }

                    // Direct shortcut to Spraying Guidance
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSprayingGuidanceClick() },
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFE8F3EC),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.spraying_conditions),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SecondaryText
                                )
                                Text(
                                    text = stringResource(R.string.view_spraying_guidance),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }
                        }
                    }

                    // Hourly Forecast Section
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = CardBackground,
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.hourly_forecast),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                data.hourly.forEach { item ->
                                    HourlyForecastChip(item = item)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Location Change Dialog
    if (showLocationDialog) {
        LocationSelectionDialog(
            currentLocation = currentLocation,
            onDismiss = { showLocationDialog = false },
            onSelectLocation = { newLoc, isGps ->
                WeatherService.setSelectedLocation(context, newLoc, isGps)
                showLocationDialog = false
                loadWeather(forceRefresh = true)
            }
        )
    }
}

@Composable
fun WeatherMetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = AppBackground,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = LightGreenTint,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText,
                    fontSize = 10.sp,
                    maxLines = 1
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun HourlyForecastChip(
    item: HourlyForecastItem
) {
    Surface(
        modifier = Modifier.width(76.dp),
        shape = RoundedCornerShape(12.dp),
        color = AppBackground,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.formattedHour,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = SecondaryText
            )

            val icon = if (item.weatherCode in listOf(0, 1)) {
                Icons.Default.WbSunny
            } else {
                Icons.Default.Cloud
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (item.weatherCode in listOf(0, 1)) Color(0xFFE67E22) else Color(0xFF8FA7B2),
                modifier = Modifier.size(22.dp)
            )

            Text(
                text = "${item.temperature.toInt()}°C",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )

            if (item.precipitationProbability > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF2980B9),
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "${item.precipitationProbability}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2980B9),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Text(
                    text = "${item.windSpeed.toInt()} km/h",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionDialog(
    currentLocation: LocationInfo,
    onDismiss: () -> Unit,
    onSelectLocation: (LocationInfo, Boolean) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = CardBackground,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_location_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )

                // GPS Option
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val gpsLoc = WeatherService.detectDeviceLocation(context)
                            if (gpsLoc != null) {
                                onSelectLocation(gpsLoc, true)
                            } else {
                                // Default or fallback if gps disabled/unavailable
                                onSelectLocation(currentLocation, false)
                            }
                        },
                    color = LightGreenTint,
                    border = BorderStroke(1.dp, PrimaryGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.use_gps_location),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }
                }

                // Preset Agricultural Locations
                Text(
                    text = "Agricultural Regions",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryText
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    defaultAgriculturalLocations.forEach { loc ->
                        val isSelected = loc.id == currentLocation.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSelectLocation(loc, false) },
                            color = if (isSelected) LightGreenTint else AppBackground,
                            border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else BorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = loc.getDisplayName(context),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = PrimaryText
                                )
                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        color = PrimaryGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    }
}
