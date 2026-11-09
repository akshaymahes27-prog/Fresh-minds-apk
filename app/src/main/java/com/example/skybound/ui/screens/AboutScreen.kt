package com.example.skybound.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandFeatures by remember { mutableStateOf(false) }
    var expandChangelog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("about_screen"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SkyNavy)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "About Skybound",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = SkyNavy
                    )
                )
            }
        }

        // Hero Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MascotView(pose = MascotPose.HAPPY, size = 80.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Skybound",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )
                    Text(
                        text = "Turn feedback into goals. Turn goals into progress.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SkyTextSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Skybound is a non-commercial TeenTech project (formerly AimUp). It helps young people aged 13 to 19 turn coach or teacher feedback into concrete goals and daily actions, so feedback is never forgotten.\n\nNo ads, no subscriptions, no selling data. Everything is local-first on your device.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SkyNavy,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }

        // The 3 Questions
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Three Core Questions Answered",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )

                    QuestionAnswerRow("What am I trying to improve?", "Your goals, each linked directly to the feedback behind it.")
                    QuestionAnswerRow("What can I do about it?", "Your Current Aim (1-3 focus goals), next actions, sessions and habits.")
                    QuestionAnswerRow("How have I progressed?", "Reviews, history, personal bests, Altitude ranks, and progress reports.")
                }
            }
        }

        // Expandable Feature Matrix (F1 to F38)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandFeatures = !expandFeatures },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Feature Matrix (F1 – F38)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                        )
                        Icon(
                            if (expandFeatures) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = SkyNavy
                        )
                    }

                    AnimatedVisibility(visible = expandFeatures) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "F1: Quick feedback entry (under a minute)",
                                "F2: Turn feedback into goals (linked to feedback)",
                                "F3: Current Aim (top 1–3 focus goals on Home)",
                                "F4: Goal checklist with segmented step bars",
                                "F5: Carry goals forward & adaptation",
                                "F6: Goal priorities (High, Medium, Low)",
                                "F7: Quick-add central button",
                                "F8: One practical next action per goal",
                                "F9: Personal definition of success (never auto judged)",
                                "F10: Post-session reviews (reword, shrink, swap, retire)",
                                "F11: Timeline history audit per goal",
                                "F12: Goal template library by activity",
                                "F13: Session scheduling (one-off & repeating)",
                                "F14: Gentle pre/post-session reminders",
                                "F15: Goal-linked habits & weekly rhythm (no streaks)",
                                "F16: Searchable history across all activities",
                                "F17: Month activity calendar",
                                "F18: Personal best records with dates and notes",
                                "F19: Meaningful milestones celebrating reflection",
                                "F20: Progress report export & sharing",
                                "F21: Duolingo-style activity switcher",
                                "F22: 4-stat top bar (activity, rhythm, aim, session)",
                                "F23: 5-item bottom bar with central quick-add",
                                "F24: Altitude system (10 personal ranks, Hatchling to Summit)",
                                "F25: Mascot with 10 expressive poses & dialogue",
                                "F26: Celebration dialogs (goal, climb, review, milestone)",
                                "F27: 5-step lesson-style feedback flow",
                                "F28: Brand refresh (Skybound name, logo, palette)",
                                "F29: Local storage & offline capability",
                                "F30: JSON backup & restore export/import",
                                "F31: Full data management & wipe safety",
                                "F32: Confirmation sheets before deletion",
                                "F33: Home screen widgets (Small & Medium live support)",
                                "F34: Onboarding & isolated example football data",
                                "F34b: Complete TeenTech tour with research & design evidence",
                                "F35: Appearance customization (accents, theme, corners)",
                                "F36: Accessibility (contrast, text scaling, reduce motion)",
                                "F37: Guides, devlog, and versioned changelog",
                                "F38: Preservation of user data across updates"
                            ).forEach { feat ->
                                Text(
                                    text = "• $feat",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SkyNavy,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Expandable Changelog
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandChangelog = !expandChangelog },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Changelog (v0.91 – v0.98)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                        )
                        Icon(
                            if (expandChangelog) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = SkyNavy
                        )
                    }

                    AnimatedVisibility(visible = expandChangelog) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ChangelogEntry(
                                version = "v0.98",
                                notes = "Full Android app architecture with real Home Screen Widgets (AppWidgetProvider), notification channels, modular Mascot Canvas renderer with 10 poses, JSON backup export & restore, and Today's Step actionable card."
                            )
                            ChangelogEntry(
                                version = "v0.97",
                                notes = "Dark theme support, lighter body weights with bold titles, segmented step progress pills, and clean status filters."
                            )
                            ChangelogEntry(
                                version = "v0.96",
                                notes = "Altitude system with 10 named ranks from Hatchling to Summit based solely on finished goals. Shield badges and rank-up celebrations."
                            )
                            ChangelogEntry(
                                version = "v0.95",
                                notes = "Today's step actionable card with 'I did it' button. Attention dots on Goals and Calendar tabs. Tap-to-use starter goal templates."
                            )
                            ChangelogEntry(
                                version = "v0.92",
                                notes = "TeenTech demo release with Concept Lab, checklist, and research decisions (survey vote 30-11, no streaks)."
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionAnswerRow(q: String, a: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(q, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black, color = DeepSky))
        Text(a, style = MaterialTheme.typography.bodySmall.copy(color = SkyNavy, lineHeight = 18.sp))
    }
}

@Composable
private fun ChangelogEntry(version: String, notes: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(version, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, color = SkyBlue))
        Text(notes, style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle, lineHeight = 17.sp))
    }
}
