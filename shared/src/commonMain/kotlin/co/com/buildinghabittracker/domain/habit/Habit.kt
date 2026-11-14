package co.com.buildinghabittracker.domain.habit

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Un hábito del usuario (reglas C1–C5).
 *
 * Es un modelo de `domain`: Kotlin puro, sin anotaciones de Room. La capa `data` lo convierte
 * desde y hacia `HabitEntity` (la tabla `habit`).
 *
 * @property id lo asigna la base de datos; un hábito nuevo, todavía sin guardar, usa [NEW_ID].
 * @property days días de la semana en que toca (C3: al menos uno).
 * @property reminderTime hora del recordatorio; queda en `null` hasta la iteración 6 (R1).
 * @property isArchived C5: archivado = oculto de Hoy, sin recordatorio, con historial.
 * @property createdAt día en que se creó; desde ahí se calcula el cumplimiento (E4).
 */
data class Habit(
    val id: Long = NEW_ID,
    val title: String,
    val description: String?,
    val emoji: String,
    val color: String,
    val days: Set<DayOfWeek>,
    val reminderTime: LocalTime? = null,
    val isArchived: Boolean = false,
    val createdAt: LocalDate,
) {
    companion object {
        const val NEW_ID = 0L
    }
}
