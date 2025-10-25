package com.example.appmovil.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ChocolateMedium,
    secondary = ChocolateLight,
    tertiary = CreamDark,
    background = ChocolateDark,
    surface = ChocolateDark,
    onPrimary = White,
    onSecondary = White,
    onTertiary = ChocolateDark,
    onBackground = Cream,
    onSurface = Cream
)

private val LightColorScheme = lightColorScheme(
    primary = ChocolateMedium,
    secondary = ChocolateLight,
    tertiary = CreamDark,
    background = Cream,
    surface = Cream,
    onPrimary = White,
    onSecondary = White,
    onTertiary = ChocolateDark,
    onBackground = ChocolateDark,
    onSurface = ChocolateDark
)

@Composable
fun AppMovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Deshabilitado para mantener colores personalizados
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}