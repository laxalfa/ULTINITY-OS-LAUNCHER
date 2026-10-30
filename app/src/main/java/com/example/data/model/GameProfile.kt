package com.example.data.model

enum class ProfileMode(
    val title: String,
    val subtitle: String,
    val description: String
) {
    BALANCED(
        title = "Balanced",
        subtitle = "Standard launcher behavior",
        description = "Provides full UI responsiveness and normal system telemetry monitoring without altering standard Android power curves."
    ),
    BATTERY_SAVER(
        title = "Battery Saver",
        subtitle = "Energy-aware launcher mode",
        description = "Slows telemetry refresh rates to 15s intervals, pauses procedural background effects, and dims launcher brightness."
    ),
    DISTRACTION_FREE(
        title = "Distraction-Free",
        subtitle = "Focus & Do Not Disturb shortcuts",
        description = "Offers direct one-tap toggles to Android system Do Not Disturb and notification silencing settings."
    ),
    PERFORMANCE_MONITORING(
        title = "Tactical Telemetry",
        subtitle = "Live hardware metrics HUD",
        description = "Renders genuine memory, battery voltage/temp, and storage metrics permitted by the Android OS sandbox."
    )
}
