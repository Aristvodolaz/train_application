package com.application.apps_for_individual_train.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Черно-серо-зеленая цветовая схема для светлой темы
private val LightColorScheme = lightColorScheme(
    primary = Green700,
    onPrimary = Color.White,
    primaryContainer = Green200,
    onPrimaryContainer = Green900,
    
    secondary = GreenA400,
    onSecondary = Color.Black,
    secondaryContainer = GreenA200.copy(alpha = 0.3f),
    onSecondaryContainer = Green900,
    
    tertiary = GreenA700,
    onTertiary = Color.Black,
    tertiaryContainer = GreenA200.copy(alpha = 0.5f),
    onTertiaryContainer = Green900,
    
    background = Gray100,
    onBackground = Gray900,
    
    surface = Color.White,
    onSurface = Gray900,
    
    surfaceVariant = Gray200,
    onSurfaceVariant = Gray700,
    
    error = ErrorRed,
    onError = Color.White,
    
    outline = Gray600
)

// Черно-серо-зеленая цветовая схема для темной темы
private val DarkColorScheme = darkColorScheme(
    primary = GreenA400,
    onPrimary = DarkBlack,
    primaryContainer = Green800,
    onPrimaryContainer = GreenA200,
    
    secondary = GreenA700,
    onSecondary = DarkBlack,
    secondaryContainer = Green900,
    onSecondaryContainer = GreenA200,
    
    tertiary = BrightGreen,
    onTertiary = DarkBlack,
    tertiaryContainer = Green700,
    onTertiaryContainer = GreenA200,
    
    background = DarkBlack,
    onBackground = TextWhite,
    
    surface = DarkGray,
    onSurface = TextWhite,
    
    surfaceVariant = MediumGray,
    onSurfaceVariant = TextGray,
    
    error = ErrorRed,
    onError = DarkBlack,
    
    outline = Gray600
)

@Composable
fun Apps_for_individual_trainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Отключаем динамические цвета по умолчанию для сохранения нашей цветовой схемы
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    // Устанавливаем цвет статус-бара и навигационной панели
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) DarkBlack.toArgb() else colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
