package com.example.plantopedia

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.plantopedia.ui.theme.DarkForestGreen
import com.example.plantopedia.ui.theme.EarthBorder
import com.example.plantopedia.ui.theme.NearBlackText
import com.example.plantopedia.ui.theme.StatusError
import com.example.plantopedia.ui.theme.WarmBeigeBackground
import com.example.plantopedia.ui.theme.WarmWhiteSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onLanguageChanged: (String) -> Unit
) {
    val context = LocalContext.current

    var isRegisterMode by remember {
        mutableStateOf(!UserManager.isRegistered(context))
    }

    var username by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var selectedLangCode by remember {
        mutableStateOf(UserManager.getLanguage(context))
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val languages = listOf(
        Triple("en", "English", "English"),
        Triple("hi", "हिंदी", "Hindi"),
        Triple("mr", "मराठी", "Marathi")
    )

    var expandedLangDropdown by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = WarmBeigeBackground // Warm beige background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // App Logo Icon Badge (Dark Forest Green)
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(DarkForestGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // App Branding Name
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = DarkForestGreen
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isRegisterMode) {
                    stringResource(id = R.string.register_title)
                } else {
                    stringResource(id = R.string.login_title)
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = NearBlackText
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Auth Form Card on Warm White Surface (#FAF8F2)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = WarmWhiteSurface,
                border = BorderStroke(1.dp, EarthBorder),
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    if (isRegisterMode) {
                        ExposedDropdownMenuBox(
                            expanded = expandedLangDropdown,
                            onExpandedChange = { expandedLangDropdown = !expandedLangDropdown },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val currentLangTriple = languages.find { it.first == selectedLangCode } ?: languages[0]
                            OutlinedTextField(
                                value = currentLangTriple.second,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(id = R.string.select_language)) },
                                textStyle = MaterialTheme.typography.bodyLarge,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = null,
                                        tint = DarkForestGreen
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLangDropdown)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DarkForestGreen,
                                    unfocusedBorderColor = EarthBorder
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedLangDropdown,
                                onDismissRequest = { expandedLangDropdown = false }
                            ) {
                                languages.forEach { langTriple ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = langTriple.second,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        },
                                        onClick = {
                                            selectedLangCode = langTriple.first
                                            expandedLangDropdown = false
                                            UserManager.setLanguage(context, langTriple.first)
                                            onLanguageChanged(langTriple.first)
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it; errorMessage = null },
                            label = { Text(stringResource(id = R.string.username)) },
                            textStyle = MaterialTheme.typography.bodyLarge,
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = DarkForestGreen) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkForestGreen, unfocusedBorderColor = EarthBorder),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it; errorMessage = null },
                        label = { Text(stringResource(id = R.string.mobile_number)) },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = DarkForestGreen) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkForestGreen, unfocusedBorderColor = EarthBorder),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text(stringResource(id = R.string.password)) },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = DarkForestGreen) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkForestGreen, unfocusedBorderColor = EarthBorder),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = DarkForestGreen
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = StatusError,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            if (isRegisterMode) {
                                if (username.isBlank()) { errorMessage = context.getString(R.string.error_username_required); return@Button }
                                if (!UserManager.isValidMobile(mobile)) { errorMessage = context.getString(R.string.error_invalid_mobile); return@Button }
                                if (password.isBlank()) { errorMessage = context.getString(R.string.error_password_required); return@Button }

                                val success = UserManager.register(context, username, mobile, password, selectedLangCode)
                                if (success) { onLanguageChanged(selectedLangCode); onAuthSuccess() } else { errorMessage = context.getString(R.string.error_user_exists) }
                            } else {
                                if (mobile.isBlank()) { errorMessage = context.getString(R.string.error_invalid_mobile); return@Button }
                                if (password.isBlank()) { errorMessage = context.getString(R.string.error_password_required); return@Button }

                                val success = UserManager.login(context, mobile, password)
                                if (success) { onLanguageChanged(UserManager.getLanguage(context)); onAuthSuccess() } else { errorMessage = context.getString(R.string.error_invalid_credentials) }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = if (isRegisterMode) stringResource(id = R.string.register_button) else stringResource(id = R.string.login_button),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text = if (isRegisterMode) stringResource(id = R.string.already_have_account) else stringResource(id = R.string.dont_have_account),
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkForestGreen,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isRegisterMode = !isRegisterMode; errorMessage = null }
                            .padding(vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedButton(
                onClick = { UserManager.setGuestMode(context, true); onAuthSuccess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, DarkForestGreen),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkForestGreen),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.continue_as_guest),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}