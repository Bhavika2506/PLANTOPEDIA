package com.example.plantopedia

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.plantopedia.ui.theme.DarkForestGreen
import com.example.plantopedia.ui.theme.EarthBorder
import com.example.plantopedia.ui.theme.MutedSlateText
import com.example.plantopedia.ui.theme.NearBlackText
import com.example.plantopedia.ui.theme.SageGreenTint
import com.example.plantopedia.ui.theme.StatusSuccess
import com.example.plantopedia.ui.theme.WarmBeigeBackground
import com.example.plantopedia.ui.theme.WarmWhiteSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    val context = LocalContext.current

    var historyItems by remember {
        mutableStateOf(
            ScanHistory.getAll(context)
        )
    }

    var selectedItemForDetail by remember {
        mutableStateOf<ScanHistoryItem?>(null)
    }

    LaunchedEffect(Unit) {
        historyItems = ScanHistory.getAll(context)
    }

    val todayItems = historyItems.filter { isToday(it.timestamp) }
    val earlierItems = historyItems.filter { !isToday(it.timestamp) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = WarmBeigeBackground // Warm beige background (#E9E2D5)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.scan_history_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = NearBlackText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(id = R.string.scan_history_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedSlateText
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (historyItems.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = WarmWhiteSurface,
                        border = BorderStroke(1.dp, EarthBorder),
                        shadowElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(38.dp))
                                    .background(SageGreenTint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = DarkForestGreen,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = stringResource(id = R.string.no_scans_yet),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = NearBlackText
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = stringResource(id = R.string.no_scans_yet_desc),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MutedSlateText
                            )
                        }
                    }
                }
            } else {
                if (todayItems.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.history_today),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkForestGreen
                        )
                    }

                    items(todayItems) { item ->
                        HistoryCard(
                            item = item,
                            onClick = { selectedItemForDetail = item }
                        )
                    }
                }

                if (earlierItems.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(id = R.string.history_earlier),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkForestGreen
                        )
                    }

                    items(earlierItems) { item ->
                        HistoryCard(
                            item = item,
                            onClick = { selectedItemForDetail = item }
                        )
                    }
                }
            }
        }
    }

    // Detail Dialog for farmer review
    if (selectedItemForDetail != null) {
        val item = selectedItemForDetail!!
        val diseaseInfo = DiseaseDatabase.get(item.disease, context)
        val displayCrop = diseaseInfo?.crop ?: item.crop
        val displayDisease = diseaseInfo?.disease ?: formatLabel(item.disease)

        BasicAlertDialog(
            onDismissRequest = { selectedItemForDetail = null }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = WarmWhiteSurface,
                border = BorderStroke(1.dp, EarthBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayCrop,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = NearBlackText
                        )
                        IconButton(onClick = { selectedItemForDetail = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MutedSlateText
                            )
                        }
                    }

                    Text(
                        text = displayDisease,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkForestGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${(item.confidence * 100).toInt()}% Confidence",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = StatusSuccess
                        )
                    }

                    if (diseaseInfo != null) {
                        Spacer(modifier = Modifier.height(14.dp))

                        DetailSection(
                            icon = Icons.Default.Search,
                            title = stringResource(id = R.string.symptoms_title),
                            content = diseaseInfo.symptoms
                        )

                        DetailSection(
                            icon = Icons.Default.MedicalServices,
                            title = stringResource(id = R.string.treatment_title),
                            content = diseaseInfo.treatment
                        )

                        DetailSection(
                            icon = Icons.Default.Shield,
                            title = stringResource(id = R.string.prevention_title),
                            content = diseaseInfo.prevention
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { selectedItemForDetail = null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.close),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DarkForestGreen,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkForestGreen
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = NearBlackText
        )
    }
}

@Composable
fun HistoryCard(
    item: ScanHistoryItem,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val emoji = getCropEmoji(item.crop)
    val diseaseInfo = DiseaseDatabase.get(item.disease, context)

    val displayCrop = diseaseInfo?.crop ?: item.crop
    val displayDisease = diseaseInfo?.disease ?: formatLabel(item.disease)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = WarmWhiteSurface, // #FAF8F2
        border = BorderStroke(1.dp, EarthBorder), // #D1C9BC
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Plant / Crop Thumbnail container
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SageGreenTint),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                // Bold Disease Title
                Text(
                    text = displayDisease,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NearBlackText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Confidence badge matching Reference Screen 2 & 4
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusSuccess,
                        modifier = Modifier.size(14.dp)
                    )

                    Text(
                        text = stringResource(
                            id = R.string.history_confidence_format,
                            displayCrop,
                            item.confidence * 100f
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusSuccess,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Date & Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MutedSlateText,
                        modifier = Modifier.size(13.dp)
                    )

                    Text(
                        text = formatTime(item.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MutedSlateText
                    )
                }
            }

            // Right chevron (>) matching Reference Screen 2
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MutedSlateText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Retain overloaded HistoryItem for backward compatibility
@Composable
fun HistoryItem(
    item: ScanHistoryItem,
    darkGreen: Color = DarkForestGreen,
    orange: Color = StatusSuccess
) {
    HistoryCard(item = item)
}

fun isToday(timestamp: Long): Boolean {
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val today = dateFormat.format(Date())
    val itemDate = dateFormat.format(Date(timestamp))
    return today == itemDate
}

fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault()).format(Date(timestamp))
}

fun getCropEmoji(crop: String): String {
    return when {
        crop.contains("Tomato", ignoreCase = true) -> "🍅"
        crop.contains("Potato", ignoreCase = true) -> "🥔"
        crop.contains("Apple", ignoreCase = true) -> "🍎"
        crop.contains("Grape", ignoreCase = true) -> "🍇"
        crop.contains("Corn", ignoreCase = true) -> "🌽"
        crop.contains("Pepper", ignoreCase = true) -> "🌶️"
        else -> "🌿"
    }
}