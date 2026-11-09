package com.example.skybound.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.RestartAlt
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
import com.example.skybound.data.AppSettingsEntity
import com.example.skybound.mascot.*
import com.example.skybound.model.ACCENT_PALETTE
import com.example.ui.theme.*

@Composable
fun AppearanceScreen(
    appSettings: AppSettingsEntity,
    onUpdateAppearance: (appName: String?, accent: String?, theme: String?, corners: String?, font: String?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var appNameInput by remember(appSettings.appName) { mutableStateOf(appSettings.appName) }
    var selectedPosePreview by remember { mutableStateOf(MascotPose.HAPPY) }
    var mascotSpokenText by remember { mutableStateOf("Customize my crest, eyes, wings, and accessories below!") }
    var activeCustomizerTab by remember { mutableStateOf(0) } // 0: Palette, 1: Crest, 2: Eyes, 3: Wings, 4: Accessory, 5: Beak

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("appearance_screen"),
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
                    text = "Appearance & Mascot Studio",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = SkyNavy
                    )
                )
            }
        }

        // Section 1: Mascot Studio & Modular Part Customizer (FEATURED HIGHLIGHT)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Mascot Studio",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                            )
                            Text(
                                text = "Every bit of the mascot is swappable in real-time!",
                                style = MaterialTheme.typography.bodySmall.copy(color = SkyTextMuted)
                            )
                        }

                        // Randomize & Reset Action Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    MascotModularState.randomize()
                                    mascotSpokenText = "Look at my new style! Fresh feathers!"
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SkyCloud)
                            ) {
                                Icon(Icons.Default.Casino, contentDescription = "Randomize", tint = SkyBlue, modifier = Modifier.size(20.dp))
                            }

                            IconButton(
                                onClick = {
                                    MascotModularState.resetToDefaults()
                                    mascotSpokenText = "Back to official Skybound blue!"
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SkyCloud)
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = "Reset", tint = SkyTextMuted, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    // Main Mascot Live Preview Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = if (MascotModularState.isFlyingLauncherMode) (if (SkyboundThemeState.isDark) Color(0xFF14293D) else Color(0xFFE3F2FD)) else SkyCloud,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (MascotModularState.isFlyingLauncherMode) SkyBlue else SkyMist
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mode chip
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (MascotModularState.isFlyingLauncherMode) AccentMint else SkyBlue
                                ) {
                                    Text(
                                        text = if (MascotModularState.isFlyingLauncherMode) "🚀 FLYING LAUNCHER" else "✨ IDLE LIVE PREVIEW",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            color = DeepNavy,
                                            fontSize = 10.sp
                                        )
                                    )
                                }

                                // Toggle Flying Launcher Button
                                Button(
                                    onClick = {
                                        MascotModularState.isFlyingLauncherMode = !MascotModularState.isFlyingLauncherMode
                                        mascotSpokenText = if (MascotModularState.isFlyingLauncherMode)
                                            "Whoosh! Soaring high in launcher flight mode!"
                                        else
                                            "Landed safely on two feet!"
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (MascotModularState.isFlyingLauncherMode) DeepNavy else (if (SkyboundThemeState.isDark) DarkCard else Color.White)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(
                                        Icons.Default.FlightTakeoff,
                                        contentDescription = "Toggle flight",
                                        tint = if (MascotModularState.isFlyingLauncherMode) Color.White else SkyNavy,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (MascotModularState.isFlyingLauncherMode) "Land" else "Test Flying",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = if (MascotModularState.isFlyingLauncherMode) Color.White else SkyNavy
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Large mascot with tap hop
                            MascotView(
                                pose = selectedPosePreview,
                                size = 104.dp,
                                isInteractive = true,
                                onSpeechChange = { quote ->
                                    mascotSpokenText = quote
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Interactive speech bubble
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    text = mascotSpokenText,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SkyNavy
                                    )
                                )
                            }
                        }
                    }

                    // Part Customization Tab Selector
                    val tabTitles = listOf("Color Palettes", "Crest / Head", "Eyes", "Wings", "Accessories", "Beak")
                    ScrollableTabRow(
                        selectedTabIndex = activeCustomizerTab,
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent,
                        contentColor = SkyNavy,
                        divider = {}
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = activeCustomizerTab == index,
                                onClick = { activeCustomizerTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (activeCustomizerTab == index) FontWeight.Black else FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            )
                        }
                    }

                    // Sub-sections based on active tab
                    when (activeCustomizerTab) {
                        // 0: Color Palettes
                        0 -> {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(MascotColorPalette.values()) { pal ->
                                    val isSelected = MascotModularState.palette == pal
                                    Column(
                                        modifier = Modifier
                                            .width(96.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable {
                                                MascotModularState.applyPreset(
                                                    newPalette = pal,
                                                    newCrest = MascotModularState.crest,
                                                    newEyes = MascotModularState.eyes,
                                                    newAccessory = MascotModularState.accessory
                                                )
                                                mascotSpokenText = "Switched to ${pal.title}!"
                                            }
                                            .background(if (isSelected) SkyCloud else Color.Transparent)
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) SkyNavy else SkyMist,
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            .padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(pal.body))
                                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(pal.wing))
                                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(pal.beak))
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = pal.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = SkyNavy,
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        // 1: Head Crest
                        1 -> {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(MascotCrest.values()) { crest ->
                                    val isSelected = MascotModularState.crest == crest
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                MascotModularState.crest = crest
                                                mascotSpokenText = "Rocking the ${crest.title}!"
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) SkyBlue else SkyCloud,
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) DeepSky else SkyMist)
                                    ) {
                                        Text(
                                            text = crest.title,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = if (isSelected) DeepNavy else SkyNavy
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // 2: Eyes
                        2 -> {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(MascotEyes.values()) { eyes ->
                                    val isSelected = MascotModularState.eyes == eyes
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                MascotModularState.eyes = eyes
                                                mascotSpokenText = "Eyes changed to ${eyes.title}!"
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) SkyBlue else SkyCloud,
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) DeepSky else SkyMist)
                                    ) {
                                        Text(
                                            text = eyes.title,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = if (isSelected) DeepNavy else SkyNavy
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // 3: Wings
                        3 -> {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(MascotWings.values()) { wing ->
                                    val isSelected = MascotModularState.wings == wing
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                MascotModularState.wings = wing
                                                mascotSpokenText = "Wings set to ${wing.title}!"
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) SkyBlue else SkyCloud,
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) DeepSky else SkyMist)
                                    ) {
                                        Text(
                                            text = wing.title,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = if (isSelected) DeepNavy else SkyNavy
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // 4: Accessories
                        4 -> {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(MascotAccessory.values()) { acc ->
                                    val isSelected = MascotModularState.accessory == acc
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                MascotModularState.accessory = acc
                                                mascotSpokenText = if (acc == MascotAccessory.NONE)
                                                    "Accessory removed!"
                                                else
                                                    "Equipped ${acc.title}!"
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) SkyBlue else SkyCloud,
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) DeepSky else SkyMist)
                                    ) {
                                        Text(
                                            text = acc.title,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = if (isSelected) DeepNavy else SkyNavy
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // 5: Beak
                        5 -> {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(MascotBeak.values()) { beak ->
                                    val isSelected = MascotModularState.beak == beak
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                MascotModularState.beak = beak
                                                mascotSpokenText = "Beak changed to ${beak.title}!"
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) SkyBlue else SkyCloud,
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) DeepSky else SkyMist)
                                    ) {
                                        Text(
                                            text = beak.title,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = if (isSelected) DeepNavy else SkyNavy
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Accent Color Picker
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
                        text = "Accent Color",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ACCENT_PALETTE.forEach { (key, triple) ->
                            val isSel = appSettings.accentKey == key
                            val color = Color(android.graphics.Color.parseColor(triple.first))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onUpdateAppearance(null, key, null, null, null) }
                                    .background(if (isSel) SkyCloud else Color.Transparent)
                                    .border(
                                        width = if (isSel) 2.dp else 1.dp,
                                        color = if (isSel) SkyNavy else SkyMist,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = triple.third,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSel) FontWeight.Black else FontWeight.Bold,
                                        color = SkyNavy,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Theme (Auto / Light / Dark)
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
                        text = "Theme Mode",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "auto" to "Auto (System)",
                            "light" to "Light",
                            "dark" to "Dark"
                        ).forEach { (mode, label) ->
                            val isSel = appSettings.themeMode == mode
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onUpdateAppearance(null, null, mode, null, null) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) SkyBlue else SkyCloud,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSel) DeepSky else SkyMist)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(label, fontWeight = FontWeight.Black, fontSize = 13.sp, color = SkyNavy)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Corner Shape Styles
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
                        text = "Corner Style",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "round" to "Round",
                            "soft" to "Soft",
                            "sharp" to "Sharp"
                        ).forEach { (style, label) ->
                            val isSel = appSettings.cornerStyle == style
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onUpdateAppearance(null, null, null, style, null) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) SkyBlue else SkyCloud,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSel) DeepSky else SkyMist)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(label, fontWeight = FontWeight.Black, fontSize = 13.sp, color = SkyNavy)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 5: App Name & Custom Branding
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
                        text = "App Name",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )

                    OutlinedTextField(
                        value = appNameInput,
                        onValueChange = { appNameInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (appNameInput.isNotBlank()) {
                                onUpdateAppearance(appNameInput.trim(), null, null, null, null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) {
                        Text("Update App Name", fontWeight = FontWeight.Black, color = SkyNavy)
                    }
                }
            }
        }

        // Section 6: Mascot Poses & Interactive Gallery
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "10 Expressive Mascot Poses",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )

                    Text(
                        text = "Notice how your custom parts and palette reflect across all poses:",
                        style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                    )

                    // 10 Pose Thumbnails
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MascotPose.values().toList().chunked(2).forEach { rowPoses ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowPoses.forEach { pose ->
                                    val isCurrent = selectedPosePreview == pose
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable {
                                                selectedPosePreview = pose
                                                val quotes = pose.getInteractiveQuotes()
                                                mascotSpokenText = quotes.first()
                                            },
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isCurrent) SkyCloud else MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(
                                            if (isCurrent) 2.dp else 1.dp,
                                            if (isCurrent) SkyBlue else SkyMist
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            MascotView(pose = pose, size = 52.dp, isInteractive = false)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = pose.title,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Black,
                                                    color = SkyNavy
                                                )
                                            )
                                            Text(
                                                text = pose.description,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    color = SkyTextMuted
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
