package com.example.plantopedia

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprayingGuidanceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var weatherData by remember { mutableStateOf<WeatherData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadWeather(forceRefresh: Boolean = false) {
        isLoading = true
        coroutineScope.launch {
            val result = WeatherService.getWeatherData(context, forceRefresh = forceRefresh)
            isLoading = false
            if (result.isSuccess) {
                weatherData = result.getOrNull()
            }
        }
    }

    LaunchedEffect(Unit) {
        loadWeather(forceRefresh = false)
    }

    val assessment = remember(weatherData) {
        SprayingGuidanceEngine.evaluate(weatherData, context)
    }

    val location = remember(weatherData) {
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
                                text = stringResource(R.string.spraying_guidance_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Text(
                                text = location.getDisplayName(context),
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = { loadWeather(forceRefresh = true) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh_weather),
                            tint = PrimaryGreen
                        )
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
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
                } else {
                    // Current Spraying Status Hero Banner
                    StatusHeroCard(assessment = assessment)

                    // Explanation Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = CardBackground,
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SecondaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = stringResource(assessment.summaryExplanationResId),
                                style = MaterialTheme.typography.bodyMedium,
                                color = PrimaryText,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Key Weather Factors Section
                    Text(
                        text = stringResource(R.string.factors_affecting_spraying),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        assessment.factors.forEach { factor ->
                            SprayingFactorCard(factor = factor)
                        }
                    }

                    // Upcoming Spraying Conditions Timeline
                    if (assessment.upcomingSlots.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.upcoming_spraying_conditions),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CardBackground,
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                assessment.upcomingSlots.forEach { slot ->
                                    UpcomingSlotRow(slot = slot)
                                }
                            }
                        }
                    }

                    // Important Agricultural Safety Notice
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF9F7F0),
                        border = BorderStroke(1.dp, Color(0xFFE2DBCB))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = stringResource(R.string.agricultural_safety_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }
                            Text(
                                text = stringResource(R.string.agricultural_safety_body),
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
fun StatusHeroCard(
    assessment: SprayingAssessment
) {
    val (bgColor, borderColor, textColor, icon, labelResId) = when (assessment.status) {
        SprayingStatus.SUITABLE -> StatusVisuals(
            bgColor = LightGreenTint,
            borderColor = PrimaryGreen,
            textColor = PrimaryGreen,
            icon = Icons.Default.CheckCircle,
            labelResId = R.string.status_suitable
        )
        SprayingStatus.USE_CAUTION -> StatusVisuals(
            bgColor = Color(0xFFFFF7E6),
            borderColor = Color(0xFFD48806),
            textColor = Color(0xFFB57004),
            icon = Icons.Default.Warning,
            labelResId = R.string.status_caution
        )
        SprayingStatus.NOT_SUITABLE -> StatusVisuals(
            bgColor = Color(0xFFFFF1F0),
            borderColor = Color(0xFFCF1322),
            textColor = Color(0xFFCF1322),
            icon = Icons.Default.Error,
            labelResId = R.string.status_not_suitable
        )
        SprayingStatus.INSUFFICIENT_DATA -> StatusVisuals(
            bgColor = AppBackground,
            borderColor = BorderColor,
            textColor = SecondaryText,
            icon = Icons.Default.Info,
            labelResId = R.string.status_insufficient_data
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Column {
                Text(
                    text = stringResource(R.string.current_spraying_status),
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(labelResId),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

private data class StatusVisuals(
    val bgColor: Color,
    val borderColor: Color,
    val textColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val labelResId: Int
)

@Composable
fun SprayingFactorCard(
    factor: SprayingFactor
) {
    val (ratingText, ratingColor, ratingBg) = when (factor.rating) {
        FactorRating.FAVORABLE -> Triple(
            stringResource(R.string.factor_favorable),
            PrimaryGreen,
            LightGreenTint
        )
        FactorRating.CAUTION -> Triple(
            stringResource(R.string.factor_caution),
            Color(0xFFB57004),
            Color(0xFFFFF7E6)
        )
        FactorRating.UNFAVORABLE -> Triple(
            stringResource(R.string.factor_unfavorable),
            Color(0xFFCF1322),
            Color(0xFFFFF1F0)
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = factor.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ratingBg,
                    border = BorderStroke(1.dp, ratingColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = ratingText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ratingColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Current: ${factor.currentValue}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryText,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Ideal: ${factor.recommendedRange}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }

            Text(
                text = factor.explanation,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun UpcomingSlotRow(
    slot: UpcomingSprayingSlot
) {
    val (statusText, statusColor) = when (slot.status) {
        SprayingStatus.SUITABLE -> stringResource(R.string.status_suitable) to PrimaryGreen
        SprayingStatus.USE_CAUTION -> stringResource(R.string.status_caution) to Color(0xFFB57004)
        SprayingStatus.NOT_SUITABLE -> stringResource(R.string.status_not_suitable) to Color(0xFFCF1322)
        SprayingStatus.INSUFFICIENT_DATA -> stringResource(R.string.status_insufficient_data) to SecondaryText
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = AppBackground,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(slot.titleResId),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = "${slot.timeLabel} • ${slot.temperature} • ${slot.wind}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    fontSize = 11.sp
                )
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
        }
    }
}
