package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HudCard
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.NeonMint
import com.example.ui.theme.PanelOutline
import com.example.ui.theme.TacticalAmber
import com.example.ui.viewmodel.LauncherUiState
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun PrivacyCenterScreen(
    uiState: LauncherUiState,
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    var showHiddenManager by remember { mutableStateOf(false) }
    var showSetPinDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }

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
                    .testTag("privacy_back_btn")
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
                    text = "PRIVACY & SECURITY",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "GrapheneOS-inspired auditability & local app hiding",
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
            // Local App Hiding Suite
            item {
                HudCard(
                    title = "Launcher App Hiding",
                    subtitle = "Hide sensitive apps from the drawer and search",
                    statusBadge = "${uiState.hiddenApps.size} HIDDEN"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Hidden applications will not appear in the ULTINITY app drawer or quick search results. They can only be managed here.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )

                        // Honest Android Limitation Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF141A26))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Android Limitation: Hiding in ULTINITY OS hides the application from this launcher interface only. The application remains installed on the device and visible in Android Settings.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TacticalAmber,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Button(
                            onClick = { showHiddenManager = !showHiddenManager },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("manage_hidden_apps_btn")
                        ) {
                            Icon(
                                imageVector = if (showHiddenManager) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (showHiddenManager) "Done Managing Hidden Apps" else "Select Apps to Hide / Restore")
                        }

                        if (showHiddenManager) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap an app to toggle its hidden state:",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                uiState.allApps.forEach { app ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (app.isHidden) CrimsonAlert.copy(alpha = 0.12f) else Color(0xFF11151F))
                                            .clickable { viewModel.toggleHidden(app.packageName) }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = app.label,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurface
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (app.isHidden) {
                                            Text(
                                                text = "HIDDEN",
                                                style = MaterialTheme.typography.labelSmall.copy(color = CrimsonAlert)
                                            )
                                        } else {
                                            Text(
                                                text = "VISIBLE",
                                                style = MaterialTheme.typography.labelSmall.copy(color = NeonMint)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Local Security PIN Setup
            item {
                HudCard(
                    title = "Local Security PIN",
                    subtitle = "Protect Privacy Center and Hidden Apps list",
                    statusBadge = if (uiState.preferences.pinEnabled) "PIN ACTIVE" else "NO PIN"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (uiState.preferences.pinEnabled) NeonMint else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (uiState.preferences.pinEnabled) "Privacy Area Protected" else "Privacy Area Unprotected",
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
                                )
                                Text(
                                    text = if (uiState.preferences.pinEnabled) "PIN is required to open this screen" else "Anyone with your phone can view hidden apps",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        if (!showSetPinDialog) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = { showSetPinDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).testTag("setup_pin_btn")
                                ) {
                                    Text(if (uiState.preferences.pinEnabled) "Change PIN" else "Set Security PIN")
                                }

                                if (uiState.preferences.pinEnabled) {
                                    OutlinedButton(
                                        onClick = { viewModel.clearPin() },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).testTag("remove_pin_btn")
                                    ) {
                                        Text("Remove PIN", color = CrimsonAlert)
                                    }
                                }
                            }
                        } else {
                            // Set PIN Inline Form
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF131822))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Enter 4-8 Digit PIN:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = newPinInput,
                                    onValueChange = { if (it.length <= 8) newPinInput = it },
                                    label = { Text("New PIN") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("new_pin_input")
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            if (newPinInput.isNotBlank()) {
                                                viewModel.setPin(newPinInput)
                                                showSetPinDialog = false
                                                newPinInput = ""
                                            }
                                        },
                                        enabled = newPinInput.length >= 4,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Save PIN")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            showSetPinDialog = false
                                            newPinInput = ""
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            }
                        }

                        if (uiState.preferences.pinEnabled) {
                            OutlinedButton(
                                onClick = { viewModel.lockPrivateAreas() },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Lock Privacy Controls Now")
                            }
                        }
                    }
                }
            }

            // Real Permission Transparency Audit
            item {
                HudCard(
                    title = "Permission Audit",
                    subtitle = "What ULTINITY OS accesses vs Android system"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PermissionAuditRow(
                            name = "Installed Applications (Package Visibility)",
                            status = "Granted (Launcher Core)",
                            detail = "Required to discover and launch apps installed on your device.",
                            isSafe = true
                        )
                        PermissionAuditRow(
                            name = "Network State (Diagnostics)",
                            status = "Read-Only Check",
                            detail = "Used solely to display offline/cellular/Wi-Fi status on the dashboard.",
                            isSafe = true
                        )
                        PermissionAuditRow(
                            name = "Haptic Vibrator",
                            status = "Active",
                            detail = "Provides tactile feedback on button presses and long taps.",
                            isSafe = true
                        )
                        PermissionAuditRow(
                            name = "Telemetry & Cloud Tracking",
                            status = "None / Zero Analytics",
                            detail = "All user preferences and game logs remain strictly on this device.",
                            isSafe = true
                        )
                    }
                }
            }

            // Android System Privacy Shortcuts
            item {
                HudCard(
                    title = "Android Privacy Shortcuts",
                    subtitle = "Direct access to official ColorOS / Android privacy screens"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrivacyShortcutRow(
                            icon = Icons.Default.Security,
                            title = "Android Privacy Dashboard",
                            detail = "Review camera, microphone, and location access by all apps",
                            onClick = { viewModel.openSystemSettings("android.settings.PRIVACY_SETTINGS") }
                        )

                        PrivacyShortcutRow(
                            icon = Icons.Default.Info,
                            title = "App Permissions Manager",
                            detail = "Manage granular runtime permissions for third-party apps",
                            onClick = { viewModel.openSystemSettings("android.settings.MANAGE_APPLICATIONS_SETTINGS") }
                        )

                        PrivacyShortcutRow(
                            icon = Icons.Default.LocationOn,
                            title = "Location Services",
                            detail = "Manage GPS toggles and high-accuracy device location",
                            onClick = { viewModel.openSystemSettings("android.settings.LOCATION_SOURCE_SETTINGS") }
                        )

                        PrivacyShortcutRow(
                            icon = Icons.Default.Fingerprint,
                            title = "Screen Lock & Security",
                            detail = "Configure device PIN, pattern, or fingerprint",
                            onClick = { viewModel.openSystemSettings("android.settings.SECURITY_SETTINGS") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionAuditRow(
    name: String,
    status: String,
    detail: String,
    isSafe: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF11151F))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isSafe) NeonMint else TacticalAmber,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = status.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSafe) NeonMint else TacticalAmber,
                    fontSize = 9.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = detail,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun PrivacyShortcutRow(
    icon: ImageVector,
    title: String,
    detail: String,
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
                text = detail,
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
