package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppInfo
import com.example.data.model.ProfileMode
import com.example.ui.components.AppIconItem
import com.example.ui.components.HudCard
import com.example.ui.components.MetricGauge
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.NeonMint
import com.example.ui.theme.PanelOutline
import com.example.ui.theme.TacticalAmber
import com.example.ui.viewmodel.LauncherUiState
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun GamingCenterScreen(
    uiState: LauncherUiState,
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    var showAddGameSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header with Back button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F1420))
                    .border(1.dp, PanelOutline, RoundedCornerShape(10.dp))
                    .size(44.dp)
                    .testTag("gaming_center_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "GAMING CENTER",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "ULTINITY Tactical Command & Telemetry",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Hero Banner Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, PanelOutline, RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ultinity_gaming_banner_1793339176910),
                        contentDescription = "Tactical Command Center Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.align(Alignment.BottomStart)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "LIVE SESSION READY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Black
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "OPPO A3x 5G / Dimensity 6300 Profile Active",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Active Gaming Session Widget
            item {
                ActiveSessionCard(
                    uiState = uiState,
                    onStopSession = { viewModel.stopGamingSession() }
                )
            }

            // Game Library Section
            item {
                HudCard(
                    title = "Game Library",
                    subtitle = "Marked installed games ready for launch",
                    statusBadge = "${uiState.gameApps.size} GAMES"
                ) {
                    if (uiState.gameApps.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "No games added yet",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showAddGameSheet = !showAddGameSheet },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("add_first_game_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Select Games from Installed Apps")
                            }
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(uiState.gameApps, key = { it.packageName }) { game ->
                                AppIconItem(
                                    app = game,
                                    iconDrawable = viewModel.repository.getAppIcon(game.packageName),
                                    iconSize = 56.dp,
                                    showLabel = true,
                                    onClick = {
                                        viewModel.startGamingSession(game.packageName, game.label)
                                    },
                                    onLongClick = { viewModel.showContextMenu(game) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { showAddGameSheet = !showAddGameSheet },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("manage_game_library_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (showAddGameSheet) "Close Library Selector" else "Add / Remove Games")
                        }
                    }

                    if (showAddGameSheet) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tap to toggle games in your library:",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            uiState.allApps.take(15).forEach { app ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (app.isGame) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color(0xFF131822))
                                        .clickable { viewModel.toggleGame(app.packageName) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = app.label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (app.isGame) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Added",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "+ ADD",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Game Profiles Selector
            item {
                HudCard(
                    title = "Gaming Profile",
                    subtitle = "Select behavior mode for launcher & monitoring",
                    statusBadge = uiState.preferences.activeProfileMode.name
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProfileMode.values().forEach { mode ->
                            val isSelected = uiState.preferences.activeProfileMode == mode
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color(0xFF121622))
                                    .border(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else PanelOutline,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.updateProfileMode(mode) }
                                    .padding(12.dp)
                                    .testTag("profile_mode_${mode.name.lowercase()}")
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = mode.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        if (isSelected) {
                                            Text(
                                                text = "ACTIVE",
                                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = mode.description,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Real Hardware Telemetry Gauges
            item {
                HudCard(
                    title = "Live Hardware Telemetry",
                    subtitle = "Permitted Android system & battery metrics",
                    statusBadge = "GENUINE METRICS"
                ) {
                    val ramTotalGb = uiState.telemetry.totalRamBytes / (1024f * 1024f * 1024f)
                    val ramAvailGb = uiState.telemetry.availRamBytes / (1024f * 1024f * 1024f)
                    val ramUsedGb = (ramTotalGb - ramAvailGb).coerceAtLeast(0f)
                    val ramPercent = if (ramTotalGb > 0f) ramUsedGb / ramTotalGb else 0f

                    val storageTotalGb = uiState.telemetry.totalStorageBytes / (1024f * 1024f * 1024f)
                    val storageAvailGb = uiState.telemetry.availStorageBytes / (1024f * 1024f * 1024f)
                    val storageUsedGb = (storageTotalGb - storageAvailGb).coerceAtLeast(0f)
                    val storagePercent = if (storageTotalGb > 0f) storageUsedGb / storageTotalGb else 0f

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricGauge(
                            title = "System RAM Usage",
                            valueText = "%.2f GB / %.2f GB".format(ramUsedGb, ramTotalGb),
                            detailText = "%.0f%%".format(ramPercent * 100f),
                            percent = ramPercent,
                            badge = if (uiState.telemetry.isLowRam) "LOW RAM" else "NORMAL"
                        )

                        MetricGauge(
                            title = "Battery Level & Health",
                            valueText = "${uiState.telemetry.batteryPercent}% (${uiState.telemetry.chargeSource})",
                            detailText = "%.1f°C | %d mV".format(uiState.telemetry.batteryTempC, uiState.telemetry.batteryVoltageMv),
                            percent = uiState.telemetry.batteryPercent / 100f,
                            badge = if (uiState.telemetry.isCharging) "CHARGING" else "DISCHARGING"
                        )

                        MetricGauge(
                            title = "Internal Storage",
                            valueText = "%.1f GB Free".format(storageAvailGb),
                            detailText = "Total %.0f GB".format(storageTotalGb),
                            percent = storagePercent,
                            badge = "STORAGE STAT"
                        )

                        // Honest Sandbox Disclosure
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF141A26))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Notice: Device FPS and CPU frequency scaling are restricted by the Android OS sandbox for non-root apps. Live memory, battery, and storage telemetry above are directly read from official Android APIs.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Quick Gaming Comfort & Focus Shortcuts
            item {
                HudCard(
                    title = "Comfort & Focus Controls",
                    subtitle = "System settings shortcuts for optimal play session"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ComfortShortcutRow(
                            icon = Icons.Default.BrightnessHigh,
                            title = "Display & Refresh Rate",
                            subtitle = "Adjust brightness & screen timeout",
                            onClick = { viewModel.openSystemSettings("android.settings.DISPLAY_SETTINGS") }
                        )

                        ComfortShortcutRow(
                            icon = Icons.Default.DoNotDisturb,
                            title = "Do Not Disturb / Focus Mode",
                            subtitle = "Configure notifications & silence alerts",
                            onClick = { viewModel.openSystemSettings("android.settings.ZEN_MODE_PRIORITY_SETTINGS") }
                        )

                        ComfortShortcutRow(
                            icon = Icons.Default.BatteryAlert,
                            title = "Android Battery Saver",
                            subtitle = "View battery usage & ColorOS power modes",
                            onClick = { viewModel.openSystemSettings("android.settings.BATTERY_SAVER_SETTINGS") }
                        )
                    }
                }
            }

            // Session History Section
            if (uiState.sessionHistory.isNotEmpty()) {
                item {
                    HudCard(
                        title = "Recent Sessions Log",
                        subtitle = "Tracked game duration and battery drain",
                        statusBadge = "${uiState.sessionHistory.size} LOGGED"
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.sessionHistory.take(5).forEach { session ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF11151F))
                                        .padding(10.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = session.gameName,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = "Duration: ${session.durationSeconds / 60}m ${session.durationSeconds % 60}s",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    if (session.batteryDelta > 0) {
                                        Text(
                                            text = "-${session.batteryDelta}% BATTERY",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TacticalAmber
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
    }
}

@Composable
private fun ActiveSessionCard(
    uiState: LauncherUiState,
    onStopSession: () -> Unit
) {
    val session = uiState.activeSession

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F1522))
            .border(
                1.dp,
                if (session != null) MaterialTheme.colorScheme.primary else PanelOutline,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        if (session != null) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(NeonMint)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ACTIVE SESSION: ${session.gameName.uppercase()}",
                        style = MaterialTheme.typography.labelMedium.copy(color = NeonMint)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val minutes = uiState.sessionElapsedSeconds / 60
                val seconds = uiState.sessionElapsedSeconds % 60

                Text(
                    text = "%02d:%02d".format(minutes, seconds),
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onStopSession,
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("end_session_btn")
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("End & Log Session")
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "NO ACTIVE SESSION",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Launch a game from your library to track playtime and battery usage.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ComfortShortcutRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF11151F))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Text(
            text = "OPEN",
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
        )
    }
}
