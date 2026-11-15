package co.com.buildinghabittracker.domain.checkin

import co.com.buildinghabittracker.domain.habit.Habit
import kotlinx.datetime.LocalDate

/**
 * Calcula la racha de un hábito. Kotlin puro: recibe datos y devuelve un número.
 *
 * Primera versión (iteración 4): E1 y E2. En la iteración 5 se agregan la mejor racha (E3)
 * y los días archivados (E6).
 */
object StreakCalculator {

    /**
     * Racha actual (E1): días programados seguidos con check-in, contando hacia atrás desde hoy.
     * - Los días que no tocan se saltan: no suman ni rompen la racha.
     * - E2: si hoy toca y aún no se marca, no rompe la racha; se cuenta desde ayer.
     * - Se usan los días que el hábito tiene AHORA (si se cambian, se recalcula con los nuevos).
     *
     * Ejemplo: Gym lunes, miércoles y viernes; hoy es viernes sin marcar.
     * Marcado el miércoles y el lunes → racha 2.
     *
     * @param checkedDates los días en que ESTE hábito tiene check-in.
     */
    fun currentStreak(habit: Habit, checkedDates: Set<LocalDate>, today: LocalDate): Int {
        var streak = 0
        var day = today
        // E2: hoy pendiente no cuenta ni rompe.
        if (CheckInRules.isScheduled(habit, today) && today !in checkedDates) day = today.yesterday()
        // Antes de crear el hábito no hay nada que contar: el ciclo termina ahí.
        while (day >= habit.createdAt) {
            if (day.dayOfWeek in habit.days) {
                if (day in checkedDates) streak++ else break
            }
            day = day.yesterday()
        }
        return streak
    }
}
