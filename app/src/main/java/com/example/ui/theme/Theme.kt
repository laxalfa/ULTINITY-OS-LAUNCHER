package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AccentChoice(val label: String, val primary: Color, val glow: Color) {
    CYAN("Electric Cyan", ElectricCyan, CyanGlow),
    MINT("Matrix Mint", NeonMint, Color(0xFF00C853)),
    PURPLE("Cyber Purple", CyberPurple, Color(0xFF9D00FF)),
    MONOCHROME("Titanium White", TitaniumWhite, Color(0xFFB0BEC5))
}

@Composable
fun UltinityTheme(
    darkTheme: Boolean = true,
    accentChoice: AccentChoice = AccentChoice.CYAN,
    content: @Composable () -> Unit
) {
    val primaryColor = accentChoice.primary

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = VoidBlack,
            primaryContainer = CharcoalSurfaceVariant,
            onPrimaryContainer = primaryColor,
            secondary = CyanGlow,
            onSecondary = VoidBlack,
            background = VoidBlack,
            onBackground = TextPrimary,
            surface = CharcoalSurface,
            onSurface = TextPrimary,
            surfaceVariant = CharcoalSurfaceVariant,
            onSurfaceVariant = TextSecondary,
            outline = PanelOutline,
            outlineVariant = PanelOutlineGlow,
            error = CrimsonAlert,
            onError = TextPrimary
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = TechWhite,
            primaryContainer = TechSurfaceVariant,
            onPrimaryContainer = TechTextPrimary,
            secondary = CyanGlow,
            onSecondary = TechWhite,
            background = TechWhite,
            onBackground = TechTextPrimary,
            surface = TechSurface,
            onSurface = TechTextPrimary,
            surfaceVariant = TechSurfaceVariant,
            onSurfaceVariant = TechTextSecondary,
            outline = TechOutline,
            outlineVariant = Color(0xFF94A3B8),
            error = CrimsonAlert,
            onError = TechWhite
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
