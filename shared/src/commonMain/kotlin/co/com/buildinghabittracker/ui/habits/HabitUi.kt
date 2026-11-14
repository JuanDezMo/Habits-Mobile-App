package co.com.buildinghabittracker.ui.habits

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.day_friday
import co.com.buildinghabittracker.resources.day_monday
import co.com.buildinghabittracker.resources.day_saturday
import co.com.buildinghabittracker.resources.day_short_friday
import co.com.buildinghabittracker.resources.day_short_monday
import co.com.buildinghabittracker.resources.day_short_saturday
import co.com.buildinghabittracker.resources.day_short_sunday
import co.com.buildinghabittracker.resources.day_short_thursday
import co.com.buildinghabittracker.resources.day_short_tuesday
import co.com.buildinghabittracker.resources.day_short_wednesday
import co.com.buildinghabittracker.resources.day_sunday
import co.com.buildinghabittracker.resources.day_thursday
import co.com.buildinghabittracker.resources.day_tuesday
import co.com.buildinghabittracker.resources.day_wednesday
import co.com.buildinghabittracker.resources.habits_every_day
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/*
 * Piezas visuales que comparten la lista y el formulario de hábitos.
 * Los colores de hábito vienen de HabitValidator.COLORS (paleta del sistema "Mapa de parque").
 */

/** "#2E8B57" → Color. Los colores vienen de la lista fija de HabitValidator.COLORS. */
fun colorFromHex(hex: String): Color = Color(0xFF000000 or hex.removePrefix("#").toLong(16))

private val shortNames = mapOf(
    DayOfWeek.MONDAY to Res.string.day_short_monday,
    DayOfWeek.TUESDAY to Res.string.day_short_tuesday,
    DayOfWeek.WEDNESDAY to Res.string.day_short_wednesday,
    DayOfWeek.THURSDAY to Res.string.day_short_thursday,
    DayOfWeek.FRIDAY to Res.string.day_short_friday,
    DayOfWeek.SATURDAY to Res.string.day_short_saturday,
    DayOfWeek.SUNDAY to Res.string.day_short_sunday,
)

private val fullNames: Map<DayOfWeek, StringResource> = mapOf(
    DayOfWeek.MONDAY to Res.string.day_monday,
    DayOfWeek.TUESDAY to Res.string.day_tuesday,
    DayOfWeek.WEDNESDAY to Res.string.day_wednesday,
    DayOfWeek.THURSDAY to Res.string.day_thursday,
    DayOfWeek.FRIDAY to Res.string.day_friday,
    DayOfWeek.SATURDAY to Res.string.day_saturday,
    DayOfWeek.SUNDAY to Res.string.day_sunday,
)

/** Letra del día en el idioma elegido: L, M, X… */
@Composable
fun dayShortName(day: DayOfWeek): String = stringResource(shortNames.getValue(day))

/** Nombre completo del día, para lectores de pantalla: "lunes". */
@Composable
fun dayFullName(day: DayOfWeek): String = stringResource(fullNames.getValue(day))

/** "Todos los días" o "L · X · V", siempre de lunes a domingo. */
@Composable
fun daysLabel(days: Set<DayOfWeek>): String =
    if (days.size == DayOfWeek.entries.size) {
        stringResource(Res.string.habits_every_day)
    } else {
        days.sortedBy { it.ordinal }.map { dayShortName(it) }.joinToString(" · ")
    }
