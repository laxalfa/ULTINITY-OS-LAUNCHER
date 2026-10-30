package com.example.data.model

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val isGame: Boolean = false,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val installTime: Long = 0L,
    val lastLaunchTime: Long = 0L
)
