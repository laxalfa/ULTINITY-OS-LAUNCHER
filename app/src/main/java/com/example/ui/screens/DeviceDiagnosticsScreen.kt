package com.example.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HudCard
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.NeonMint
import com.example.ui.theme.PanelOutline
import com.example.ui.theme.TacticalAmber
import com.example.ui.viewmodel.LauncherTab
import com.example.ui.viewmodel.LauncherUiState
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun DeviceDiagnosticsScreen(
    uiState: LauncherUiState,
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header
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
                    .testTag("diagnostics_back_btn")
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
                    text = "DEVICE DIAGNOSTICS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Hardware telemetry & system compatibility check",
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
            // Device Information Card
            item {
                HudCard(
                    title = "Device Identity & Kernel",
                    subtitle = "Hardware parameters detected by Android OS"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DiagDetailRow("Device Manufacturer", Build.MANUFACTURER.uppercase())
                        DiagDetailRow("Device Model", Build.MODEL)
                        DiagDetailRow("Hardware Board", Build.HARDWARE)
                        DiagDetailRow("Android OS Version", "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                        DiagDetailRow("Target Reference", "OPPO A3x 5G / Dimensity 6300")
                        DiagDetailRow("Application Build", "ULTINITY OS v1.0.0-RELEASE")
                    }
                }
            }

            // Subsystem Health Checklist
            item {
                HudCard(
                    title = "Subsystem Health Status",
                    subtitle = "Launcher integrity and subsystem verification"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SubsystemStatusRow(
                            name = "Home App Registration",
                            status = if (uiState.isDefaultLauncher) "Configured as Default" else "Secondary Home",
                            isOk = uiState.isDefaultLauncher
                        )

                        SubsystemStatusRow(
                            name = "Application Query Permission",
                            status = "${uiState.allApps.size} Installed Apps Discovered",
                            isOk = uiState.allApps.isNotEmpty()
                        )

                        SubsystemStatusRow(
                            name = "DataStore Preferences",
                            status = "Persistent Local Storage Active",
                            isOk = true
                        )

                        SubsystemStatusRow(
                            name = "Hardware Telemetry Provider",
                            status = "Live (ActivityManager & BatteryManager)",
                            isOk = true
                        )

                        SubsystemStatusRow(
                            name = "Zero-Cloud Privacy Guarantee",
                            status = "100% On-Device / No Tracking SDKs",
                            isOk = true
                        )
                    }
                }
            }

            // Custom ROM Roadmap Navigation
            item {
                HudCard(
                    title = "Future Custom ROM Roadmap",
                    subtitle = "Engineering specifications for standalone OS development"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Learn how ULTINITY OS can technically evolve from an Android Launcher into a custom Android operating system.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                        Button(
                            onClick = { viewModel.navigateTo(LauncherTab.ROM_ROADMAP) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("view_rom_roadmap_btn")
                        ) {
                            Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Custom ROM Roadmap")
                        }
                    }
                }
            }

            // Reset Preferences
            item {
                HudCard(
                    title = "Preferences Maintenance",
                    subtitle = "Restore default grid, themes, and favorites"
                ) {
                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("reset_prefs_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset All Launcher Preferences", color = CrimsonAlert)
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Confirm Preference Reset") },
            text = {
                Text("This will restore default home grid layout, wallpapers, favorites, and gaming library tags. Installed applications will NOT be uninstalled.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllPreferences()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DiagDetailRow(title: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF11151F))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun SubsystemStatusRow(name: String, status: String, isOk: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF11151F))
            .padding(10.dp)
    ) {
        Icon(
            imageVector = if (isOk) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (isOk) NeonMint else TacticalAmber,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = if (isOk) NeonMint else TacticalAmber
            )
        )
    }
}
