package co.com.buildinghabittracker.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Colores del sistema de diseño "Mapa de parque" (iteración 3).
 *
 * Todo es color plano: sin degradados ni sombras. Los nombres vienen del mapa:
 * - water: el fondo (el "agua" del mapa).
 * - ink: el texto y los controles principales.
 * - land: el verde del lóbulo de Hoy.
 * - field: el fondo de los campos y teclas (blanco en claro).
 * - onAccent: el texto SOBRE cualquier color de hábito o lóbulo. Es oscuro en los dos modos,
 *   porque los colores de hábito son claros y vivos (contraste AA verificado).
 *
 * Se leen en cualquier pantalla con `ParkTheme.colors.water`, etc.
 */
@Immutable
data class ParkColors(
    val water: Color,
    val ink: Color,
    val inkSoft: Color,
    val land: Color,
    val notice: Color,
    val field: Color,
    val rule: Color,
    val onAccent: Color,
    val error: Color,
    val missed: Color,
    val isDark: Boolean,
)

val LightParkColors = ParkColors(
    water = Color(0xFFF5F1E4),
    ink = Color(0xFF2C2E2A),
    inkSoft = Color(0xFF5A5C55),
    land = Color(0xFF8ED462),
    notice = Color(0xFFF5E211),
    field = Color(0xFFFFFFFF),
    rule = Color(0xFFE3DECD),
    onAccent = Color(0xFF2C2E2A),
    error = Color(0xFFB23A2A),
    missed = Color(0xFFB5AE9C),
    isDark = false,
)

val DarkParkColors = ParkColors(
    water = Color(0xFF1E211C),
    ink = Color(0xFFF2EEDF),
    inkSoft = Color(0xFFC4C0B2),
    land = Color(0xFF6FB847),
    notice = Color(0xFFF5E211),
    field = Color(0xFF2B2F28),
    rule = Color(0xFF3A3E35),
    onAccent = Color(0xFF1E211C),
    error = Color(0xFFFF8E7E),
    missed = Color(0xFF6E6A5E),
    isDark = true,
)

/**
 * Colores de los hábitos (regla C2): las tintas planas de las cápsulas del mapa.
 * El primero es el valor por defecto. Siempre llevan texto [ParkColors.onAccent] encima.
 */
object HabitPalette {
    val coral = Color(0xFFFF705D)
    val blue = Color(0xFF2BA0FF)
    val lilac = Color(0xFFEBC1FF)
    val yellow = Color(0xFFF5E211)
    val green = Color(0xFF8ED462)
    val orange = Color(0xFFFFA24C)
    val teal = Color(0xFF3FD0B6)
    val pink = Color(0xFFFF9FC8)
}

val LocalParkColors = staticCompositionLocalOf { LightParkColors }
