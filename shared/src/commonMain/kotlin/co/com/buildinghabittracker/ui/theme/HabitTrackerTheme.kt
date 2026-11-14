package co.com.buildinghabittracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Tema de la app (regla G2: modo claro y oscuro).
 *
 * Iteración 0: usa los colores por defecto de Material 3 y sigue el modo del teléfono.
 * Iteración 3: aquí se conecta la paleta propia del sistema de diseño y la opción de
 * Ajustes (Igual que el teléfono / Claro / Oscuro, regla G4).
 *
 * @param darkTheme si es `true` se usan los colores oscuros. Por defecto, lo decide el teléfono.
 */
@Composable
fun HabitTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
        content = content,
    )
}
