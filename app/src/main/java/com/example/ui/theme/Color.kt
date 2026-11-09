package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Global reactive theme state.
 * Any Composable reading SkyNavy, SkyCloud, SkyMist, SkyTextSubtle, or SkyTextMuted
 * automatically recomposes with high-contrast, beautiful palette colors in both Light and Dark modes.
 */
object SkyboundThemeState {
    var isDark by mutableStateOf(false)
}

// Skybound Core Fixed Brand Colors
val SkyBlue = Color(0xFF1CB0F6)       // Primary action & progress fills
val DeepSky = Color(0xFF1899D6)       // 3D button edge and pressed states
val DeepNavy = Color(0xFF0F2A4A)      // Constant dark navy (for text/icons on bright SkyBlue)
val LightWhite = Color(0xFFFFFFFF)    // Constant white

// Adaptive Brand Colors (Seamlessly switch between Light and Dark mode!)
val SkyNavy: Color
    get() = if (SkyboundThemeState.isDark) Color(0xFFF0F6FC) else Color(0xFF0F2A4A)

val SkyWhite: Color
    get() = if (SkyboundThemeState.isDark) Color(0xFF132233) else Color(0xFFFFFFFF)

val SkyCloud: Color
    get() = if (SkyboundThemeState.isDark) Color(0xFF162536) else Color(0xFFEAF6FF)

val SkyMist: Color
    get() = if (SkyboundThemeState.isDark) Color(0xFF263D54) else Color(0xFFCFE9FB)

// Adaptive Secondary & Muted Text Colors (Prevents invisible text in Dark Mode)
val SkyTextSubtle: Color
    get() = if (SkyboundThemeState.isDark) Color(0xFFA5B9CC) else Color(0xFF4A6584)

val SkyTextMuted: Color
    get() = if (SkyboundThemeState.isDark) Color(0xFF8EA3B8) else Color(0xFF5B7794)

// Adaptive interactive button/link text (Glowing sky cyan in Dark mode, DeepSky in Light mode)
val SkyInteractiveText: Color
    get() = if (SkyboundThemeState.isDark) Color(0xFF8FD3FA) else Color(0xFF1899D6)

// Multicolour Accents (Vibrant & high-contrast in both modes)
val AccentSun = Color(0xFFFFC800)     // Milestones, records, celebrations
val AccentCoral = Color(0xFFFF6B6B)   // Activity colour, gentle attention dots
val AccentMint = Color(0xFF3DDC97)    // Activity colour, completed goals & steps
val AccentViolet = Color(0xFFA78BFA)  // Activity colour, reviews & reflection
val AccentOrange = Color(0xFFFF9F43)  // Activity colour, habits & weekly rhythm
val AccentPink = Color(0xFFFF7EB6)    // Activity colour, mascot details

// Dark Mode Palette Definitions
val DarkBg = Color(0xFF0B1520)
val DarkCard = Color(0xFF132233)
val DarkNavyText = Color(0xFFF0F6FC)
val DarkMist = Color(0xFF263D54)
val DarkCloud = Color(0xFF162536)
