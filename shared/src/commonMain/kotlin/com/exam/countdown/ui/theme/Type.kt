package com.exam.countdown.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    displayLarge = TextStyle(fontSize = 57.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontSize = 45.sp, fontWeight = FontWeight.Bold),
    headlineLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.4).sp), // iOS standard
    titleSmall = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, letterSpacing = (-0.2).sp),
    bodyLarge = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.4).sp), // iOS standard body
    bodyMedium = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.2).sp),
    bodySmall = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, letterSpacing = 0.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
)
