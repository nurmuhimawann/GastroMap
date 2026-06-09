package com.nurmuhimawann.gastromap.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonLime,
    secondary = NeonLime,
    background = DeepBlack,
    surface = SurfaceDark,
    onPrimary = DeepBlack,
    onSecondary = DeepBlack,
    onBackground = PureWhite,
    onSurface = PureWhite,
    surfaceVariant = DarkGrayText,
    onSurfaceVariant = GrayText
)

private val LightColorScheme = lightColorScheme(
    primary = NeonLimeMuted,
    secondary = NeonLimeMuted,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onBackground = DeepBlack,
    onSurface = DeepBlack,
    surfaceVariant = GrayText,
    onSurfaceVariant = DarkGrayText
)

@Composable
fun GastroMapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
