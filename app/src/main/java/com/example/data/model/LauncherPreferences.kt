package com.example.data.model

data class LauncherPreferences(
    val gridColumns: Int = 4,
    val iconSizeDp: Int = 52,
    val showLabels: Boolean = true,
    val wallpaperStyle: String = "CYBER_GRID",
    val customWallpaperUri: String? = null,
    val wallpaperDimPercent: Int = 20,
    val accentName: String = "CYAN",
    val isDarkTheme: Boolean = true,
    val reducedMotion: Boolean = false,
    val pinHash: String = "",
    val pinEnabled: Boolean = false,
    val favoritePackages: Set<String> = emptySet(),
    val hiddenPackages: Set<String> = emptySet(),
    val gamePackages: Set<String> = emptySet(),
    val dockPackages: List<String> = emptyList(),
    val activeProfileMode: ProfileMode = ProfileMode.BALANCED,
    val breakAlertMinutes: Int = 45
)
