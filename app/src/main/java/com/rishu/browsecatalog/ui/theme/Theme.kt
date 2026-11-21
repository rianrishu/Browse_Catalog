package com.rishu.browsecatalog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Day = lightColorScheme(
    primary = Color(0xFF325C48),
    background = Color(0xFFFAF9F5),
    surface = Color(0xFFFAF9F5),
    surfaceContainerLow = Color(0xFFF0EFE7),
    onSurface = Color(0xFF222820)
)

private val Night = darkColorScheme(
    primary = Color(0xFFA6CEB4),
    background = Color(0xFF151A16),
    surface = Color(0xFF151A16),
    surfaceContainerLow = Color(0xFF202722)
)

@Composable
fun BrowseCatalogTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) Night else Day, typography = Typography, content = content)
}
