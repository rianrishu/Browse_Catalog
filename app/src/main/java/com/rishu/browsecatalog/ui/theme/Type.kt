package com.rishu.browsecatalog.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily

private val defaults = Typography()

val Typography = Typography(
    displaySmall = defaults.displaySmall.copy(fontFamily = FontFamily.Serif),
    headlineLarge = defaults.headlineLarge.copy(fontFamily = FontFamily.Serif),
    headlineMedium = defaults.headlineMedium.copy(fontFamily = FontFamily.Serif)
)
