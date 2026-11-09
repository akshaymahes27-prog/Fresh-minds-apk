package com.example.skybound.mascot

/**
 * The 10 distinct Mascot Poses in Skybound.
 * Each pose has a defined personality role and fallback chain.
 */
enum class MascotPose(
    val title: String,
    val description: String,
    val fallback: String
) {
    HI("Hi", "Default greeting and universal fallback pose", "HI"),
    WAVE("Wave", "Morning greetings and welcome screen", "HI"),
    HAPPY("Happy", "Good news, picking an activity, completed reviews", "HI"),
    CHEER("Cheer", "Small wins, celebrations, and pop-up messages", "HAPPY"),
    PROUD("Proud", "Finished goals, personal bests, high weekly rhythm", "CHEER"),
    WOW("Wow", "Leveling up in Altitude and first-time milestones", "CHEER"),
    THINKING("Thinking", "Goal reviews, goal planning, and focus selection", "HI"),
    TIRED("Tired", "Late evenings and gentle rest prompts", "SLEEP"),
    SLEEP("Sleep", "Empty screens and quiet hours", "HI"),
    SAD("Sad", "Searches and filters that find no matches", "HI");

    fun getInteractiveQuotes(): List<String> = when (this) {
        HAPPY -> listOf("Hi again!", "Boing! Ready to soar!", "Step by step!")
        WOW -> listOf("Whoa! Look at you!", "High flyer!", "Incredible progress!")
        WAVE -> listOf("Hello friend!", "Over here!", "Welcome to Skybound!")
        CHEER -> listOf("Yay! Go you!", "You got this!", "Small steps count!")
        THINKING -> listOf("Hmm, what is next?", "Good question!", "Reflecting is progress.")
        PROUD -> listOf("Look at you go!", "Very nice work!", "Solid effort today.")
        TIRED -> listOf("Yaaawn... restful evening?", "Rest days matter too!", "Good night champion.")
        SLEEP -> listOf("Zzz... dreaming high...", "Rest up for tomorrow!", "Recharging...")
        SAD -> listOf("No worries, try another word!", "We will find it!", "Keep looking!")
        HI -> listOf("Ready when you are!", "Let's turn feedback into goals!", "Hello there!")
    }
}
