package com.example.skybound.mascot

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Modular parts system for the Skybound Mascot.
 * Every part (crest, eyes, beak, wings, accessory, palette) can be freely swapped,
 * extended, or randomized.
 */

enum class MascotCrest(val id: String, val title: String) {
    TUFT("tuft", "Classic Tuft"),
    CROWN("crown", "Royal Crown"),
    GOGGLES("goggles", "Aviator Goggles"),
    HEADBAND("headband", "Sport Headband"),
    STAR_HALO("halo", "Celestial Halo"),
    SMOOTH("smooth", "Aerodynamic")
}

enum class MascotEyes(val id: String, val title: String) {
    SPARKLE("sparkle", "Curious Sparkle"),
    SUNGLASSES("sunglasses", "Cool Aviators"),
    STAR_EYES("star", "Starstruck"),
    JOYFUL("joyful", "Joyful Arcs"),
    WINK("wink", "Playful Wink"),
    SLEEPY("sleepy", "Peaceful")
}

enum class MascotBeak(val id: String, val title: String) {
    CLASSIC("classic", "Classic Beak"),
    OPEN_SMILE("smile", "Wide Smile"),
    CHIRP("chirp", "Singing Chirp"),
    WHISTLE("whistle", "Whistle Note")
}

enum class MascotWings(val id: String, val title: String) {
    CLASSIC("classic", "Resting Wing"),
    FLAPPING("flapping", "Fluttering"),
    SOARING("soaring", "Soaring Glide"),
    CHEERING("cheering", "Victory Wings")
}

enum class MascotAccessory(val id: String, val title: String) {
    NONE("none", "None"),
    CHAMPION_MEDAL("medal", "Gold Champion Medal"),
    HERO_SCARF("scarf", "Pilot Hero Scarf"),
    JETPACK("jetpack", "Rocket Jetpack"),
    STAR_BADGE("star_badge", "Honor Star")
}

enum class MascotColorPalette(
    val id: String,
    val title: String,
    val body: Color,
    val wing: Color,
    val tummy: Color,
    val beak: Color,
    val feet: Color
) {
    SKY_BLUE(
        id = "sky",
        title = "Sky Blue",
        body = Color(0xFF1CB0F6),
        wing = Color(0xFF1899D6),
        tummy = Color(0xFFEAF6FF),
        beak = Color(0xFFFFC800),
        feet = Color(0xFFFF9F43)
    ),
    PHOENIX(
        id = "phoenix",
        title = "Sunset Phoenix",
        body = Color(0xFFFF5252),
        wing = Color(0xFFD32F2F),
        tummy = Color(0xFFFFF0D4),
        beak = Color(0xFFFFC107),
        feet = Color(0xFFFF6E40)
    ),
    EMERALD(
        id = "emerald",
        title = "Emerald Falcon",
        body = Color(0xFF58CC02),
        wing = Color(0xFF46A302),
        tummy = Color(0xFFE8FBD8),
        beak = Color(0xFFFFC800),
        feet = Color(0xFFFF9F43)
    ),
    COSMIC(
        id = "cosmic",
        title = "Cosmic Swift",
        body = Color(0xFF9C27B0),
        wing = Color(0xFF7B1FA2),
        tummy = Color(0xFFF3E5F5),
        beak = Color(0xFFFFD54F),
        feet = Color(0xFFFF8A65)
    ),
    GOLDEN(
        id = "golden",
        title = "Golden Champion",
        body = Color(0xFFFFC800),
        wing = Color(0xFFE5A500),
        tummy = Color(0xFFFFFBEA),
        beak = Color(0xFFFF5722),
        feet = Color(0xFFFF9F43)
    ),
    CYBER(
        id = "cyber",
        title = "Cyber Neon",
        body = Color(0xFF00E5FF),
        wing = Color(0xFF0091EA),
        tummy = Color(0xFFE0F7FA),
        beak = Color(0xFFFF4081),
        feet = Color(0xFFFF5252)
    )
}

/**
 * Observable active Mascot State for instant live preview across the entire application.
 */
object MascotModularState {
    var crest by mutableStateOf(MascotCrest.TUFT)
    var eyes by mutableStateOf(MascotEyes.SPARKLE)
    var beak by mutableStateOf(MascotBeak.CLASSIC)
    var wings by mutableStateOf(MascotWings.CLASSIC)
    var accessory by mutableStateOf(MascotAccessory.NONE)
    var palette by mutableStateOf(MascotColorPalette.SKY_BLUE)

    // Flying Launcher Mode toggle
    var isFlyingLauncherMode by mutableStateOf(false)

    // Flag to enable official artist high-res illustration
    var useOfficialArtwork by mutableStateOf(true)

    fun applyPreset(
        newPalette: MascotColorPalette,
        newCrest: MascotCrest = MascotCrest.TUFT,
        newEyes: MascotEyes = MascotEyes.SPARKLE,
        newAccessory: MascotAccessory = MascotAccessory.NONE
    ) {
        palette = newPalette
        crest = newCrest
        eyes = newEyes
        accessory = newAccessory
        // Also sync legacy config
        MascotConfig.bodyColor = newPalette.body
        MascotConfig.wingColor = newPalette.wing
        MascotConfig.tummyColor = newPalette.tummy
        MascotConfig.beakColor = newPalette.beak
        MascotConfig.feetColor = newPalette.feet
    }

    fun randomize() {
        val palettes = MascotColorPalette.values()
        val crests = MascotCrest.values()
        val eyeList = MascotEyes.values()
        val beaks = MascotBeak.values()
        val accessories = MascotAccessory.values()

        palette = palettes.random()
        crest = crests.random()
        eyes = eyeList.random()
        beak = beaks.random()
        accessory = accessories.random()

        MascotConfig.bodyColor = palette.body
        MascotConfig.wingColor = palette.wing
        MascotConfig.tummyColor = palette.tummy
        MascotConfig.beakColor = palette.beak
        MascotConfig.feetColor = palette.feet
    }

    fun resetToDefaults() {
        applyPreset(
            newPalette = MascotColorPalette.SKY_BLUE,
            newCrest = MascotCrest.TUFT,
            newEyes = MascotEyes.SPARKLE,
            newAccessory = MascotAccessory.NONE
        )
        beak = MascotBeak.CLASSIC
        wings = MascotWings.CLASSIC
        isFlyingLauncherMode = false
    }
}
