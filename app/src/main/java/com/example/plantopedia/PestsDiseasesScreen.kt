package com.example.plantopedia

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
fun PestsDiseasesScreen(
    initialCropId: String = "Tomato",
    onBack: () -> Unit,
    onDiseaseClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang = remember(context) { UserManager.getLanguage(context) }

    var selectedCropId by remember { mutableStateOf(initialCropId) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val allCrops = remember { LibraryRepository.crops }
    val currentCrop = remember(selectedCropId) {
        LibraryRepository.getCropById(selectedCropId) ?: allCrops.first()
    }

    val diseasesForCrop = remember(selectedCropId) {
        LibraryRepository.getDiseasesForCrop(selectedCropId)
    }

    // Group diseases by stage
    val stages = listOf(
        "Seedling Stage",
        "Vegetative Stage",
        "Flowering Stage",
        "Fruiting Stage",
        "Harvesting Stage"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with Back button and subtitle
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
                            text = stringResource(R.string.pests_diseases_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.pests_diseases_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Crop Selection Dropdown (Plantix inspired clean selector)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { dropdownExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            color = LightGreenTint.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, PrimaryGreen)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = currentCrop.emoji,
                                        fontSize = 22.sp
                                    )

                                    Column {
                                        Text(
                                            text = stringResource(R.string.select_crop_label),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SecondaryText
                                        )
                                        Text(
                                            text = when (currentLang) {
                                                "hi" -> currentCrop.nameHi
                                                "mr" -> currentCrop.nameMr
                                                else -> currentCrop.nameEn
                                            },
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Crop",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            allCrops.forEach { crop ->
                                val displayName = when (currentLang) {
                                    "hi" -> crop.nameHi
                                    "mr" -> crop.nameMr
                                    else -> crop.nameEn
                                }
                                DropdownMenuItem(
                                    leadingIcon = { Text(crop.emoji, fontSize = 20.sp) },
                                    text = {
                                        Text(
                                            text = displayName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = if (crop.id == selectedCropId) FontWeight.Bold else FontWeight.Normal,
                                            color = if (crop.id == selectedCropId) PrimaryGreen else PrimaryText
                                        )
                                    },
                                    onClick = {
                                        selectedCropId = crop.id
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Scrollable List Organized by Growth Stage
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(6.dp)) }

                stages.forEach { stageName ->
                    val stageItems = diseasesForCrop.filter { it.stage.equals(stageName, ignoreCase = true) }
                    if (stageItems.isNotEmpty()) {
                        item {
                            // Stage Section Header Pill
                            StageHeaderPill(stageName = stageName, currentLang = currentLang)
                        }

                        items(stageItems, key = { it.id }) { item ->
                            DiseasePestCard(
                                item = item,
                                currentLang = currentLang,
                                onClick = { onDiseaseClick(item.id) }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun StageHeaderPill(
    stageName: String,
    currentLang: String,
    modifier: Modifier = Modifier
) {
    val localizedStage = when (stageName) {
        "Seedling Stage" -> stringResource(R.string.stage_seedling)
        "Vegetative Stage" -> stringResource(R.string.stage_vegetative)
        "Flowering Stage" -> stringResource(R.string.stage_flowering)
        "Fruiting Stage" -> stringResource(R.string.stage_fruiting)
        "Maturity Stage" -> stringResource(R.string.stage_maturity)
        "Harvesting Stage" -> stringResource(R.string.stage_harvesting)
        else -> stageName
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = PrimaryGreen,
            modifier = Modifier.size(8.dp)
        ) {}

        Text(
            text = localizedStage,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen
        )
    }
}

@Composable
fun DiseasePestCard(
    item: DiseasePestItem,
    currentLang: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val itemName = when (currentLang) {
        "hi" -> item.nameHi
        "mr" -> item.nameMr
        else -> item.nameEn
    }

    val symptoms = when (currentLang) {
        "hi" -> item.symptomsHi
        "mr" -> item.symptomsMr
        else -> item.symptomsEn
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image / Icon
            Surface(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = Color(0xFFF3F5F3),
                border = BorderStroke(0.5.dp, BorderColor)
            ) {
                if (item.assetImagePath != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/${item.assetImagePath}")
                            .crossfade(true)
                            .build(),
                        contentDescription = itemName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (item.isPest) Icons.Default.BugReport else Icons.Default.Coronavirus,
                            contentDescription = null,
                            tint = if (item.isPest) Color(0xFFC05621) else Color(0xFFD69E2E),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Info column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = itemName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Pest vs Disease Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (item.isPest) Color(0xFFFEEBC8) else LightGreenTint,
                        border = BorderStroke(0.5.dp, if (item.isPest) Color(0xFFDD6B20) else PrimaryGreen)
                    ) {
                        Text(
                            text = if (item.isPest) stringResource(R.string.pest_badge) else stringResource(R.string.disease_badge),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.isPest) Color(0xFFC05621) else PrimaryGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = symptoms,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
fun DiseaseDetailScreen(
    diseaseId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang = remember(context) { UserManager.getLanguage(context) }
    val item = remember(diseaseId) { LibraryRepository.getDiseaseById(diseaseId) }

    if (item == null) {
        Surface(modifier = modifier.fillMaxSize(), color = AppBackground) {
            Box(contentAlignment = Alignment.Center) {
                Text("Information not found", color = SecondaryText)
            }
        }
        return
    }

    val itemName = when (currentLang) {
        "hi" -> item.nameHi
        "mr" -> item.nameMr
        else -> item.nameEn
    }

    val symptoms = when (currentLang) {
        "hi" -> item.symptomsHi
        "mr" -> item.symptomsMr
        else -> item.symptomsEn
    }

    val causes = when (currentLang) {
        "hi" -> item.causesHi
        "mr" -> item.causesMr
        else -> item.causesEn
    }

    val conditions = when (currentLang) {
        "hi" -> item.conditionsHi
        "mr" -> item.conditionsMr
        else -> item.conditionsEn
    }

    val prevention = when (currentLang) {
        "hi" -> item.preventionHi
        "mr" -> item.preventionMr
        else -> item.preventionEn
    }

    val treatment = when (currentLang) {
        "hi" -> item.treatmentHi
        "mr" -> item.treatmentMr
        else -> item.treatmentEn
    }

    val management = when (currentLang) {
        "hi" -> item.managementHi
        "mr" -> item.managementMr
        else -> item.managementEn
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
                            text = itemName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                        Text(
                            text = "${item.cropId} • ${item.stage}",
                            style = MaterialTheme.typography.bodySmall,
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
                // Photo banner if available
                if (item.assetImagePath != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/${item.assetImagePath}")
                                .crossfade(true)
                                .build(),
                            contentDescription = itemName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Section 1: Symptoms
                InfoCardSection(
                    title = stringResource(R.string.disease_symptoms_heading),
                    icon = Icons.Default.Warning,
                    content = {
                        Text(
                            text = symptoms,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryText,
                            lineHeight = 22.sp
                        )
                    }
                )

                // Section 2: Causes
                InfoCardSection(
                    title = stringResource(R.string.disease_causes_heading),
                    icon = Icons.Default.Coronavirus,
                    content = {
                        Text(
                            text = causes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryText,
                            lineHeight = 22.sp
                        )
                    }
                )

                // Section 3: Conditions Encouraging Disease
                InfoCardSection(
                    title = stringResource(R.string.disease_conditions_heading),
                    icon = Icons.Default.LocalFlorist,
                    content = {
                        Text(
                            text = conditions,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryText,
                            lineHeight = 22.sp
                        )
                    }
                )

                // Section 4: Prevention Methods
                InfoCardSection(
                    title = stringResource(R.string.disease_prevention_heading),
                    icon = Icons.Default.Shield,
                    content = {
                        Text(
                            text = prevention,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryText,
                            lineHeight = 22.sp
                        )
                    }
                )

                // Section 5: Recommended Treatment
                InfoCardSection(
                    title = stringResource(R.string.disease_treatment_heading),
                    icon = Icons.Default.MedicalServices,
                    content = {
                        Text(
                            text = treatment,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryText,
                            lineHeight = 22.sp
                        )
                    }
                )

                // Section 6: General Management Practices
                InfoCardSection(
                    title = stringResource(R.string.disease_management_heading),
                    icon = Icons.Default.HealthAndSafety,
                    content = {
                        Text(
                            text = management,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryText,
                            lineHeight = 22.sp
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
