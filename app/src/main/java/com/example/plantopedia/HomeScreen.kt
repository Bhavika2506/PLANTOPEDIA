package com.example.plantopedia

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

data class CropOption(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val nameMr: String,
    val emoji: String
)

val availableCropOptions = listOf(
    CropOption("Tomato", "Tomato", "टमाटर", "टोमॅटो", "🍅"),
    CropOption("Rice", "Rice", "चावल", "तांदूळ", "🌾"),
    CropOption("Wheat", "Wheat", "गेहूं", "गहू", "🌾"),
    CropOption("Cotton", "Cotton", "कपास", "कापूस", "☁️"),
    CropOption("Sugarcane", "Sugarcane", "गन्ना", "उस", "🎋"),
    CropOption("Potato", "Potato", "आलू", "बटाटा", "🥔"),
    CropOption("Apple", "Apple", "सेब", "सफरचंद", "🍎"),
    CropOption("Corn", "Corn", "मक्का", "मका", "🌽")
)

fun getCropDisplayName(cropId: String, context: Context): String {
    val lang = UserManager.getLanguage(context)
    val option = availableCropOptions.find { it.id.equals(cropId, ignoreCase = true) } ?: return cropId
    return when (lang) {
        "hi" -> option.nameHi
        "mr" -> option.nameMr
        else -> option.nameEn
    }
}

fun formatCurrentDate(lang: String, date: Date = Date()): String {
    val locale = when (lang) {
        "hi" -> Locale.forLanguageTag("hi-IN")
        "mr" -> Locale.forLanguageTag("mr-IN")
        else -> Locale.ENGLISH
    }
    return try {
        val formatter = SimpleDateFormat("d MMM", locale)
        formatter.format(date)
    } catch (_: Exception) {
        SimpleDateFormat("d MMM", Locale.ENGLISH).format(date)
    }
}

fun formatCurrentDate(context: Context, date: Date = Date()): String {
    val lang = UserManager.getLanguage(context)
    return formatCurrentDate(lang, date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    cameraPermissionGranted: Boolean,
    onScanClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onLanguageChanged: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onAccountClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showCropDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }

    var selectedCrops by remember {
        mutableStateOf(UserManager.getSelectedCrops(context))
    }

    val recentScans = remember {
        ScanHistory.getAll(context).take(2)
    }

    val currentLang = UserManager.getLanguage(context)
    val langLabel = when (currentLang) {
        "hi" -> "हिंदी"
        "mr" -> "मराठी"
        else -> "EN"
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // COMPACT TOP HEADER: [Account Icon] Agromedic ... [Language Selector]
            Surface(
                color = CardBackground,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Clickable Agromedic Account/Profile Icon (min touch target: 48x48 dp)
                        Box(
                            modifier = Modifier
                                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                                .clickable(
                                    onClick = {
                                        showAccountDialog = true
                                        onAccountClick()
                                    },
                                    onClickLabel = stringResource(id = R.string.username)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = LightGreenTint,
                                border = BorderStroke(1.dp, BorderColor),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.Person,
                                        contentDescription = stringResource(id = R.string.username),
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    // Language Selector and Menu
                    Box(
                        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
                    ) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showMenu = true },
                            color = LightGreenTint,
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = stringResource(R.string.select_language),
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = langLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("English") },
                                onClick = {
                                    showMenu = false
                                    UserManager.setLanguage(context, "en")
                                    onLanguageChanged("en")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("हिंदी") },
                                onClick = {
                                    showMenu = false
                                    UserManager.setLanguage(context, "hi")
                                    onLanguageChanged("hi")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("मराठी") },
                                onClick = {
                                    showMenu = false
                                    UserManager.setLanguage(context, "mr")
                                    onLanguageChanged("mr")
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(R.string.logout),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    UserManager.logout(context)
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            }

            // MAIN SCROLLABLE CONTENT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // MY CROPS SECTION (Clean circular plant avatars with names underneath)
                MyCropsRowSection(
                    selectedCrops = selectedCrops,
                    onManageCropsClick = { showCropDialog = true }
                )

                // COMBINED WEATHER AND CHECK MY PLANT SECTION (Plantix inspired unified container)
                CombinedWeatherAndCheckPlantSection(
                    onScanClick = onScanClick
                )

                // RECENT SCANS SECTION
                RecentScansPreviewSection(
                    recentScans = recentScans,
                    onViewAllClick = onHistoryClick
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // Crop Selection Dialog
    if (showCropDialog) {
        CropSelectionDialog(
            initialSelectedCrops = selectedCrops,
            onDismiss = { showCropDialog = false },
            onSave = { newSelection ->
                selectedCrops = newSelection
                UserManager.saveSelectedCrops(context, newSelection)
                showCropDialog = false
            }
        )
    }

    // Account / Profile Details Dialog
    if (showAccountDialog) {
        AccountDetailsDialog(
            onDismiss = { showAccountDialog = false },
            onLogout = {
                showAccountDialog = false
                UserManager.logout(context)
                onLogout()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsDialog(
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val isGuest = UserManager.isGuest(context)
    val username = if (isGuest) stringResource(id = R.string.guest_user) else UserManager.getUsername(context)
    val currentLang = UserManager.getLanguage(context)
    val langLabel = when (currentLang) {
        "hi" -> "हिंदी (Hindi)"
        "mr" -> "मराठी (Marathi)"
        else -> "English (EN)"
    }

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
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close),
                            tint = SecondaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightGreenTint, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryGreen,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = username,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = if (isGuest) stringResource(id = R.string.continue_as_guest) else stringResource(id = R.string.register_title),
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppBackground, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = R.string.preferred_language),
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                        Text(
                            text = langLabel,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onLogout,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.logout),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.close),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Clean circular crop avatars in a horizontally scrolling strip.
 * Features a circular '+' Add/Edit button.
 */
@Composable
fun MyCropsRowSection(
    selectedCrops: Set<String>,
    onManageCropsClick: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.my_crops),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )

                Text(
                    text = if (selectedCrops.isEmpty()) stringResource(R.string.select_your_crops) else stringResource(R.string.crop_selection_title),
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onManageCropsClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Add / Edit Button Circle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onManageCropsClick() }
                        .padding(horizontal = 4.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = LightGreenTint,
                        border = BorderStroke(1.5.dp, PrimaryGreen)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_crop),
                                tint = PrimaryGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.add_crop),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryGreen,
                        maxLines = 1
                    )
                }

                if (selectedCrops.isEmpty()) {
                    // Quick default crop suggestions if none selected
                    availableCropOptions.take(4).forEach { option ->
                        CircularCropItem(
                            emoji = option.emoji,
                            displayName = getCropDisplayName(option.id, context),
                            onClick = onManageCropsClick
                        )
                    }
                } else {
                    selectedCrops.forEach { cropId ->
                        val option = availableCropOptions.find { it.id.equals(cropId, ignoreCase = true) }
                        val emoji = option?.emoji ?: "🌿"
                        val displayName = getCropDisplayName(cropId, context)

                        CircularCropItem(
                            emoji = emoji,
                            displayName = displayName,
                            onClick = onManageCropsClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CircularCropItem(
    emoji: String,
    displayName: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(62.dp)
            .clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = LightGreenTint,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = emoji,
                    fontSize = 26.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = displayName,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = PrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Combined Weather and Check My Plant unified container.
 * Inspired by Plantix layout: compact weather & spraying conditions at top,
 * clean 3-step visual diagnosis diagram in the middle, and full-width CTA button below.
 */
@Composable
fun CombinedWeatherAndCheckPlantSection(
    onScanClick: () -> Unit
) {
    val context = LocalContext.current
    val currentDateStr = remember(context) {
        formatCurrentDate(context)
    }

    // Unified container with soft mint background
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFE8F3EC), // Soft mint green tint matching the theme
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Section: Compact Weather & Spraying Conditions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Weather Pill: Date, Temperature, Weather icon
                Surface(
                    modifier = Modifier.weight(1.1f),
                    shape = RoundedCornerShape(16.dp),
                    color = CardBackground,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = currentDateStr,
                                style = MaterialTheme.typography.labelMedium,
                                color = SecondaryText,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.weather_temp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = stringResource(R.string.weather_condition),
                            tint = Color(0xFF8FA7B2),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Spraying Conditions Pill
                Surface(
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(16.dp),
                    color = CardBackground,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.spraying_conditions),
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.spraying_optimal),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                        }

                        Text(
                            text = stringResource(R.string.spraying_rest_of_day),
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryText,
                            textAlign = TextAlign.End,
                            maxLines = 2
                        )
                    }
                }
            }

            // Bottom Section: White Card with 3-Step Diagram and CTA Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = CardBackground,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Three-step visual process diagram
                    ProcessDiagramRow(
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Prominent full-width button
                    Button(
                        onClick = onScanClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.check_my_plant),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Visual three-step diagram connecting Step 1, Step 2, and Step 3 with right arrows (→).
 */
@Composable
fun ProcessDiagramRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step 1: Take a Picture
        DiagramStepItem(
            modifier = Modifier.weight(1f),
            illustration = { PhoneScanningIllustration(modifier = Modifier.size(56.dp)) },
            label = stringResource(id = R.string.step_take_picture)
        )

        // Arrow 1
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = BorderColor,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .size(18.dp)
        )

        // Step 2: See Diagnosis
        DiagramStepItem(
            modifier = Modifier.weight(1f),
            illustration = { PlantDiagnosisIllustration(modifier = Modifier.size(56.dp)) },
            label = stringResource(id = R.string.step_see_diagnosis)
        )

        // Arrow 2
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = BorderColor,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .size(18.dp)
        )

        // Step 3: Get Treatment
        DiagramStepItem(
            modifier = Modifier.weight(1f),
            illustration = { SprayTreatmentIllustration(modifier = Modifier.size(56.dp)) },
            label = stringResource(id = R.string.step_get_treatment)
        )
    }
}

@Composable
fun DiagramStepItem(
    illustration: @Composable () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(58.dp),
            contentAlignment = Alignment.Center
        ) {
            illustration()
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = PrimaryText,
            textAlign = TextAlign.Center,
            maxLines = 2,
            minLines = 2,
            lineHeight = 15.sp
        )
    }
}

/**
 * Clean illustration of a smartphone scanning a plant leaf with crop/reticle corners.
 */
@Composable
fun PhoneScanningIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Phone body
        val phoneLeft = w * 0.22f
        val phoneTop = h * 0.08f
        val phoneWidth = w * 0.56f
        val phoneHeight = h * 0.84f
        val phoneCorner = 8.dp.toPx()

        drawRoundRect(
            color = Color(0xFF2C3E50),
            topLeft = Offset(phoneLeft, phoneTop),
            size = Size(phoneWidth, phoneHeight),
            cornerRadius = CornerRadius(phoneCorner, phoneCorner),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Screen area
        drawRoundRect(
            color = Color(0xFFF7FAF8),
            topLeft = Offset(phoneLeft + 4.dp.toPx(), phoneTop + 6.dp.toPx()),
            size = Size(phoneWidth - 8.dp.toPx(), phoneHeight - 12.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Fill
        )

        // Leaf inside screen
        val stemPath = Path().apply {
            moveTo(w * 0.5f, h * 0.72f)
            quadraticTo(w * 0.5f, h * 0.45f, w * 0.5f, h * 0.35f)
        }
        drawPath(
            path = stemPath,
            color = Color(0xFF388E3C),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Top Leaf
        drawOval(
            color = Color(0xFF4CAF50),
            topLeft = Offset(w * 0.44f, h * 0.26f),
            size = Size(w * 0.12f, h * 0.16f)
        )

        // Left Leaf
        val leftLeaf = Path().apply {
            moveTo(w * 0.5f, h * 0.48f)
            quadraticTo(w * 0.36f, h * 0.42f, w * 0.38f, h * 0.36f)
            quadraticTo(w * 0.46f, h * 0.38f, w * 0.5f, h * 0.48f)
        }
        drawPath(path = leftLeaf, color = Color(0xFF43A047), style = Fill)

        // Right Leaf
        val rightLeaf = Path().apply {
            moveTo(w * 0.5f, h * 0.52f)
            quadraticTo(w * 0.64f, h * 0.46f, w * 0.62f, h * 0.40f)
            quadraticTo(w * 0.54f, h * 0.42f, w * 0.5f, h * 0.52f)
        }
        drawPath(path = rightLeaf, color = Color(0xFF43A047), style = Fill)

        // Viewfinder reticle corners (top-left, top-right, bottom-left, bottom-right)
        val bracketColor = Color(0xFF2E7D32)
        val bStroke = 1.8.dp.toPx()
        val bLen = 5.dp.toPx()

        // Top-left
        drawLine(bracketColor, Offset(w * 0.33f, h * 0.30f), Offset(w * 0.33f + bLen, h * 0.30f), bStroke, StrokeCap.Round)
        drawLine(bracketColor, Offset(w * 0.33f, h * 0.30f), Offset(w * 0.33f, h * 0.30f + bLen), bStroke, StrokeCap.Round)

        // Top-right
        drawLine(bracketColor, Offset(w * 0.67f, h * 0.30f), Offset(w * 0.67f - bLen, h * 0.30f), bStroke, StrokeCap.Round)
        drawLine(bracketColor, Offset(w * 0.67f, h * 0.30f), Offset(w * 0.67f, h * 0.30f + bLen), bStroke, StrokeCap.Round)

        // Bottom-left
        drawLine(bracketColor, Offset(w * 0.33f, h * 0.68f), Offset(w * 0.33f + bLen, h * 0.68f), bStroke, StrokeCap.Round)
        drawLine(bracketColor, Offset(w * 0.33f, h * 0.68f), Offset(w * 0.33f, h * 0.68f - bLen), bStroke, StrokeCap.Round)

        // Bottom-right
        drawLine(bracketColor, Offset(w * 0.67f, h * 0.68f), Offset(w * 0.67f - bLen, h * 0.68f), bStroke, StrokeCap.Round)
        drawLine(bracketColor, Offset(w * 0.67f, h * 0.68f), Offset(w * 0.67f, h * 0.68f - bLen), bStroke, StrokeCap.Round)
    }
}

/**
 * Clean illustration of a plant with diagnosis check indicator badge.
 */
@Composable
fun PlantDiagnosisIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Stem
        drawLine(
            color = Color(0xFF2E7D32),
            start = Offset(w * 0.5f, h * 0.88f),
            end = Offset(w * 0.5f, h * 0.22f),
            strokeWidth = 3.2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Top central leaf
        val topLeaf = Path().apply {
            moveTo(w * 0.5f, h * 0.24f)
            quadraticTo(w * 0.42f, h * 0.12f, w * 0.5f, h * 0.06f)
            quadraticTo(w * 0.58f, h * 0.12f, w * 0.5f, h * 0.24f)
        }
        drawPath(path = topLeaf, color = Color(0xFF43A047), style = Fill)

        // Upper Left Leaf
        val upperLeft = Path().apply {
            moveTo(w * 0.5f, h * 0.36f)
            quadraticTo(w * 0.30f, h * 0.28f, w * 0.26f, h * 0.18f)
            quadraticTo(w * 0.40f, h * 0.20f, w * 0.5f, h * 0.36f)
        }
        drawPath(path = upperLeft, color = Color(0xFF4CAF50), style = Fill)

        // Upper Right Leaf
        val upperRight = Path().apply {
            moveTo(w * 0.5f, h * 0.36f)
            quadraticTo(w * 0.70f, h * 0.28f, w * 0.74f, h * 0.18f)
            quadraticTo(w * 0.60f, h * 0.20f, w * 0.5f, h * 0.36f)
        }
        drawPath(path = upperRight, color = Color(0xFF4CAF50), style = Fill)

        // Mid Left Leaf
        val midLeft = Path().apply {
            moveTo(w * 0.5f, h * 0.54f)
            quadraticTo(w * 0.24f, h * 0.48f, w * 0.20f, h * 0.38f)
            quadraticTo(w * 0.36f, h * 0.38f, w * 0.5f, h * 0.54f)
        }
        drawPath(path = midLeft, color = Color(0xFF388E3C), style = Fill)

        // Mid Right Leaf
        val midRight = Path().apply {
            moveTo(w * 0.5f, h * 0.54f)
            quadraticTo(w * 0.76f, h * 0.48f, w * 0.80f, h * 0.38f)
            quadraticTo(w * 0.64f, h * 0.38f, w * 0.5f, h * 0.54f)
        }
        drawPath(path = midRight, color = Color(0xFF388E3C), style = Fill)

        // Circular diagnosis check badge on lower right
        val badgeCenter = Offset(w * 0.68f, h * 0.72f)
        val badgeRadius = w * 0.20f

        // Badge background
        drawCircle(
            color = Color(0xFF1E88E5), // Blue diagnosis check badge matching reference
            radius = badgeRadius,
            center = badgeCenter
        )

        // White checkmark inside badge
        val checkPath = Path().apply {
            moveTo(badgeCenter.x - badgeRadius * 0.48f, badgeCenter.y + badgeRadius * 0.02f)
            lineTo(badgeCenter.x - badgeRadius * 0.12f, badgeCenter.y + badgeRadius * 0.38f)
            lineTo(badgeCenter.x + badgeRadius * 0.52f, badgeCenter.y - badgeRadius * 0.36f)
        }
        drawPath(
            path = checkPath,
            color = Color.White,
            style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

/**
 * Clean illustration of a medicine spray bottle with nozzle and spray droplets.
 */
@Composable
fun SprayTreatmentIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Spray Bottle Body (purple/violet shade inspired by Plantix reference)
        val bodyLeft = w * 0.28f
        val bodyTop = h * 0.42f
        val bodyWidth = w * 0.42f
        val bodyHeight = h * 0.50f
        val bodyRadius = 6.dp.toPx()

        drawRoundRect(
            color = Color(0xFF8E6FB7),
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyWidth, bodyHeight),
            cornerRadius = CornerRadius(bodyRadius, bodyRadius),
            style = Fill
        )

        // Highlight stripe on bottle
        drawRoundRect(
            color = Color(0xFFA58BCB),
            topLeft = Offset(bodyLeft + 3.dp.toPx(), bodyTop + 4.dp.toPx()),
            size = Size(bodyWidth * 0.25f, bodyHeight - 8.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Fill
        )

        // Neck
        drawRect(
            color = Color(0xFFCBD5E1),
            topLeft = Offset(w * 0.40f, h * 0.32f),
            size = Size(w * 0.18f, h * 0.10f),
            style = Fill
        )

        // Spray Head
        val headPath = Path().apply {
            moveTo(w * 0.22f, h * 0.32f)
            lineTo(w * 0.30f, h * 0.18f)
            lineTo(w * 0.62f, h * 0.18f)
            lineTo(w * 0.64f, h * 0.26f)
            lineTo(w * 0.56f, h * 0.32f)
            close()
        }
        drawPath(path = headPath, color = Color(0xFF94A3B8), style = Fill)

        // Nozzle tip (extending to the right)
        drawRect(
            color = Color(0xFF0D47A1),
            topLeft = Offset(w * 0.64f, h * 0.20f),
            size = Size(w * 0.10f, h * 0.07f),
            style = Fill
        )

        // Spray droplets (small water spray droplets)
        val sprayColor = Color(0xFF78909C)
        drawCircle(color = sprayColor, radius = 1.6.dp.toPx(), center = Offset(w * 0.80f, h * 0.18f))
        drawCircle(color = sprayColor, radius = 2.0.dp.toPx(), center = Offset(w * 0.86f, h * 0.23f))
        drawCircle(color = sprayColor, radius = 1.5.dp.toPx(), center = Offset(w * 0.92f, h * 0.21f))
        drawCircle(color = sprayColor, radius = 1.8.dp.toPx(), center = Offset(w * 0.83f, h * 0.28f))
        drawCircle(color = sprayColor, radius = 1.4.dp.toPx(), center = Offset(w * 0.90f, h * 0.27f))
    }
}

/**
 * Clean Recent Scans Preview Section.
 */
@Composable
fun RecentScansPreviewSection(
    recentScans: List<ScanHistoryItem>,
    onViewAllClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.recent_scans),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )

            if (recentScans.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.view_all),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen,
                    modifier = Modifier
                        .clickable { onViewAllClick() }
                        .padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (recentScans.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = CardBackground,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = LightGreenTint,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = stringResource(id = R.string.no_recent_scans),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                        Text(
                            text = stringResource(id = R.string.no_recent_scans_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recentScans.forEach { item ->
                    HistoryCard(item = item)
                }
            }
        }
    }
}

/**
 * Multi-Select Crop Selection Dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropSelectionDialog(
    initialSelectedCrops: Set<String>,
    onDismiss: () -> Unit,
    onSave: (Set<String>) -> Unit
) {
    val context = LocalContext.current
    var currentSelection by remember { mutableStateOf(initialSelectedCrops) }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
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
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.crop_selection_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SecondaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(id = R.string.crop_selection_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableCropOptions.forEach { option ->
                        val isSelected = currentSelection.contains(option.id)
                        val displayName = getCropDisplayName(option.id, context)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    currentSelection = if (isSelected) {
                                        currentSelection - option.id
                                    } else {
                                        currentSelection + option.id
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) LightGreenTint else CardBackground,
                            border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else BorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = option.emoji,
                                        fontSize = 22.sp
                                    )
                                    Text(
                                        text = displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryText
                                    )
                                }

                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        currentSelection = if (checked) {
                                            currentSelection + option.id
                                        } else {
                                            currentSelection - option.id
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PrimaryGreen,
                                        uncheckedColor = SecondaryText,
                                        checkmarkColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onSave(currentSelection) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.save_crops),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
