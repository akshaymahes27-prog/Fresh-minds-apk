package com.example.skybound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.mascot.MascotPose
import com.example.skybound.mascot.MascotView
import com.example.ui.theme.*

data class TourStep(
    val title: String,
    val description: String,
    val featureTag: String,
    val mascotPose: MascotPose
)

val TOUR_STEPS = listOf(
    TourStep(
        title = "Welcome to Skybound",
        description = "Skybound helps young athletes, musicians, and learners turn feedback into goals, and goals into visible progress.",
        featureTag = "Overview",
        mascotPose = MascotPose.WAVE
    ),
    TourStep(
        title = "The Problem",
        description = "A coach says 'use your weaker foot in drills'. By tomorrow it is forgotten, and at the next session the athlete arrives unsure what to focus on.",
        featureTag = "Context",
        mascotPose = MascotPose.THINKING
    ),
    TourStep(
        title = "Our Goal and Vision",
        description = "Core promise: Never lose track of what you're working towards. A friendly daily habit that feels achievable and motivating, never like homework.",
        featureTag = "Vision",
        mascotPose = MascotPose.PROUD
    ),
    TourStep(
        title = "Our 7 Project Aims",
        description = "1. Record in under a minute.\n2. Easy next actions.\n3. Continuous adaptation.\n4. Seamless activity switching.\n5. Polished and accessible.\n6. Preserved data.\n7. Part of real training routines.",
        featureTag = "Project Aims",
        mascotPose = MascotPose.CHEER
    ),
    TourStep(
        title = "The 6-Step Loop",
        description = "01 Capture feedback → 02 Create goals → 03 Choose your focus (Current Aim) → 04 Take action → 05 Review progress → 06 Build your journey.",
        featureTag = "Workflow",
        mascotPose = MascotPose.HAPPY
    ),
    TourStep(
        title = "Current Aim (Focus)",
        description = "Star up to 3 goals. They appear first on Home with their next action and segmented progress bars.",
        featureTag = "F3, F7",
        mascotPose = MascotPose.WOW
    ),
    TourStep(
        title = "Goal Checklist & Next Action",
        description = "Break big goals into small steps. Complete the parent goal as steps are ticked. Keep a clear next action for easy starts.",
        featureTag = "F4, F8",
        mascotPose = MascotPose.HAPPY
    ),
    TourStep(
        title = "Altitude Ranks",
        description = "Your personal rank in each activity rises only with finished goals, from Hatchling to Summit. Never ranked against other users.",
        featureTag = "F24",
        mascotPose = MascotPose.PROUD
    ),
    TourStep(
        title = "Research & No Streaks Decision",
        description = "We surveyed young people: Skybound won 30-11 over AimUp. We deliberately rejected streak counters because breaking a streak creates guilt and anxiety.",
        featureTag = "Research",
        mascotPose = MascotPose.THINKING
    ),
    TourStep(
        title = "Privacy & Wellbeing by Design",
        description = "Local-first storage. No ads, no selling data, no public profiles, no AI judging your performance. Complete control with JSON backup.",
        featureTag = "Privacy",
        mascotPose = MascotPose.PROUD
    ),
    TourStep(
        title = "Home Screen Widgets & Notifications",
        description = "Interactive home screen widgets show your Current Aim and next session at a glance. Gentle reminders provide kind prompts before and after training.",
        featureTag = "Widgets & Notis",
        mascotPose = MascotPose.CHEER
    ),
    TourStep(
        title = "Mascot Studio & Physics Animations",
        description = "Every bit of the mascot is swappable in real-time (crest, eyes, beak, wings, accessories like jetpacks & medals), complete with physics confetti, breathing, and flying launcher mode!",
        featureTag = "Modular Mascot",
        mascotPose = MascotPose.WOW
    ),
    TourStep(
        title = "Handcrafted Vector Icons",
        description = "No generic single-letter placeholders: Skybound features custom vector icons for sports, music, fitness, dance, academic study, and personal development.",
        featureTag = "Design Polish",
        mascotPose = MascotPose.PROUD
    )
)

@Composable
fun TeenTechTourScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStepIndex by remember { mutableStateOf(0) }
    val step = TOUR_STEPS[currentStepIndex]
    val totalSteps = TOUR_STEPS.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SkyCloud)
            .statusBarsPadding()
            .testTag("tour_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Exit tour", tint = SkyNavy)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TeenTech Tour (${currentStepIndex + 1} of $totalSteps)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = DeepSky
                    )
                )
                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(SkyMist)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((currentStepIndex + 1) / totalSteps.toFloat())
                            .clip(RoundedCornerShape(99.dp))
                            .background(SkyBlue)
                    )
                }
            }
        }

        // Main Card
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(2.5.dp, SkyMist),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MascotView(
                    pose = step.mascotPose,
                    size = 96.dp,
                    animateBob = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(SkyCloud)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = step.featureTag,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = DeepSky
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = step.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = SkyNavy,
                        fontSize = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = SkyNavy,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Navigation Footer
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStepIndex > 0) {
                    OutlinedButton(
                        onClick = { currentStepIndex-- },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = DeepSky)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back", fontWeight = FontWeight.Bold, color = DeepSky)
                    }
                }

                Button(
                    onClick = {
                        if (currentStepIndex < totalSteps - 1) {
                            currentStepIndex++
                        } else {
                            onDismiss()
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                ) {
                    Text(
                        text = if (currentStepIndex < totalSteps - 1) "Next →" else "Finish Tour",
                        fontWeight = FontWeight.Black,
                        color = SkyNavy
                    )
                }
            }
        }
    }
}
