package com.example.ui.screens

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
import androidx.compose.material.icons.filled.BuildCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HudCard
import com.example.ui.theme.PanelOutline
import com.example.ui.theme.TacticalAmber
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun CustomRomRoadmapScreen(
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
                    .testTag("rom_roadmap_back_btn")
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
                    text = "CUSTOM ROM ROADMAP",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Standalone OS Architecture & Hardware Integration",
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
            // Critical Engineering Boundary Disclosure
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1F150A))
                        .border(1.dp, TacticalAmber.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = TacticalAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ENGINEERING BOUNDARY NOTICE",
                                style = MaterialTheme.typography.labelMedium.copy(color = TacticalAmber)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "The current ULTINITY OS release is a high-performance Android application operating within the standard Android security sandbox. Custom operating system features (such as kernel-level CPU governors, custom recovery, and OS-level security) require an independent Custom Android ROM built from source and flashed to system partitions.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFFF5E6CC),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            // Phase 1: Device Tree & Kernel Sources
            item {
                RoadmapCard(
                    icon = Icons.Default.Memory,
                    title = "1. Kernel & Device Trees",
                    subtitle = "MediaTek MT6835 / Dimensity 6300 Integration",
                    points = listOf(
                        "Upstream kernel sources release by manufacturer (GPL compliance audit).",
                        "Extract proprietary vendor firmware blobs (radios, GPU Mali-G57 drivers, camera HAL).",
                        "Create device-tree repositories (device/oppo/CPH2681) mapping partition tables."
                    )
                )
            }

            // Phase 2: Bootloader & Partition Architecture
            item {
                RoadmapCard(
                    icon = Icons.Default.DeveloperBoard,
                    title = "2. Bootloader & Fastbootd",
                    subtitle = "Dynamic Partitions & A/B Seamless Updates",
                    points = listOf(
                        "Bootloader unlock validation policy (EDL / MediaTek DA auth constraints).",
                        "Super partition repacking (system, vendor, product, system_ext).",
                        "Custom recovery creation (TWRP / OrangeFox / AOSP Recovery) compatible with Android 14 encryption."
                    )
                )
            }

            // Phase 3: Hardware Abstraction Layer (HAL)
            item {
                RoadmapCard(
                    icon = Icons.Default.BuildCircle,
                    title = "3. HAL Adaptation & Telemetry",
                    subtitle = "Direct Hardware Telemetry & Audio/Display Drivers",
                    points = listOf(
                        "Direct thermal zone sensors (/sys/class/thermal/thermal_zone*) interfacing.",
                        "Hardware-level CPU/GPU governor tuning without root sandbox restrictions.",
                        "ColorOS proprietary display refresh rate & touch sampling rate driver adaptation."
                    )
                )
            }

            // Phase 4: GrapheneOS-Grade Security & SELinux
            item {
                RoadmapCard(
                    icon = Icons.Default.Security,
                    title = "4. SELinux & Android Verified Boot",
                    subtitle = "Zero-Trust System Integrity",
                    points = listOf(
                        "Enforcing SELinux policy audit with zero permissive domains.",
                        "AVB (Android Verified Boot 2.0) with custom user root-of-trust signing keys.",
                        "Hardened memory allocator (hardened_malloc) and sandboxed Google Play services."
                    )
                )
            }

            // Phase 5: Build Automation
            item {
                RoadmapCard(
                    icon = Icons.Default.Code,
                    title = "5. AOSP Source Tree Automation",
                    subtitle = "Automated Continuous Integration",
                    points = listOf(
                        "Lunch build target configuration for Dimensity 6300 userdebug/user builds.",
                        "Over-The-Air (OTA) update server infrastructure with cryptographic signature verification.",
                        "SafetyNet / Play Integrity API attestation planning."
                    )
                )
            }
        }
    }
}

@Composable
private fun RoadmapCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    points: List<String>
) {
    HudCard(
        title = title,
        subtitle = subtitle
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            points.forEach { point ->
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .size(5.dp)
                            .clip(RoundedCornerShape(2.5.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
