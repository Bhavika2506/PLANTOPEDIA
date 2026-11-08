package com.example.plantopedia

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

fun getTipIcon(iconName: String): ImageVector {
    return when (iconName) {
        "Terrain" -> Icons.Default.Terrain
        "Yard" -> Icons.Default.Yard
        "WaterDrop" -> Icons.Default.WaterDrop
        "ContentCut" -> Icons.Default.ContentCut
        "Eco" -> Icons.Default.Eco
        "Spa" -> Icons.Default.Spa
        "LocalFlorist" -> Icons.Default.LocalFlorist
        "Grass" -> Icons.Default.Grass
        "Inventory2" -> Icons.Default.Inventory2
        "Visibility" -> Icons.Default.Visibility
        else -> Icons.AutoMirrored.Filled.MenuBook
    }
}

@Composable
fun CultivationTipsScreen(
    initialCropId: String = "Tomato",
    onBack: () -> Unit,
    onTipClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang = remember(context) { UserManager.getLanguage(context) }

    var selectedCropId by remember { mutableStateOf(initialCropId) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0 = By Task, 1 = By Stage

    val allCrops = remember { LibraryRepository.crops }
    val currentCrop = remember(selectedCropId) {
        LibraryRepository.getCropById(selectedCropId) ?: allCrops.first()
    }

    val tipsForCrop = remember(selectedCropId) {
        LibraryRepository.getTipsForCrop(selectedCropId)
    }

    val taskTips = remember(tipsForCrop) {
        tipsForCrop.filter { it.categoryType == "TASK" }
    }

    val stageTips = remember(tipsForCrop) {
        tipsForCrop.filter { it.categoryType == "STAGE" }
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
                            text = stringResource(R.string.cultivation_tips_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.cultivation_tips_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Crop Selection Dropdown
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // Two Horizontal Tabs: "By Task" and "By Stage"
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = CardBackground,
                        contentColor = PrimaryGreen,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = PrimaryGreen,
                                height = 3.dp
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = {
                                Text(
                                    text = stringResource(R.string.tab_by_task),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == 0) PrimaryGreen else SecondaryText
                                )
                            }
                        )

                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = {
                                Text(
                                    text = stringResource(R.string.tab_by_stage),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == 1) PrimaryGreen else SecondaryText
                                )
                            }
                        )
                    }
                }
            }

            // Tab Content List
            val displayedList = if (selectedTabIndex == 0) taskTips else stageTips

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { Spacer(modifier = Modifier.height(6.dp)) }

                items(displayedList, key = { it.id }) { tip ->
                    CultivationTipCard(
                        tip = tip,
                        currentLang = currentLang,
                        onClick = { onTipClick(tip.id) }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun CultivationTipCard(
    tip: CultivationTipItem,
    currentLang: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when (currentLang) {
        "hi" -> tip.titleHi
        "mr" -> tip.titleMr
        else -> tip.titleEn
    }

    val summary = when (currentLang) {
        "hi" -> tip.summaryHi
        "mr" -> tip.summaryMr
        else -> tip.summaryEn
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
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = LightGreenTint,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = getTipIcon(tip.iconName),
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun CultivationTipDetailScreen(
    tipId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang = remember(context) { UserManager.getLanguage(context) }
    val tip = remember(tipId) { LibraryRepository.getTipById(tipId) }

    if (tip == null) {
        Surface(modifier = modifier.fillMaxSize(), color = AppBackground) {
            Box(contentAlignment = Alignment.Center) {
                Text("Tip not found", color = SecondaryText)
            }
        }
        return
    }

    val title = when (currentLang) {
        "hi" -> tip.titleHi
        "mr" -> tip.titleMr
        else -> tip.titleEn
    }

    val summary = when (currentLang) {
        "hi" -> tip.summaryHi
        "mr" -> tip.summaryMr
        else -> tip.summaryEn
    }

    val steps = when (currentLang) {
        "hi" -> tip.detailedStepsHi
        "mr" -> tip.detailedStepsMr
        else -> tip.detailedStepsEn
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
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                        Text(
                            text = "${tip.categoryType}: ${tip.categoryKey}",
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
                // Summary Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = LightGreenTint.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, PrimaryGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(28.dp)
                        )

                        Text(
                            text = summary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryText,
                            lineHeight = 20.sp
                        )
                    }
                }

                // Actionable Guidance Steps
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = CardBackground,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Practical Recommendations",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )

                        steps.forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF9FAF9), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = PrimaryGreen,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PrimaryText,
                                    lineHeight = 21.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
