package com.example.plantopedia

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.plantopedia.ui.theme.BorderColor
import com.example.plantopedia.ui.theme.CardBackground
import com.example.plantopedia.ui.theme.LightGreenTint
import com.example.plantopedia.ui.theme.PlantopediaTheme
import com.example.plantopedia.ui.theme.PrimaryGreen
import com.example.plantopedia.ui.theme.PrimaryText
import com.example.plantopedia.ui.theme.SecondaryText

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
        enableEdgeToEdge()
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
                                    Surface(
                                        color = CardBackground,
                                        border = BorderStroke(1.dp, BorderColor),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        NavigationBar(
                                            containerColor = CardBackground,
                                            tonalElevation = 0.dp,
                                            windowInsets = NavigationBarDefaults.windowInsets,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            NavigationBarItem(
                                                selected = currentScreen == "home",
                                                onClick = { currentScreen = "home" },
                                                icon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Home,
                                                        contentDescription = stringResource(R.string.nav_home),
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = stringResource(R.string.nav_home),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = if (currentScreen == "home") FontWeight.Bold else FontWeight.Medium
                                                    )
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = PrimaryGreen,
                                                    selectedTextColor = PrimaryGreen,
                                                    indicatorColor = LightGreenTint,
                                                    unselectedIconColor = SecondaryText,
                                                    unselectedTextColor = SecondaryText
                                                )
                                            )

                                            NavigationBarItem(
                                                selected = currentScreen == "camera",
                                                onClick = { currentScreen = "camera" },
                                                icon = {
                                                    Icon(
                                                        imageVector = Icons.Default.PhotoCamera,
                                                        contentDescription = stringResource(R.string.nav_scan),
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = stringResource(R.string.nav_scan),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = if (currentScreen == "camera") FontWeight.Bold else FontWeight.Medium
                                                    )
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = PrimaryGreen,
                                                    selectedTextColor = PrimaryGreen,
                                                    indicatorColor = LightGreenTint,
                                                    unselectedIconColor = SecondaryText,
                                                    unselectedTextColor = SecondaryText
                                                )
                                            )

                                            NavigationBarItem(
                                                selected = currentScreen == "market",
                                                onClick = { currentScreen = "market" },
                                                icon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Storefront,
                                                        contentDescription = stringResource(R.string.nav_market),
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = stringResource(R.string.nav_market),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = if (currentScreen == "market") FontWeight.Bold else FontWeight.Medium
                                                    )
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = PrimaryGreen,
                                                    selectedTextColor = PrimaryGreen,
                                                    indicatorColor = LightGreenTint,
                                                    unselectedIconColor = SecondaryText,
                                                    unselectedTextColor = SecondaryText
                                                )
                                            )

                                            NavigationBarItem(
                                                selected = currentScreen == "advisor",
                                                onClick = { currentScreen = "advisor" },
                                                icon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Psychology,
                                                        contentDescription = stringResource(R.string.nav_advisor),
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = stringResource(R.string.nav_advisor),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = if (currentScreen == "advisor") FontWeight.Bold else FontWeight.Medium
                                                    )
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = PrimaryGreen,
                                                    selectedTextColor = PrimaryGreen,
                                                    indicatorColor = LightGreenTint,
                                                    unselectedIconColor = SecondaryText,
                                                    unselectedTextColor = SecondaryText
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        ) { innerPadding ->
                            when (currentScreen) {
                                "home" -> {
                                    HomeScreen(
                                        cameraPermissionGranted = cameraPermissionGranted,
                                        onScanClick = { currentScreen = "camera" },
                                        onHistoryClick = { currentScreen = "advisor_history" },
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

                                "market" -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    ) {
                                        MarketScreen()
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

                                "advisor_history" -> {
                                    BackHandler { currentScreen = "home" }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    ) {
                                        HistoryScreen(onBack = { currentScreen = "home" })
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
