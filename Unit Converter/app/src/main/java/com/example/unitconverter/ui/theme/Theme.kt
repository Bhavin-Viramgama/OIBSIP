package com.example.unitconverter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily

private val TypographyModern = Typography(
    displayLarge  = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 44.sp, lineHeight = 48.sp),
    displayMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 36.sp),
    headlineMedium= TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 24.sp),
    bodyLarge     = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp),
    labelLarge    = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val scheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(
        colorScheme = scheme,
        typography = TypographyModern,
        content = content
    )
}
