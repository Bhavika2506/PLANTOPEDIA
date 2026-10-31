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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantopedia.ui.theme.DarkForestGreen
import com.example.plantopedia.ui.theme.EarthBorder
import com.example.plantopedia.ui.theme.MutedSlateText
import com.example.plantopedia.ui.theme.NearBlackText
import com.example.plantopedia.ui.theme.SageGreenTint
import com.example.plantopedia.ui.theme.WarmBeigeBackground
import com.example.plantopedia.ui.theme.WarmWhiteSurface
import kotlinx.coroutines.launch

@Composable
fun AdvisorScreen() {
    val coroutineScope = rememberCoroutineScope()

    var question by remember { mutableStateOf("") }
    var advice by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val q1 = stringResource(id = R.string.question_1)
    val q2 = stringResource(id = R.string.question_2)
    val q3 = stringResource(id = R.string.question_3)
    val q4 = stringResource(id = R.string.question_4)

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
                bottom = 30.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.advisor_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = NearBlackText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(id = R.string.advisor_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedSlateText
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // INTRO HERO BANNER (Deep Forest Green #23443D)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = DarkForestGreen,
                    shadowElevation = 0.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.size(14.dp))

                            Column {
                                Text(
                                    text = stringResource(R.string.advisor_card_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = stringResource(R.string.advisor_card_subtitle),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = stringResource(R.string.advisor_intro),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.95f),
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            // SUGGESTED QUESTIONS HEADER (18-20sp titleLarge)
            item {
                Text(
                    text = stringResource(R.string.suggested_questions),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkForestGreen
                )

                Spacer(modifier = Modifier.height(2.dp))
            }

            item {
                AdvisorQuestionCard(
                    question = q1,
                    onClick = {
                        question = q1
                        advice = null
                    }
                )
            }

            item {
                AdvisorQuestionCard(
                    question = q2,
                    onClick = {
                        question = q2
                        advice = null
                    }
                )
            }

            item {
                AdvisorQuestionCard(
                    question = q3,
                    onClick = {
                        question = q3
                        advice = null
                    }
                )
            }

            item {
                AdvisorQuestionCard(
                    question = q4,
                    onClick = {
                        question = q4
                        advice = null
                    }
                )
            }

            // ASK CUSTOM QUESTION SECTION (Warm White Card #FAF8F2 with EarthBorder #D1C9BC)
            item {
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = WarmWhiteSurface,
                    border = BorderStroke(1.dp, EarthBorder),
                    shadowElevation = 0.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = stringResource(R.string.ask_own_question),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkForestGreen
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(R.string.ask_question_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedSlateText
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = question,
                            onValueChange = {
                                question = it
                                advice = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = NearBlackText),
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DarkForestGreen,
                                unfocusedBorderColor = EarthBorder,
                                focusedTextColor = NearBlackText,
                                unfocusedTextColor = NearBlackText,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = DarkForestGreen
                            ),
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.type_message_placeholder),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MutedSlateText
                                )
                            },
                            singleLine = false,
                            enabled = !isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 52dp Dark Forest Green Pill Button
                        Button(
                            onClick = {
                                if (question.trim().isNotEmpty() && !isLoading) {
                                    coroutineScope.launch {
                                        isLoading = true
                                        advice = null
                                        advice = GeminiService.getAdvice(question.trim())
                                        isLoading = false
                                    }
                                }
                            },
                            enabled = !isLoading && question.trim().isNotEmpty(),
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
                            if (isLoading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = stringResource(id = R.string.getting_advice),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.get_advice),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ADVICE RESULT CARD (Warm White Card #FAF8F2 with EarthBorder #D1C9BC)
            if (advice != null) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = WarmWhiteSurface,
                        border = BorderStroke(1.dp, EarthBorder),
                        shadowElevation = 0.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = DarkForestGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = stringResource(R.string.advice_result_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkForestGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = advice!!,
                                style = MaterialTheme.typography.bodyLarge,
                                color = NearBlackText,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }
            }

            // DISCLAIMER TEXT
            item {
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MutedSlateText,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )

                    Text(
                        text = stringResource(R.string.advisor_disclaimer),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedSlateText
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AdvisorQuestionCard(
    question: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
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
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SageGreenTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = null,
                    tint = DarkForestGreen,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = question,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = NearBlackText
            )

            // Right chevron (>) matching Reference Screens
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MutedSlateText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}