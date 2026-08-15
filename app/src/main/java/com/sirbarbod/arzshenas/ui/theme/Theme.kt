package com.sirbarbod.arzshenas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

// -----------------------------------------------------------------------
// فونت فارسی وزیرمتن - اختیاری
// برای فعال‌سازی فونت واقعی، طبق راهنمای app/src/main/res/font/README.txt
// عمل کنید و بلوک کامنت زیر را از حالت کامنت خارج کنید.
// -----------------------------------------------------------------------
// TODO: FONT - START
// val AppFontFamily = FontFamily(
//     Font(com.sirbarbod.arzshenas.R.font.vazirmatn_regular, FontWeight.Normal),
//     Font(com.sirbarbod.arzshenas.R.font.vazirmatn_medium, FontWeight.Medium),
//     Font(com.sirbarbod.arzshenas.R.font.vazirmatn_bold, FontWeight.Bold),
// )
// TODO: FONT - END
val AppFontFamily = FontFamily.Default

private val DarkColors = darkColorScheme(
    primary = Gold,
    secondary = GoldSoft,
    tertiary = GoldBright,
    background = BlackColor,
    surface = BlackCardDark,
    onPrimary = BlackColor,
    onSecondary = BlackColor,
    onBackground = WhiteColor,
    onSurface = WhiteColor,
    error = RedDown,
)

private val LightColors = lightColorScheme(
    primary = Gold,
    secondary = GoldSoft,
    tertiary = GoldBright,
    background = Cream,
    surface = WhiteColor,
    onPrimary = BlackColor,
    onSecondary = BlackColor,
    onBackground = BlackColor,
    onSurface = BlackColor,
    error = RedDown,
)

@Composable
fun ArzshenasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val typography = androidx.compose.material3.Typography(
        bodyLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal),
        bodyMedium = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal),
        titleLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Bold),
        titleMedium = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium),
        labelLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium),
    )
    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content,
    )
}
