package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.VoidBlack

@Composable
fun ProceduralWallpaper(
    style: String,
    dimPercent: Int = 20,
    accentColor: Color = ElectricCyan,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(VoidBlack)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            when (style) {
                "CYBER_GRID" -> {
                    // Dark base
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF06090F),
                                Color(0xFF0A0F1A),
                                Color(0xFF04060A)
                            )
                        )
                    )

                    // Cybernetic grid lines (spacious 48.dp step)
                    val step = 64f
                    val gridColor = accentColor.copy(alpha = 0.05f)
                    var x = 0f
                    while (x <= width) {
                        drawLine(
                            color = gridColor,
                            start = Offset(x, 0f),
                            end = Offset(x, height),
                            strokeWidth = 1f
                        )
                        x += step
                    }

                    var y = 0f
                    while (y <= height) {
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                        y += step
                    }

                    // Subtle radial center accent glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            center = Offset(width * 0.5f, height * 0.35f),
                            radius = width * 0.65f
                        )
                    )
                }

                "CYAN_AURORA" -> {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.15f),
                                Color(0xFF0B1424),
                                VoidBlack
                            ),
                            center = Offset(width * 0.7f, height * 0.2f),
                            radius = width * 0.9f
                        )
                    )
                }

                "MATRIX_DOTS" -> {
                    // Nothing OS style dot matrix
                    drawRect(color = Color(0xFF08090D))
                    val dotSpacing = 32f
                    val dotColor = Color.White.copy(alpha = 0.07f)
                    var x = dotSpacing / 2
                    while (x < width) {
                        var y = dotSpacing / 2
                        while (y < height) {
                            drawCircle(
                                color = dotColor,
                                radius = 1.2f,
                                center = Offset(x, y)
                            )
                            y += dotSpacing
                        }
                        x += dotSpacing
                    }
                }

                "OBSIDIAN" -> {
                    // Pure deep obsidian with subtle corner vignette
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF090A0D),
                                Color(0xFF030304)
                            )
                        )
                    )
                }

                else -> {
                    // Clean Matte
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0E121A),
                                Color(0xFF07090D)
                            )
                        )
                    )
                }
            }

            // Dimming overlay
            if (dimPercent > 0) {
                drawRect(
                    color = Color.Black.copy(alpha = (dimPercent / 100f).coerceIn(0f, 0.85f))
                )
            }
        }
    }
}
