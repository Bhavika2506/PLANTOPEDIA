package com.example.plantopedia

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.plantopedia.ui.theme.StatusSuccess

enum class PriceTrend {
    UP, DOWN, STEADY
}

data class MandiCommodity(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val nameMr: String,
    val emoji: String,
    val mandi: String,
    val state: String,
    val modalPrice: Int,
    val minPrice: Int,
    val maxPrice: Int,
    val trend: PriceTrend,
    val trendPercent: Double
)

val sampleMandiData = listOf(
    MandiCommodity(
        id = "Tomato",
        nameEn = "Tomato",
        nameHi = "टमाटर",
        nameMr = "टोमॅटो",
        emoji = "🍅",
        mandi = "Kolar APMC",
        state = "Karnataka",
        modalPrice = 1850,
        minPrice = 1600,
        maxPrice = 2100,
        trend = PriceTrend.UP,
        trendPercent = 4.2
    ),
    MandiCommodity(
        id = "Wheat",
        nameEn = "Wheat",
        nameHi = "गेहूं",
        nameMr = "गहू",
        emoji = "🌾",
        mandi = "Indore APMC",
        state = "Madhya Pradesh",
        modalPrice = 2450,
        minPrice = 2300,
        maxPrice = 2600,
        trend = PriceTrend.STEADY,
        trendPercent = 0.0
    ),
    MandiCommodity(
        id = "Cotton",
        nameEn = "Cotton (Medium Staple)",
        nameHi = "कपास (मध्यम)",
        nameMr = "कापूस (मध्यम)",
        emoji = "☁️",
        mandi = "Rajkot Mandi",
        state = "Gujarat",
        modalPrice = 7120,
        minPrice = 6800,
        maxPrice = 7450,
        trend = PriceTrend.UP,
        trendPercent = 2.5
    ),
    MandiCommodity(
        id = "Potato",
        nameEn = "Potato",
        nameHi = "आलू",
        nameMr = "बटाटा",
        emoji = "🥔",
        mandi = "Agra Mandi",
        state = "Uttar Pradesh",
        modalPrice = 1420,
        minPrice = 1250,
        maxPrice = 1600,
        trend = PriceTrend.DOWN,
        trendPercent = 1.8
    ),
    MandiCommodity(
        id = "Rice",
        nameEn = "Paddy (Basmati)",
        nameHi = "धान (बासमती)",
        nameMr = "भात (बासमती)",
        emoji = "🌾",
        mandi = "Karnal Mandi",
        state = "Haryana",
        modalPrice = 3650,
        minPrice = 3400,
        maxPrice = 3900,
        trend = PriceTrend.UP,
        trendPercent = 1.2
    ),
    MandiCommodity(
        id = "Corn",
        nameEn = "Maize (Corn)",
        nameHi = "मक्का",
        nameMr = "मका",
        emoji = "🌽",
        mandi = "Nizamabad APMC",
        state = "Telangana",
        modalPrice = 2180,
        minPrice = 2050,
        maxPrice = 2300,
        trend = PriceTrend.STEADY,
        trendPercent = 0.0
    ),
    MandiCommodity(
        id = "Sugarcane",
        nameEn = "Sugarcane",
        nameHi = "गन्ना",
        nameMr = "उस",
        emoji = "🎋",
        mandi = "Kolhapur APMC",
        state = "Maharashtra",
        modalPrice = 3150,
        minPrice = 3050,
        maxPrice = 3300,
        trend = PriceTrend.UP,
        trendPercent = 0.8
    ),
    MandiCommodity(
        id = "Apple",
        nameEn = "Apple (Delicious)",
        nameHi = "सेब",
        nameMr = "सफरचंद",
        emoji = "🍎",
        mandi = "Shimla Mandi",
        state = "Himachal Pradesh",
        modalPrice = 8200,
        minPrice = 7500,
        maxPrice = 9000,
        trend = PriceTrend.DOWN,
        trendPercent = 3.1
    )
)

@Composable
fun MarketScreen() {
    val context = LocalContext.current
    val currentLang = UserManager.getLanguage(context)

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterCrop by remember { mutableStateOf<String?>(null) }

    val filteredItems = remember(searchQuery, selectedFilterCrop, currentLang) {
        sampleMandiData.filter { item ->
            val name = when (currentLang) {
                "hi" -> item.nameHi
                "mr" -> item.nameMr
                else -> item.nameEn
            }

            val matchesFilter = selectedFilterCrop == null || item.id.equals(selectedFilterCrop, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                    name.contains(searchQuery, ignoreCase = true) ||
                    item.mandi.contains(searchQuery, ignoreCase = true) ||
                    item.state.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesQuery
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // COMPACT TOP HEADER
            Surface(
                color = CardBackground,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = LightGreenTint,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(10.dp))

                    Column {
                        Text(
                            text = stringResource(id = R.string.market_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = stringResource(id = R.string.market_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // SEARCH BAR
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.market_search_placeholder),
                                style = MaterialTheme.typography.bodyMedium,
                                color = SecondaryText
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = SecondaryText,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = CardBackground,
                            unfocusedContainerColor = CardBackground,
                            focusedTextColor = PrimaryText,
                            unfocusedTextColor = PrimaryText,
                            cursorColor = PrimaryGreen
                        )
                    )
                }

                // CROP FILTER CHIPS
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isAllSelected = selectedFilterCrop == null
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedFilterCrop = null },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isAllSelected) PrimaryGreen else CardBackground,
                            border = BorderStroke(1.dp, if (isAllSelected) PrimaryGreen else BorderColor)
                        ) {
                            Text(
                                text = stringResource(R.string.market_all_crops),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isAllSelected) Color.White else PrimaryText
                            )
                        }

                        sampleMandiData.map { it.id to it.emoji }.distinctBy { it.first }.forEach { (cropId, emoji) ->
                            val isSelected = selectedFilterCrop.equals(cropId, ignoreCase = true)
                            val displayName = getCropDisplayName(cropId, context)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        selectedFilterCrop = if (isSelected) null else cropId
                                    },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) PrimaryGreen else CardBackground,
                                border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else BorderColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = emoji, fontSize = 14.sp)
                                    Text(
                                        text = displayName,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.White else PrimaryText
                                    )
                                }
                            }
                        }
                    }
                }

                // MARKET TIP BANNER
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFE8F3EC),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = LightGreenTint,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = stringResource(R.string.market_insights_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(R.string.market_insights_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrimaryText,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                // COMMODITY LIST
                items(filteredItems) { commodity ->
                    MandiCommodityCard(commodity = commodity)
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun MandiCommodityCard(
    commodity: MandiCommodity
) {
    val context = LocalContext.current
    val currentLang = UserManager.getLanguage(context)

    val displayName = when (currentLang) {
        "hi" -> commodity.nameHi
        "mr" -> commodity.nameMr
        else -> commodity.nameEn
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = LightGreenTint,
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = commodity.emoji, fontSize = 22.sp)
                        }
                    }

                    Column {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = SecondaryText,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${commodity.mandi}, ${commodity.state}",
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryText
                            )
                        }
                    }
                }

                // Modal Price & Trend
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.market_price_per_quintal, commodity.modalPrice),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (commodity.trend) {
                            PriceTrend.UP -> Color(0xFFE8F5E9)
                            PriceTrend.DOWN -> Color(0xFFFFEBEE)
                            PriceTrend.STEADY -> Color(0xFFF5F5F5)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            when (commodity.trend) {
                                PriceTrend.UP -> {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = StatusSuccess,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.market_trend_up, commodity.trendPercent),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusSuccess
                                    )
                                }
                                PriceTrend.DOWN -> {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                        contentDescription = null,
                                        tint = Color(0xFFC62828),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.market_trend_down, commodity.trendPercent),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFC62828)
                                    )
                                }
                                PriceTrend.STEADY -> {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingFlat,
                                        contentDescription = null,
                                        tint = SecondaryText,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.market_trend_steady),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = SecondaryText
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price range bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = AppBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Min: ₹${commodity.minPrice}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText
                    )
                    Text(
                        text = stringResource(R.string.market_updated_today),
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryGreen,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Max: ₹${commodity.maxPrice}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}
