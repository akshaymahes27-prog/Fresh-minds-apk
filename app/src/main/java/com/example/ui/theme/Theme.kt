package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun getSkyboundShapes(cornerStyle: String): Shapes {
    return when (cornerStyle.lowercase()) {
        "soft" -> Shapes(
            extraSmall = RoundedCornerShape(4.dp),
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp),
            extraLarge = RoundedCornerShape(20.dp)
        )
        "sharp" -> Shapes(
            extraSmall = RoundedCornerShape(2.dp),
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(6.dp),
            large = RoundedCornerShape(8.dp),
            extraLarge = RoundedCornerShape(10.dp)
        )
        else -> Shapes( // "round" default
            extraSmall = RoundedCornerShape(8.dp),
            small = RoundedCornerShape(14.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(32.dp)
        )
    }
}

@Composable
fun SkyboundTheme(
    themeMode: String = "auto", // "auto", "light", "dark"
    accentPrimary: Color = SkyBlue,
    cornerStyle: String = "round",
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode.lowercase()) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }

    // Sync global theme state so all adaptive colors react immediately
    SkyboundThemeState.isDark = isDark

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = accentPrimary,
            onPrimary = DeepNavy,
            primaryContainer = DarkCloud,
            onPrimaryContainer = DarkNavyText,
            secondary = AccentMint,
            onSecondary = DeepNavy,
            background = DarkBg,
            onBackground = DarkNavyText,
            surface = DarkCard,
            onSurface = DarkNavyText,
            surfaceVariant = DarkCloud,
            onSurfaceVariant = Color(0xFFA5B9CC),
            outline = DarkMist
        )
    } else {
        lightColorScheme(
            primary = accentPrimary,
            onPrimary = SkyWhite,
            primaryContainer = SkyCloud,
            onPrimaryContainer = SkyNavy,
            secondary = AccentMint,
            onSecondary = SkyNavy,
            background = SkyCloud,
            onBackground = SkyNavy,
            surface = SkyWhite,
            onSurface = SkyNavy,
            surfaceVariant = SkyMist,
            onSurfaceVariant = SkyNavy,
            outline = SkyMist
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = getSkyboundShapes(cornerStyle),
        content = content
    )
}
