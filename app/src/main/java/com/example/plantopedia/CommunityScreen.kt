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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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

data class CommunityPost(
    val id: String,
    val authorName: String,
    val location: String,
    val timeAgo: String,
    val category: String,
    val contentEn: String,
    val contentHi: String,
    val contentMr: String,
    var likesCount: Int = 0,
    var repliesCount: Int = 0,
    var isLiked: Boolean = false
)

val sampleCommunityPosts = listOf(
    CommunityPost(
        id = "1",
        authorName = "Ramesh Patil",
        location = "Nashik, Maharashtra",
        timeAgo = "2h ago",
        category = "Diseases",
        contentEn = "Early blight spots observed on my tomato crops after recent rainfall. Has anyone tried copper fungicide spray with good results?",
        contentHi = "हाल की बारिश के बाद मेरे टमाटर में अगेती झुलसा के धब्बे दिखे हैं। क्या किसी ने कॉपर फफूंदनाशी छिड़काव का उपयोग किया है?",
        contentMr = "नुकत्याच झालेल्या पावसानंतर टोमॅटो पिकावर करपा रोगाचे डाग दिसत आहेत. कोणी कॉपर बुरशीनाशक फवारणी केली आहे का?",
        likesCount = 14,
        repliesCount = 6
    ),
    CommunityPost(
        id = "2",
        authorName = "Suresh Verma",
        location = "Karnal, Haryana",
        timeAgo = "4h ago",
        category = "Crops",
        contentEn = "Wheat crop is at heading stage. Weather is warming up. Make sure to maintain optimum moisture for grain filling.",
        contentHi = "गेहूं की फसल बाली अवस्था में है। मौसम गर्म हो रहा है। दानों के भराव के लिए पर्याप्त नमी बनाए रखें।",
        contentMr = "गहू पीक ओंब्या भरण्याच्या अवस्थेत आहे. हवामान उष्ण होत आहे. दाणे भरण्यासाठी योग्य ओलावा ठेवा.",
        likesCount = 28,
        repliesCount = 9
    ),
    CommunityPost(
        id = "3",
        authorName = "Anand Sharma",
        location = "Indore, Madhya Pradesh",
        timeAgo = "1d ago",
        category = "Organic",
        contentEn = "Prepared Jeevamrut organic fertilizer for my vegetable beds. Increased earthworm activity noticed within 2 weeks!",
        contentHi = "सब्जियों के लिए जीवामृत जैविक खाद तैयार की। 2 सप्ताह में केंचुओं की गतिविधि में उल्लेखनीय वृद्धि देखी गई!",
        contentMr = "भाजीपाल्यासाठी जीवामृत तयार केले. २ आठवड्यांत गांडुळांची हालचाल वाढल्याचे दिसून आले!",
        likesCount = 35,
        repliesCount = 12
    ),
    CommunityPost(
        id = "4",
        authorName = "Venkatesh Rao",
        location = "Warangal, Telangana",
        timeAgo = "1d ago",
        category = "Tips",
        contentEn = "Drip irrigation tip: Flush drip lines once every 15 days with mild acid solution to prevent salt clogging.",
        contentHi = "ड्रिप सिंचाई सलाह: नमक जमने से रोकने के लिए हर 15 दिन में ड्रिप लाइनों को हल्के घोल से साफ करें।",
        contentMr = "ठिबक सिंचन टीप: क्षार साचणे रोखण्यासाठी दर १५ दिवसांनी ठिबक नळ्या स्वच्छ करा.",
        likesCount = 22,
        repliesCount = 4
    )
)

val communityCategories = listOf("All", "Crops", "Diseases", "Organic", "Tips")

@Composable
fun CommunityScreen() {
    val context = LocalContext.current
    val currentLang = UserManager.getLanguage(context)

    var selectedCategory by remember { mutableStateOf("All") }
    var newPostContent by remember { mutableStateOf("") }

    val posts = remember {
        mutableStateListOf<CommunityPost>().apply {
            addAll(sampleCommunityPosts)
        }
    }

    val filteredPosts = remember(posts, selectedCategory) {
        if (selectedCategory == "All") {
            posts
        } else {
            posts.filter { it.category.equals(selectedCategory, ignoreCase = true) }
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
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(10.dp))

                    Column {
                        Text(
                            text = stringResource(id = R.string.community_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = stringResource(id = R.string.community_subtitle),
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
                // CATEGORY FILTER CHIPS
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        communityCategories.forEach { category ->
                            val isSelected = selectedCategory == category
                            val label = when (category) {
                                "All" -> stringResource(R.string.community_filter_all)
                                "Crops" -> stringResource(R.string.community_filter_crops)
                                "Diseases" -> stringResource(R.string.community_filter_diseases)
                                "Organic" -> stringResource(R.string.community_filter_organic)
                                "Tips" -> stringResource(R.string.community_filter_tips)
                                else -> category
                            }

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { selectedCategory = category },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) PrimaryGreen else CardBackground,
                                border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else BorderColor)
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else PrimaryText
                                )
                            }
                        }
                    }
                }

                // ASK / SHARE COMPOSER CARD
                item {
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
                            OutlinedTextField(
                                value = newPostContent,
                                onValueChange = { newPostContent = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.community_ask_prompt),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SecondaryText
                                    )
                                },
                                minLines = 2,
                                maxLines = 4,
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

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        if (newPostContent.isNotBlank()) {
                                            val author = if (UserManager.isGuest(context)) {
                                                context.getString(R.string.guest_user)
                                            } else {
                                                UserManager.getUsername(context)
                                            }
                                            val newPost = CommunityPost(
                                                id = System.currentTimeMillis().toString(),
                                                authorName = author,
                                                location = "India",
                                                timeAgo = "Just now",
                                                category = if (selectedCategory == "All") "Tips" else selectedCategory,
                                                contentEn = newPostContent.trim(),
                                                contentHi = newPostContent.trim(),
                                                contentMr = newPostContent.trim()
                                            )
                                            posts.add(0, newPost)
                                            newPostContent = ""
                                        }
                                    },
                                    enabled = newPostContent.isNotBlank(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryGreen,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.community_post_button),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // POSTS FEED
                items(filteredPosts, key = { it.id }) { post ->
                    CommunityPostCard(
                        post = post,
                        currentLang = currentLang,
                        onLikeClick = {
                            val idx = posts.indexOfFirst { it.id == post.id }
                            if (idx != -1) {
                                val current = posts[idx]
                                val newLiked = !current.isLiked
                                val newCount = if (newLiked) current.likesCount + 1 else maxOf(0, current.likesCount - 1)
                                posts[idx] = current.copy(isLiked = newLiked, likesCount = newCount)
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun CommunityPostCard(
    post: CommunityPost,
    currentLang: String,
    onLikeClick: () -> Unit
) {
    val postText = when (currentLang) {
        "hi" -> post.contentHi
        "mr" -> post.contentMr
        else -> post.contentEn
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
            // Author info and category
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
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = LightGreenTint,
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = post.authorName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = "${post.location} • ${post.timeAgo}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryText
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightGreenTint
                ) {
                    Text(
                        text = post.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post content
            Text(
                text = postText,
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryText,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action row (Like, Reply count)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLikeClick() }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) Color(0xFFE53935) else SecondaryText,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.community_likes, post.likesCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (post.isLiked) Color(0xFFE53935) else SecondaryText,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Replies",
                        tint = SecondaryText,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.community_replies, post.repliesCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
