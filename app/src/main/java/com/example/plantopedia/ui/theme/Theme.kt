package com.example.plantopedia.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = DarkForestGreen,
    onPrimary = Color.White,
    primaryContainer = SageGreenTint,
    onPrimaryContainer = DarkForestGreen,
    secondary = SecondaryGreen,
    onSecondary = Color.White,
    secondaryContainer = SoftWarningTint,
    onSecondaryContainer = StatusWarning,
    background = WarmBeigeBackground,
    onBackground = NearBlackText,
    surface = WarmWhiteSurface,
    onSurface = NearBlackText,
    surfaceVariant = SageGreenTint,
    onSurfaceVariant = MutedSlateText,
    outline = EarthBorder,
    error = StatusError,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF10281F),
    primaryContainer = DarkForestGreen,
    onPrimaryContainer = Color.White,
    secondary = SecondaryGreen,
    onSecondary = Color(0xFF10281F),
    background = Color(0xFF142419),
    onBackground = Color(0xFFFAF8F2),
    surface = Color(0xFF1E3224),
    onSurface = Color(0xFFFAF8F2),
    surfaceVariant = Color(0xFF26402E),
    onSurfaceVariant = Color(0xFFC8E6C9),
    outline = Color(0xFF3B5642),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

@Composable
fun PlantopediaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}