package com.tuapp.gastos.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AppPrimary,
    secondary = AppSecondary,
    background = AppBackground,
    surface = AppCard,
    error = AppError,
    onPrimary = AppCard,
    onSecondary = AppCard,
    onBackground = AppTextPrimary,
    onSurface = AppTextPrimary,
    onError = AppCard
)

@Composable
fun ControlGastosAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}