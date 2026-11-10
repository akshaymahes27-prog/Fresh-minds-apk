package com.example.skybound.mascot

import com.example.R

/**
 * The official Mascot Poses in Skybound, mapped directly to the artist's 20-pose sprite sheet.
 * Each pose has a defined personality role, fallback chain, and official drawable resource.
 */
enum class MascotPose(
    val title: String,
    val description: String,
    val fallback: String,
    val drawableRes: Int
) {
    HI("Hi", "Default greeting and universal fallback pose", "HI", R.drawable.mascot_pose_happy),
    WAVE("Wave", "Morning greetings and welcome screen", "HI", R.drawable.mascot_pose_wink),
    HAPPY("Happy", "Good news, picking an activity, completed reviews", "HI", R.drawable.mascot_pose_happy),
    CHEER("Cheer", "Small wins, celebrations, and pop-up messages", "HAPPY", R.drawable.mascot_pose_celebrating),
    PROUD("Proud", "Finished goals, personal bests, high weekly rhythm", "CHEER", R.drawable.mascot_pose_victory),
    WOW("Wow", "Leveling up in Altitude and first-time milestones", "CHEER", R.drawable.mascot_pose_excited),
    THINKING("Thinking", "Goal reviews, goal planning, and focus selection", "HI", R.drawable.mascot_pose_thinking),
    TIRED("Tired", "Late evenings and gentle rest prompts", "SLEEP", R.drawable.mascot_pose_calm),
    SLEEP("Sleep", "Empty screens and quiet hours", "HI", R.drawable.mascot_pose_sleepy),
    SAD("Sad", "Searches and filters that find no matches", "HI", R.drawable.mascot_pose_sad),

    // Additional official artist poses
    EXCITED("Excited", "High energy celebration and motivation", "HAPPY", R.drawable.mascot_pose_excited),
    VICTORY("Victory", "Holding trophy up high for milestones", "PROUD", R.drawable.mascot_pose_victory),
    COOL("Cool", "Wearing stylish shades", "HI", R.drawable.mascot_pose_cool),
    FOCUSED("Focused", "Working deep on laptop", "THINKING", R.drawable.mascot_pose_focused),
    CELEBRATING("Celebrating", "Sparkles and confetti joy", "CHEER", R.drawable.mascot_pose_celebrating),
    ORGANISED("Organised", "Checklist clipboard in wing", "THINKING", R.drawable.mascot_pose_organised),
    IN_PROGRESS("In Progress", "Running fast on the move", "HAPPY", R.drawable.mascot_pose_inprogress),
    CALM("Calm", "Peaceful rest and meditation", "SLEEP", R.drawable.mascot_pose_calm),
    PEEKING("Peeking", "Curious bird peeking over the edge", "HI", R.drawable.mascot_pose_peeking),
    LOVE("Love", "Heart eyes and affection", "HAPPY", R.drawable.mascot_pose_love),
    DETERMINED("Determined", "Ready for a big challenge", "HI", R.drawable.mascot_pose_determined);

    fun getInteractiveQuotes(): List<String> = when (this) {
        HAPPY, HI -> listOf("Hi again!", "Boing! Ready to soar!", "Step by step!", "Small steps count!")
        WOW, EXCITED -> listOf("Whoa! Look at you!", "High flyer!", "Incredible progress!", "Unstoppable!")
        WAVE -> listOf("Hello friend!", "Over here!", "Welcome to Skybound!", "Glad to see you!")
        CHEER, CELEBRATING -> listOf("Yay! Go you!", "You got this!", "Small steps count!", "Celebrate every win!")
        THINKING, FOCUSED, ORGANISED -> listOf("Hmm, what is next?", "Good question!", "Reflecting is progress.", "Stay focused on today's aim.")
        PROUD, VICTORY -> listOf("Look at you go!", "Very nice work!", "Solid effort today.", "Champion altitude!")
        TIRED, CALM -> listOf("Yaaawn... restful evening?", "Rest days matter too!", "Good night champion.", "Breathe and relax.")
        SLEEP -> listOf("Zzz... dreaming high...", "Rest up for tomorrow!", "Recharging...")
        SAD -> listOf("No worries, try another word!", "We will find it!", "Keep looking!")
        COOL -> listOf("Smooth flying!", "Looking sharp!", "Cruising along nicely.")
        IN_PROGRESS -> listOf("On the move!", "Building momentum!", "Almost there!")
        LOVE -> listOf("You're doing amazing! ❤️", "Love this progress!", "Proud of your dedication.")
        DETERMINED -> listOf("Let's do this!", "Eyes on the target!", "Focus and deliver!")
        PEEKING -> listOf("Peek-a-boo! I see you training!", "I'm always cheering for you!")
        else -> listOf("Ready when you are!", "Let's turn feedback into goals!")
    }
}
