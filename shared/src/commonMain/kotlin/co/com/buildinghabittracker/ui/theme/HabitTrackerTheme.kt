package co.com.buildinghabittracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.bricolage_grotesque
import org.jetbrains.compose.resources.Font

/**
 * Tema de la app: sistema de diseño "Mapa de parque" (iteración 3).
 *
 * - Colores: [ParkColors] (claro y oscuro, regla G2). No se usa Dynamic Color: la app se ve
 *   igual en Android e iOS.
 * - Tipografía: una sola familia, Bricolage Grotesque, en un solo peso (negrita). La jerarquía
 *   la da el TAMAÑO, no el peso. Los tamaños van en `sp` para respetar la letra del teléfono.
 * - Formas: cápsulas (radio completo) y círculos.
 *
 * Los componentes de Material 3 (botones, diálogos, snackbars) toman estos valores solos,
 * porque el tema también arma el `ColorScheme` de Material a partir de [ParkColors].
 *
 * @param darkTheme por defecto lo decide el teléfono. En la pantalla Ajustes (G4) se podrá elegir.
 */
@Composable
fun HabitTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val park = if (darkTheme) DarkParkColors else LightParkColors
    CompositionLocalProvider(LocalParkColors provides park) {
        MaterialTheme(
            colorScheme = park.toMaterial(),
            typography = parkTypography(),
            shapes = ParkShapes,
            content = content,
        )
    }
}

/** Acceso corto a los colores propios: `ParkTheme.colors.land`. */
object ParkTheme {
    val colors: ParkColors
        @Composable @ReadOnlyComposable get() = LocalParkColors.current
}

/** Traduce los colores del mapa a los roles de Material 3. */
private fun ParkColors.toMaterial(): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = ink,
        onPrimary = water,
        primaryContainer = notice,
        onPrimaryContainer = onAccent,
        secondary = ink,
        onSecondary = water,
        secondaryContainer = field,
        onSecondaryContainer = ink,
        background = water,
        onBackground = ink,
        surface = water,
        onSurface = ink,
        onSurfaceVariant = inkSoft,
        surfaceContainerLowest = field,
        surfaceContainerLow = field,
        surfaceContainer = field,
        surfaceContainerHigh = water,
        surfaceContainerHighest = field,
        inverseSurface = ink,
        inverseOnSurface = water,
        outline = ink,
        outlineVariant = rule,
        error = error,
        onError = water,
        scrim = ink,
    )
}

@Composable
private fun parkTypography(): Typography {
    // Un solo archivo de fuente variable; se usa siempre en negrita (700).
    val bricolage = FontFamily(Font(Res.font.bricolage_grotesque, FontWeight.Bold))
    fun style(size: Int, line: Int, tracking: Double = 0.0) = TextStyle(
        fontFamily = bricolage,
        fontWeight = FontWeight.Bold,
        fontSize = size.sp,
        lineHeight = line.sp,
        letterSpacing = tracking.em,
    )
    return Typography(
        displayLarge = style(112, 100, -0.05),   // el número grande de Hoy: "2 de 3"
        displayMedium = style(56, 54, -0.04),
        displaySmall = style(44, 42, -0.04),     // título de cada lóbulo
        headlineLarge = style(36, 36, -0.04),
        headlineMedium = style(30, 30, -0.04),   // título de pantallas sin lóbulo
        headlineSmall = style(24, 26, -0.03),
        titleLarge = style(20, 24, -0.02),       // nombre del hábito en su cápsula
        titleMedium = style(17, 22, -0.01),
        titleSmall = style(15, 20),
        bodyLarge = style(17, 24),
        bodyMedium = style(15, 21),
        bodySmall = style(13, 18),
        labelLarge = style(17, 22),              // texto de botones
        labelMedium = style(15, 20),
        labelSmall = style(12, 16, 0.06),
    )
}

/** Cápsulas y círculos: el mapa no tiene esquinas rectas. */
val ParkShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(22.dp),       // campos de texto
    medium = RoundedCornerShape(26.dp),      // bloques de estadísticas
    large = RoundedCornerShape(32.dp),       // diálogos
    extraLarge = RoundedCornerShape(percent = 50),
)
