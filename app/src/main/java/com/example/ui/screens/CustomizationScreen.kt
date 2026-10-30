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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HudCard
import com.example.ui.theme.AccentChoice
import com.example.ui.theme.PanelOutline
import com.example.ui.viewmodel.LauncherUiState
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun CustomizationScreen(
    uiState: LauncherUiState,
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
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
                    .testTag("customization_back_btn")
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
                    text = "CUSTOMIZATION STUDIO",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Wallpapers, cyber accents, and grid layout",
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
            // Accent Color Palette
            item {
                HudCard(
                    title = "System Accent Glow",
                    subtitle = "Select primary HUD and telemetry accent color"
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AccentChoice.values().forEach { choice ->
                            val isSelected = uiState.preferences.accentName == choice.name
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.updateAccent(choice.name) }
                                    .padding(8.dp)
                                    .testTag("accent_color_${choice.name.lowercase()}")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(choice.primary)
                                        .border(
                                            2.dp,
                                            if (isSelected) Color.White else Color.Transparent,
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = choice.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Procedural Wallpaper Presets
            item {
                HudCard(
                    title = "Procedural Wallpaper",
                    subtitle = "High-performance Canvas backgrounds engineered for zero battery drain"
                ) {
                    val wallpapers = listOf(
                        "CYBER_GRID" to "Cyber Grid",
                        "MATRIX_DOTS" to "Matrix Dots",
                        "CYAN_AURORA" to "Cyan Aurora",
                        "OBSIDIAN" to "Deep Obsidian",
                        "DARK_MATTE" to "Matte Charcoal"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        wallpapers.forEach { (styleKey, styleTitle) ->
                            val isSelected = uiState.preferences.wallpaperStyle == styleKey
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color(0xFF11151F))
                                    .border(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else PanelOutline,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.updateWallpaperStyle(styleKey) }
                                    .padding(12.dp)
                                    .testTag("wallpaper_style_${styleKey.lowercase()}")
                            ) {
                                Text(
                                    text = styleTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Text(
                                        text = "SELECTED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Wallpaper Dimming: ${uiState.preferences.wallpaperDimPercent}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Slider(
                            value = uiState.preferences.wallpaperDimPercent.toFloat(),
                            onValueChange = { viewModel.updateWallpaperDim(it.toInt()) },
                            valueRange = 0f..70f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("wallpaper_dim_slider")
                        )
                    }
                }
            }

            // Grid & Icon Sizing
            item {
                HudCard(
                    title = "App Grid & Icon Scale",
                    subtitle = "Adjust layout columns and touch icon diameter"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Grid Columns
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Grid Columns",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(4, 5).forEach { cols ->
                                    val isSelected = uiState.preferences.gridColumns == cols
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF141A26))
                                            .clickable { viewModel.updateGridColumns(cols) }
                                            .padding(horizontal = 14.dp, vertical = 6.dp)
                                            .testTag("grid_col_$cols")
                                    ) {
                                        Text(
                                            text = "${cols}x${cols + 1}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.Black else Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Icon Sizes
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Icon Scale",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(44 to "S", 52 to "M", 60 to "L").forEach { (sizeDp, label) ->
                                    val isSelected = uiState.preferences.iconSizeDp == sizeDp
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF141A26))
                                            .clickable { viewModel.updateIconSize(sizeDp) }
                                            .padding(horizontal = 14.dp, vertical = 6.dp)
                                            .testTag("icon_size_${label.lowercase()}")
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.Black else Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Show App Labels
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Show Application Labels",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "Toggle text labels under home & drawer icons",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Switch(
                                checked = uiState.preferences.showLabels,
                                onCheckedChange = { viewModel.updateShowLabels(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("show_labels_switch")
                            )
                        }

                        // Reduced Motion
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reduced Motion Mode",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "Optimized for budget SoCs like Dimensity 6300",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Switch(
                                checked = uiState.preferences.reducedMotion,
                                onCheckedChange = { viewModel.updateReducedMotion(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("reduced_motion_switch")
                            )
                        }
                    }
                }
            }
        }
    }
}
