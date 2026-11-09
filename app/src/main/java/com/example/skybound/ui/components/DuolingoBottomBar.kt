package com.example.skybound.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class SkyboundTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    GOALS("Goals", Icons.Default.TrackChanges),
    CALENDAR("Calendar", Icons.Default.CalendarToday),
    HISTORY("History", Icons.Default.History)
}

@Composable
fun DuolingoBottomBar(
    currentTab: SkyboundTab,
    onTabSelected: (SkyboundTab) -> Unit,
    onQuickAddClick: () -> Unit,
    hasGoalsAttention: Boolean = false,
    hasCalendarAttention: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Tab 1: Home
            BottomTabItem(
                tab = SkyboundTab.HOME,
                isSelected = currentTab == SkyboundTab.HOME,
                onClick = { onTabSelected(SkyboundTab.HOME) }
            )

            // Tab 2: Goals (with attention dot if unreviewed)
            BottomTabItem(
                tab = SkyboundTab.GOALS,
                isSelected = currentTab == SkyboundTab.GOALS,
                showAttentionDot = hasGoalsAttention,
                onClick = { onTabSelected(SkyboundTab.GOALS) }
            )

            // Center: Chunky 3D Quick-Add Button
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DeepSky)
                    .padding(bottom = 3.dp) // Creates 3D chunky bottom border
                    .clip(RoundedCornerShape(16.dp))
                    .background(SkyBlue)
                    .clickable(onClick = onQuickAddClick)
                    .testTag("quick_add_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Quick add",
                    tint = DeepNavy,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Tab 3: Calendar (with attention dot if session today)
            BottomTabItem(
                tab = SkyboundTab.CALENDAR,
                isSelected = currentTab == SkyboundTab.CALENDAR,
                showAttentionDot = hasCalendarAttention,
                onClick = { onTabSelected(SkyboundTab.CALENDAR) }
            )

            // Tab 4: History
            BottomTabItem(
                tab = SkyboundTab.HISTORY,
                isSelected = currentTab == SkyboundTab.HISTORY,
                onClick = { onTabSelected(SkyboundTab.HISTORY) }
            )
        }
    }
}

@Composable
private fun BottomTabItem(
    tab: SkyboundTab,
    isSelected: Boolean,
    showAttentionDot: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) SkyCloud else Color.Transparent)
            .clickable(onClick = onClick)
            .testTag("tab_${tab.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.title,
            tint = if (isSelected) DeepSky else SkyTextMuted,
            modifier = Modifier.size(26.dp)
        )

        // Attention dot
        if (showAttentionDot) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = 6.dp)
                    .clip(CircleShape)
                    .background(AccentCoral)
            )
        }
    }
}
