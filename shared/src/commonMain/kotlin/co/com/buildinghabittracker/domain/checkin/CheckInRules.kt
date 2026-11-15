package co.com.buildinghabittracker.domain.checkin

import co.com.buildinghabittracker.domain.habit.Habit
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

/**
 * Reglas de check-in (D1–D3). Kotlin puro: se prueban sin pantalla ni base de datos.
 */
object CheckInRules {

    /** ¿Al hábito le toca ese día? (C3: días de la semana elegidos). */
    fun isScheduled(habit: Habit, date: LocalDate): Boolean =
        date.dayOfWeek in habit.days && date >= habit.createdAt

    /**
     * D1 y D3: solo se puede marcar o desmarcar hoy o ayer, en un día programado,
     * y solo hábitos activos (C5: un archivado no aparece en Hoy).
     */
    fun canToggle(habit: Habit, date: LocalDate, today: LocalDate): Boolean =
        !habit.isArchived &&
            (date == today || date == today.yesterday()) &&
            isScheduled(habit, date)
}

/** El día anterior. */
fun LocalDate.yesterday(): LocalDate = minus(DatePeriod(days = 1))
