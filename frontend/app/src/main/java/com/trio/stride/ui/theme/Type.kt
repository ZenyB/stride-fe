package com.trio.stride.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.trio.stride.R

val displayFontFamily = FontFamily(
    Font(R.font.roboto_extra_bold)
)

val headlineFontFamily = FontFamily(
    Font(R.font.roboto_bold)
)

val titleFontFamily = FontFamily(
    Font(R.font.roboto_semi_bold)
)

val bodyFontFamily = FontFamily(
    Font(R.font.roboto_medium)
)

val labelFontFamily = FontFamily(
    Font(R.font.roboto_regular)
)

// Default Material 3 typography values
val baseline = Typography()

val StrideTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = headlineFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = headlineFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = headlineFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = titleFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = titleFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = titleFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = labelFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = labelFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = labelFontFamily),
)

