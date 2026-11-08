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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.plantopedia.ui.theme.AppBackground
import com.example.plantopedia.ui.theme.BorderColor
import com.example.plantopedia.ui.theme.CardBackground
import com.example.plantopedia.ui.theme.LightGreenTint
import com.example.plantopedia.ui.theme.PrimaryGreen
import com.example.plantopedia.ui.theme.PrimaryText
import com.example.plantopedia.ui.theme.SecondaryGreen
import com.example.plantopedia.ui.theme.SecondaryText

@Composable
fun CropsScreen(
    onBack: () -> Unit,
    onCropClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang = remember(context) { UserManager.getLanguage(context) }
    var searchQuery by remember { mutableStateOf("") }

    val allCrops = remember { LibraryRepository.crops }
    val filteredCrops = remember(searchQuery, currentLang) {
        if (searchQuery.isBlank()) {
            allCrops
        } else {
            allCrops.filter { crop ->
                val name = when (currentLang) {
                    "hi" -> crop.nameHi
                    "mr" -> crop.nameMr
                    else -> crop.nameEn
                }
                name.contains(searchQuery, ignoreCase = true) ||
                        crop.nameEn.contains(searchQuery, ignoreCase = true) ||
                        crop.scientificName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with Back button, Heading & Subtitle inspired by Plantix
            Surface(
                color = CardBackground,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = PrimaryGreen
                            )
                        }

                        Text(
                            text = stringResource(R.string.crops_screen_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.crops_screen_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Clean Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_crops),
                                style = MaterialTheme.typography.bodyMedium,
                                color = SecondaryText
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = SecondaryText
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = SecondaryText
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = CardBackground,
                            unfocusedContainerColor = CardBackground
                        )
                    )
                }
            }

            // Two-Column Grid of Crop Cards
            if (filteredCrops.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_crops_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredCrops, key = { it.id }) { crop ->
                        CropGridCard(
                            crop = crop,
                            currentLang = currentLang,
                            onClick = { onCropClick(crop.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CropGridCard(
    crop: CropLibraryItem,
    currentLang: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cropName = when (currentLang) {
        "hi" -> crop.nameHi
        "mr" -> crop.nameMr
        else -> crop.nameEn
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = CardBackground,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image / Illustration Box with light neutral background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .background(Color(0xFFF2F5F2)),
                contentAlignment = Alignment.Center
            ) {
                if (crop.assetImagePath != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/${crop.assetImagePath}")
                            .crossfade(true)
                            .build(),
                        contentDescription = cropName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = crop.emoji,
                            fontSize = 42.sp
                        )
                    }
                }

                // Category pill in top right corner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightGreenTint.copy(alpha = 0.92f),
                    border = BorderStroke(0.5.dp, BorderColor),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = crop.category,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Bottom Name Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = cropName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = crop.scientificName,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = SecondaryText,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CropDetailScreen(
    cropId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang = remember(context) { UserManager.getLanguage(context) }
    val crop = remember(cropId) { LibraryRepository.getCropById(cropId) }

    if (crop == null) {
        Surface(modifier = modifier.fillMaxSize(), color = AppBackground) {
            Box(contentAlignment = Alignment.Center) {
                Text("Crop not found", color = SecondaryText)
            }
        }
        return
    }

    val cropName = when (currentLang) {
        "hi" -> crop.nameHi
        "mr" -> crop.nameMr
        else -> crop.nameEn
    }

    val cropOverview = when (currentLang) {
        "hi" -> crop.overviewHi
        "mr" -> crop.overviewMr
        else -> crop.overviewEn
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Surface(
                color = CardBackground,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = PrimaryGreen
                        )
                    }

                    Column {
                        Text(
                            text = cropName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                        Text(
                            text = crop.scientificName,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = SecondaryText
                        )
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Banner Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = CardBackground,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            color = LightGreenTint,
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(crop.emoji, fontSize = 32.sp)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cropName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Category: ${crop.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Duration: ${crop.growingDuration}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText
                            )
                        }
                    }
                }

                // Section 1: Overview
                InfoCardSection(
                    title = stringResource(R.string.crop_overview_heading),
                    icon = Icons.Default.Info,
                    content = {
                        Text(
                            text = cropOverview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryText,
                            lineHeight = 22.sp
                        )
                    }
                )

                // Section 2: Growing Conditions (Compact Specs Table/Cards)
                InfoCardSection(
                    title = stringResource(R.string.growing_conditions_heading),
                    icon = Icons.Default.Yard,
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SpecRowItem(label = stringResource(R.string.climate_label), value = crop.suitableClimate)
                            SpecRowItem(label = stringResource(R.string.ideal_temp_label), value = crop.idealTemperature)
                            SpecRowItem(label = stringResource(R.string.soil_label), value = crop.soilRequirements)
                            SpecRowItem(label = stringResource(R.string.soil_ph_label), value = crop.soilPh)
                            SpecRowItem(label = stringResource(R.string.water_req_label), value = crop.waterRequirements)
                            SpecRowItem(label = stringResource(R.string.sowing_season_label), value = crop.sowingSeason)
                            SpecRowItem(label = stringResource(R.string.duration_label), value = crop.growingDuration)
                        }
                    }
                )

                // Section 3: Fertilizer Requirements
                InfoCardSection(
                    title = stringResource(R.string.fertilizer_heading),
                    icon = Icons.Default.Eco,
                    content = {
                        Text(
                            text = crop.fertilizerRequirements,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryText,
                            lineHeight = 20.sp
                        )
                    }
                )

                // Section 4: Common Pests and Diseases
                InfoCardSection(
                    title = stringResource(R.string.pests_diseases_heading),
                    icon = Icons.Default.Opacity,
                    content = {
                        Text(
                            text = crop.commonPestsAndDiseases,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryText,
                            lineHeight = 20.sp
                        )
                    }
                )

                // Section 5: Harvesting Information
                InfoCardSection(
                    title = stringResource(R.string.harvesting_heading),
                    icon = Icons.Default.Timer,
                    content = {
                        Text(
                            text = crop.harvestingInformation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryText,
                            lineHeight = 20.sp
                        )
                    }
                )

                // Section 6: Companion Crops
                InfoCardSection(
                    title = stringResource(R.string.companion_heading),
                    icon = Icons.Default.Eco,
                    content = {
                        Text(
                            text = crop.companionCrops,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryText,
                            lineHeight = 20.sp
                        )
                    }
                )

                // Section 7: Crop Rotation Recommendations
                InfoCardSection(
                    title = stringResource(R.string.rotation_heading),
                    icon = Icons.AutoMirrored.Filled.RotateRight,
                    content = {
                        Text(
                            text = crop.cropRotation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryText,
                            lineHeight = 20.sp
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun InfoCardSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = LightGreenTint,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            content()
        }
    }
}

@Composable
fun SpecRowItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAF9), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = SecondaryText,
            modifier = Modifier.width(130.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            color = PrimaryText,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
