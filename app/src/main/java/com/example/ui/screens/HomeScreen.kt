package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.AppInfo
import com.example.ui.components.AppIconItem
import com.example.ui.theme.NeonMint
import com.example.ui.theme.PanelOutline
import com.example.ui.theme.TacticalAmber
import com.example.ui.viewmodel.LauncherTab
import com.example.ui.viewmodel.LauncherUiState
import com.example.ui.viewmodel.LauncherViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    uiState: LauncherUiState,
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    var currentTimeStr by remember { mutableStateOf("") }
    var currentDateStr by remember { mutableStateOf("") }

    // Live clock ticker
    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTimeStr = timeFormat.format(now)
            currentDateStr = dateFormat.format(now).uppercase()
            delay(1000L)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Top Status HUD
        TopStatusHud(
            isDefaultLauncher = uiState.isDefaultLauncher,
            batteryPercent = uiState.telemetry.batteryPercent,
            isCharging = uiState.telemetry.isCharging,
            onDefaultClick = { viewModel.openDefaultLauncherSettings() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Digital Clock & Date HUD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0C1018).copy(alpha = 0.85f))
                .border(1.dp, PanelOutline, RoundedCornerShape(16.dp))
                .padding(vertical = 16.dp, horizontal = 20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentTimeStr,
                        style = MaterialTheme.typography.displayLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 54.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ULTINITY HUD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (uiState.telemetry.availRamBytes > 0) {
                                val freeGb = uiState.telemetry.availRamBytes / (1024f * 1024f * 1024f)
                                "%.1f GB RAM FREE".format(freeGb)
                            } else "SYSTEM NORMAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentDateStr,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = {
                viewModel.onSearchQueryChanged(it)
                if (it.isNotBlank()) {
                    viewModel.navigateTo(LauncherTab.APP_DRAWER)
                }
            },
            placeholder = {
                Text(
                    text = "Search apps, games, settings...",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B))
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F1420),
                unfocusedContainerColor = Color(0xFF0A0E17),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = PanelOutline
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_search_bar")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Navigation Tabs Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickNavButton(
                icon = Icons.Default.SportsEsports,
                label = "Gaming",
                onClick = { viewModel.navigateTo(LauncherTab.GAMING_CENTER) },
                modifier = Modifier.weight(1f)
            )
            QuickNavButton(
                icon = Icons.Default.Security,
                label = "Privacy",
                onClick = { viewModel.navigateTo(LauncherTab.PRIVACY_CENTER) },
                modifier = Modifier.weight(1f)
            )
            QuickNavButton(
                icon = Icons.Default.Palette,
                label = "Studio",
                onClick = { viewModel.navigateTo(LauncherTab.CUSTOMIZATION) },
                modifier = Modifier.weight(1f)
            )
            QuickNavButton(
                icon = Icons.Default.Build,
                label = "Diagnostics",
                onClick = { viewModel.navigateTo(LauncherTab.DIAGNOSTICS) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pinned Apps / Favorites Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "HOME APPS & FAVORITES",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${uiState.favoriteApps.size} PINNED",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        // Favorites Grid
        val displayApps = if (uiState.favoriteApps.isNotEmpty()) {
            uiState.favoriteApps
        } else {
            uiState.drawerApps.take(8)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(uiState.preferences.gridColumns),
            contentPadding = PaddingValues(bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(displayApps, key = { it.packageName }) { app ->
                AppIconItem(
                    app = app,
                    iconDrawable = viewModel.repository.getAppIcon(app.packageName),
                    iconSize = uiState.preferences.iconSizeDp.dp,
                    showLabel = uiState.preferences.showLabels,
                    onClick = { viewModel.launchApp(app.packageName) },
                    onLongClick = { viewModel.showContextMenu(app) }
                )
            }
        }
    }
}

@Composable
private fun TopStatusHud(
    isDefaultLauncher: Boolean,
    batteryPercent: Int,
    isCharging: Boolean,
    onDefaultClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // ULTINITY Brand
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ULTINITY OS",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Default Launcher status pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isDefaultLauncher) NeonMint.copy(alpha = 0.12f)
                    else TacticalAmber.copy(alpha = 0.15f)
                )
                .border(
                    1.dp,
                    if (isDefaultLauncher) NeonMint.copy(alpha = 0.4f)
                    else TacticalAmber.copy(alpha = 0.6f),
                    RoundedCornerShape(8.dp)
                )
                .clickable { onDefaultClick() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("default_launcher_status_pill")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isDefaultLauncher) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isDefaultLauncher) NeonMint else TacticalAmber,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isDefaultLauncher) "DEFAULT HOME" else "SET AS DEFAULT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isDefaultLauncher) NeonMint else TacticalAmber
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Battery Chip
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF141A26))
                .border(1.dp, PanelOutline, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "$batteryPercent%${if (isCharging) " ⚡" else ""}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
private fun QuickNavButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F1420))
            .border(1.dp, PanelOutline, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp)
            .testTag("nav_btn_${label.lowercase()}")
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}
