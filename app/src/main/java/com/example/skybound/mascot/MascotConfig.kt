package com.example.skybound.mascot

import androidx.compose.ui.graphics.Color

/**
 * Easily editable Mascot configuration.
 * Change bird color palettes, beak colors, and accents right here.
 */
object MascotConfig {
    // Primary Body Feather Colors
    var bodyColor: Color = Color(0xFF1CB0F6)        // Sky Blue
    var wingColor: Color = Color(0xFF1899D6)        // Deep Sky Blue
    var tummyColor: Color = Color(0xFFEAF6FF)       // Soft Cloud White
    var beakColor: Color = Color(0xFFFFC800)        // Sunshine Yellow
    var beakInsideColor: Color = Color(0xFFFF9F43)  // Warm Orange
    var cheekColor: Color = Color(0xFFFF7EB6).copy(alpha = 0.8f) // Cheerful Pink
    var pupilColor: Color = Color(0xFF0F2A4A)       // Deep Navy
    var feetColor: Color = Color(0xFFFF9F43)        // Warm Orange

    // Custom Mascot Name
    var mascotName: String = "Sky"

    /**
     * Reset colors to official Skybound defaults
     */
    fun resetToDefaults() {
        bodyColor = Color(0xFF1CB0F6)
        wingColor = Color(0xFF1899D6)
        tummyColor = Color(0xFFEAF6FF)
        beakColor = Color(0xFFFFC800)
        beakInsideColor = Color(0xFFFF9F43)
        cheekColor = Color(0xFFFF7EB6).copy(alpha = 0.8f)
        pupilColor = Color(0xFF0F2A4A)
        feetColor = Color(0xFFFF9F43)
        mascotName = "Sky"
    }
}
