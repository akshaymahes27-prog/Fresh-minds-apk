package com.example

import com.example.skybound.model.ALTITUDE_RANKS
import com.example.skybound.model.GoalPriority
import com.example.skybound.model.calculateAltitude
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAltitudeCalculation() {
        val hatchling = calculateAltitude(0)
        assertEquals("Hatchling", hatchling.currentRank.title)
        assertEquals(1, hatchling.goalsToNextRank)

        val takeOff = calculateAltitude(3)
        assertEquals("Take-off", takeOff.currentRank.title)
        assertEquals(3, takeOff.goalsToNextRank)

        val summit = calculateAltitude(100)
        assertEquals("Summit", summit.currentRank.title)
        assertNull(summit.nextRank)
        assertEquals(100, summit.progressPercent)
    }

    @Test
    fun testGoalPriorityConversion() {
        assertEquals(GoalPriority.HIGH, GoalPriority.fromCode("H"))
        assertEquals(GoalPriority.MEDIUM, GoalPriority.fromCode("M"))
        assertEquals(GoalPriority.LOW, GoalPriority.fromCode("L"))
        assertEquals(GoalPriority.MEDIUM, GoalPriority.fromCode("UNKNOWN"))
    }

    @Test
    fun testAltitudeRanksOrder() {
        assertEquals(10, ALTITUDE_RANKS.size)
        assertEquals("Hatchling", ALTITUDE_RANKS.first().title)
        assertEquals("Summit", ALTITUDE_RANKS.last().title)
    }

    @Test
    fun testMascotModularStatePresets() {
        com.example.skybound.mascot.MascotModularState.applyPreset(
            newPalette = com.example.skybound.mascot.MascotColorPalette.PHOENIX,
            newCrest = com.example.skybound.mascot.MascotCrest.CROWN,
            newEyes = com.example.skybound.mascot.MascotEyes.SUNGLASSES,
            newAccessory = com.example.skybound.mascot.MascotAccessory.JETPACK
        )
        assertEquals(com.example.skybound.mascot.MascotColorPalette.PHOENIX, com.example.skybound.mascot.MascotModularState.palette)
        assertEquals(com.example.skybound.mascot.MascotCrest.CROWN, com.example.skybound.mascot.MascotModularState.crest)
        assertEquals(com.example.skybound.mascot.MascotEyes.SUNGLASSES, com.example.skybound.mascot.MascotModularState.eyes)
        assertEquals(com.example.skybound.mascot.MascotAccessory.JETPACK, com.example.skybound.mascot.MascotModularState.accessory)

        com.example.skybound.mascot.MascotModularState.resetToDefaults()
        assertEquals(com.example.skybound.mascot.MascotColorPalette.SKY_BLUE, com.example.skybound.mascot.MascotModularState.palette)
        assertEquals(com.example.skybound.mascot.MascotCrest.TUFT, com.example.skybound.mascot.MascotModularState.crest)
        assertFalse(com.example.skybound.mascot.MascotModularState.isFlyingLauncherMode)
    }
}
