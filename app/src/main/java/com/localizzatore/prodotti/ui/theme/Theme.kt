package com.localizzatore.prodotti.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val SchemaScuro = darkColorScheme(
    primary = Blu80,
    secondary = BluGrigio80,
    tertiary = Verde80,
    background = SfondoScuro
)

private val SchemaChiaro = lightColorScheme(
    primary = Blu40,
    secondary = BluGrigio40,
    tertiary = Verde40,
    background = SfondoChiaro
)

/** Tema Material 3 con supporto a dark mode automatica e dynamic color (Android 12+). */
@Composable
fun LocalizzatoreProdottiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> SchemaScuro
        else -> SchemaChiaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
