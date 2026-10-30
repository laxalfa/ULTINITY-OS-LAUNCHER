package com.example.data.model

data class GamingSession(
    val id: String = System.currentTimeMillis().toString(),
    val packageName: String,
    val gameName: String,
    val startTimeMs: Long,
    val durationSeconds: Long = 0L,
    val batteryStartPercent: Int = 0,
    val batteryEndPercent: Int = 0,
    val notes: String = ""
) {
    val batteryDelta: Int
        get() = (batteryStartPercent - batteryEndPercent).coerceAtLeast(0)
}
