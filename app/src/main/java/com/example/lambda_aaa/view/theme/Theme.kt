// Provides the Material 3 theme used by every Compose screen.
package com.example.lambda_aaa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    secondary = DarkForestGreen,
    background = WarmBackground,
    surface = WarmBackground,
)

private val DarkColorScheme = darkColorScheme(
    primary = LightGreen,
    secondary = LightGreen,
    background = DarkBackground,
    surface = DarkBackground,
)

/** Applies the application's Material 3 colors and typography. */
@Composable
fun CampsiteReservationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography(),
        content = content,
    )
}
