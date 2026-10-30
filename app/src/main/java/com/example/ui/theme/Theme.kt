package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TransitBlueDarkPrimary,
    onPrimary = TransitBlueDarkOnPrimary,
    primaryContainer = TransitBlueDarkContainer,
    onPrimaryContainer = TransitBlueDarkOnContainer,
    secondary = TransitTealDarkSecondary,
    onSecondary = TransitTealDarkOnSecondary,
    secondaryContainer = TransitTealDarkContainer,
    onSecondaryContainer = TransitTealDarkOnContainer,
    tertiary = TransitAmberDarkTertiary,
    onTertiary = TransitAmberDarkOnTertiary,
    tertiaryContainer = TransitAmberDarkContainer,
    onTertiaryContainer = TransitAmberDarkOnContainer,
    background = TransitSurfaceDark,
    surface = TransitSurfaceDark,
    onBackground = TransitOnSurfaceDark,
    onSurface = TransitOnSurfaceDark,
    surfaceVariant = TransitSurfaceVariantDark,
    onSurfaceVariant = TransitOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = TransitBluePrimary,
    onPrimary = TransitBlueOnPrimary,
    primaryContainer = TransitBlueContainer,
    onPrimaryContainer = TransitBlueOnContainer,
    secondary = TransitTealSecondary,
    onSecondary = TransitTealOnSecondary,
    secondaryContainer = TransitTealContainer,
    onSecondaryContainer = TransitTealOnContainer,
    tertiary = TransitAmberTertiary,
    onTertiary = TransitAmberOnTertiary,
    tertiaryContainer = TransitAmberContainer,
    onTertiaryContainer = TransitAmberOnContainer,
    background = TransitSurfaceLight,
    surface = TransitSurfaceLight,
    onBackground = TransitOnSurfaceLight,
    onSurface = TransitOnSurfaceLight,
    surfaceVariant = TransitSurfaceVariantLight,
    onSurfaceVariant = TransitOnSurfaceVariantLight
)

@Composable
fun TransitFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false by default to ensure strong cohesive Transit branding
    content: @Composable () -> Unit,
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

// Backwards compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) = TransitFlowTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
