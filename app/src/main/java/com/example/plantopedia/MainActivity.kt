package com.example.plantopedia

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.plantopedia.ui.theme.DarkForestGreen
import com.example.plantopedia.ui.theme.PlantopediaTheme
import com.example.plantopedia.ui.theme.WarmWhiteSurface

class MainActivity : ComponentActivity() {

    private var currentLangCode by mutableStateOf("en")

    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            showApp(granted)
        }

    override fun attachBaseContext(newBase: Context) {
        val lang = UserManager.getLanguage(newBase)
        val contextWithLocale = LocaleHelper.applyLanguage(newBase, lang)
        super.attachBaseContext(contextWithLocale)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentLangCode = UserManager.getLanguage(this)

        val permissionGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            showApp(true)
        } else {
            cameraPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    private fun updateLanguage(langCode: String) {
        UserManager.setLanguage(this, langCode)
        currentLangCode = langCode
        recreate()
    }

    private fun showApp(cameraPermissionGranted: Boolean) {
        setContent {
            val localizedContext = remember(currentLangCode) {
                LocaleHelper.applyLanguage(this, currentLangCode)
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedContext.resources.configuration
            ) {
                PlantopediaTheme {
                    var isAuthCompleted by remember {
                        mutableStateOf(UserManager.isLoggedIn(this) || UserManager.isGuest(this))
                    }

                    if (!isAuthCompleted) {
                        AuthScreen(
                            onAuthSuccess = {
                                isAuthCompleted = true
                            },
                            onLanguageChanged = { langCode ->
                                updateLanguage(langCode)
                            }
                        )
                    } else {
                        var currentScreen by remember {
                            mutableStateOf("home")
                        }

                        Scaffold(
                            containerColor = MaterialTheme.colorScheme.background,
                            bottomBar = {
                                if (currentScreen != "camera") {
                                    NavigationBar(
                                        containerColor = DarkForestGreen,
                                        tonalElevation = 0.dp
                                    ) {
                                        NavigationBarItem(
                                            selected = currentScreen == "home",
                                            onClick = { currentScreen = "home" },
                                            icon = { NavIcon(Icons.Default.Home, currentScreen == "home") },
                                            label = { NavLabel(stringResource(R.string.nav_home), currentScreen == "home") },
                                            colors = navItemColors()
                                        )

                                        NavigationBarItem(
                                            selected = currentScreen == "camera",
                                            onClick = { currentScreen = "camera" },
                                            icon = { NavIcon(Icons.Default.PhotoCamera, currentScreen == "camera") },
                                            label = { NavLabel(stringResource(R.string.nav_scan), currentScreen == "camera") },
                                            colors = navItemColors()
                                        )

                                        NavigationBarItem(
                                            selected = currentScreen == "history",
                                            onClick = { currentScreen = "history" },
                                            icon = { NavIcon(Icons.Default.History, currentScreen == "history") },
                                            label = { NavLabel(stringResource(R.string.nav_history), currentScreen == "history") },
                                            colors = navItemColors()
                                        )

                                        NavigationBarItem(
                                            selected = currentScreen == "advisor",
                                            onClick = { currentScreen = "advisor" },
                                            icon = { NavIcon(Icons.Default.Psychology, currentScreen == "advisor") },
                                            label = { NavLabel(stringResource(R.string.nav_advisor), currentScreen == "advisor") },
                                            colors = navItemColors()
                                        )
                                    }
                                }
                            }
                        ) { innerPadding ->
                            when (currentScreen) {
                                "home" -> {
                                    HomeScreen(
                                        cameraPermissionGranted = cameraPermissionGranted,
                                        onScanClick = { currentScreen = "camera" },
                                        onHistoryClick = { currentScreen = "history" },
                                        onLanguageChanged = { langCode ->
                                            updateLanguage(langCode)
                                        },
                                        onLogout = {
                                            isAuthCompleted = false
                                        },
                                        modifier = Modifier.padding(innerPadding)
                                    )
                                }

                                "camera" -> {
                                    CameraScreen(
                                        onBack = { currentScreen = "home" }
                                    )
                                }

                                "history" -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    ) {
                                        HistoryScreen()
                                    }
                                }

                                "advisor" -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    ) {
                                        AdvisorScreen()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = DarkForestGreen,
    selectedTextColor = DarkForestGreen,
    indicatorColor = WarmWhiteSurface,
    unselectedIconColor = Color.White,
    unselectedTextColor = Color.White
)

@Composable
fun NavIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean) {
    if (selected) {
        Box(
            modifier = Modifier
                .size(40.dp, 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(WarmWhiteSurface),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Icon(icon, null, tint = DarkForestGreen)
        }
    } else {
        Icon(icon, null)
    }
}

@Composable
fun NavLabel(text: String, selected: Boolean) {
    Text(
        text = text,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1,
        softWrap = false
    )
}