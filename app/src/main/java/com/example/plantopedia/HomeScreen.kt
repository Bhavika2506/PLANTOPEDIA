package com.example.plantopedia

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhotoCamera
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
import android.content.Context
import com.example.plantopedia.ui.theme.DarkForestGreen
import com.example.plantopedia.ui.theme.EarthBorder
import com.example.plantopedia.ui.theme.MutedSlateText
import com.example.plantopedia.ui.theme.NearBlackText
import com.example.plantopedia.ui.theme.SageGreenTint
import com.example.plantopedia.ui.theme.StatusSuccess
import com.example.plantopedia.ui.theme.WarmBeigeBackground
import com.example.plantopedia.ui.theme.WarmWhiteSurface

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    cameraPermissionGranted: Boolean,
    onScanClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onLanguageChanged: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showCropDialog by remember { mutableStateOf(false) }

    val username = if (UserManager.isGuest(context)) {
        stringResource(id = R.string.guest_user)
    } else {
        UserManager.getUsername(context)
    }

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
        color = WarmBeigeBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // HEADER BANNER (Deep Forest Green #23443D)
            Surface(
                color = DarkForestGreen,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.welcome_farmer, username),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 12.dp)
                        )

                        Box(
                            modifier = Modifier.wrapContentSize(Alignment.TopEnd)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { showMenu = true },
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = stringResource(R.string.select_language),
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = langLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(id = R.string.home_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // MY CROPS SECTION (Single interactive section for viewing & editing)
                MyCropsSection(
                    selectedCrops = selectedCrops,
                    onClick = { showCropDialog = true }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // HERO SCAN CARD
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = WarmWhiteSurface,
                    border = BorderStroke(1.dp, EarthBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = DarkForestGreen,
                            modifier = Modifier.size(44.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(id = R.string.check_my_plant),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = NearBlackText,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(id = R.string.check_plant_desc),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MutedSlateText,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onScanClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 56.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.scan_crop),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // RECENT SCANS HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.recent_scans),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = NearBlackText
                    )
                    Text(
                        text = stringResource(id = R.string.view_all),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkForestGreen,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onHistoryClick() }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // LIST
                if (recentScans.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = WarmWhiteSurface,
                        border = BorderStroke(1.dp, EarthBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = DarkForestGreen,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Column {
                                Text(
                                    text = stringResource(id = R.string.no_recent_scans),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NearBlackText
                                )
                                Text(
                                    text = stringResource(id = R.string.no_recent_scans_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MutedSlateText
                                )
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        recentScans.forEach { item ->
                            HistoryItem(item)
                        }
                    }
                }
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
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyCropsSection(
    selectedCrops: Set<String>,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = WarmWhiteSurface,
        border = BorderStroke(1.dp, EarthBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SageGreenTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = DarkForestGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.my_crops),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NearBlackText
                    )
                }

                // Edit / Plus Icon button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkForestGreen
                ) {
                    Icon(
                        imageVector = if (selectedCrops.isEmpty()) Icons.Default.Add else Icons.Default.Edit,
                        contentDescription = "Edit Crops",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(6.dp)
                            .size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedCrops.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.select_your_crops),
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkForestGreen,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedCrops.forEach { cropId ->
                        val option = availableCropOptions.find { it.id.equals(cropId, ignoreCase = true) }
                        val emoji = option?.emoji ?: "🌿"
                        val displayName = getCropDisplayName(cropId, context)

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SageGreenTint,
                            border = BorderStroke(1.dp, EarthBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = emoji)
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkForestGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

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
                        text = stringResource(id = R.string.crop_selection_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = NearBlackText
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MutedSlateText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(id = R.string.crop_selection_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSlateText
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
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    currentSelection = if (isSelected) {
                                        currentSelection - option.id
                                    } else {
                                        currentSelection + option.id
                                    }
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) SageGreenTint else Color.White,
                            border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else EarthBorder)
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
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = NearBlackText
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
                                        checkedColor = DarkForestGreen,
                                        uncheckedColor = MutedSlateText,
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
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkForestGreen,
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