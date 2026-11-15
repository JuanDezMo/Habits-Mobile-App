package co.com.buildinghabittracker.domain.checkin

import co.com.buildinghabittracker.fakes.sampleHabit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pruebas de la racha actual (E1, E2).
 * Calendario: lunes 9, miércoles 11, viernes 13 y lunes 16 de noviembre de 2026.
 * `sampleHabit()` es "Gym": lunes, miércoles y viernes, creado el domingo 1.
 */
class StreakCalculatorTest {
    private val gym = sampleHabit()
    private val daily = sampleHabit(title = "Leer", days = DayOfWeek.entries.toSet())

    private fun d(day: Int) = LocalDate(2026, 11, day)

    @Test
    fun sin_check_ins_la_racha_es_cero() {
        assertEquals(0, StreakCalculator.currentStreak(gym, emptySet(), today = d(13)))
    }

    @Test
    fun e1_cuenta_dias_programados_seguidos_con_check_in() {
        val checked = setOf(d(9), d(11), d(13))
        assertEquals(3, StreakCalculator.currentStreak(gym, checked, today = d(13)))
    }

    @Test
    fun e1_los_dias_no_programados_no_rompen_la_racha() {
        // Martes 10 y jueves 12 no tocan: lunes 9 y miércoles 11 siguen seguidos.
        val checked = setOf(d(9), d(11))
        assertEquals(2, StreakCalculator.currentStreak(gym, checked, today = d(12)))
    }

    @Test
    fun e1_un_dia_programado_sin_check_in_corta_la_racha() {
        // Falta el miércoles 11: solo cuenta el viernes 13.
        val checked = setOf(d(9), d(13))
        assertEquals(1, StreakCalculator.currentStreak(gym, checked, today = d(13)))
    }

    @Test
    fun e2_hoy_sin_marcar_mantiene_la_racha_de_ayer() {
        val checked = setOf(d(10), d(11), d(12))
        assertEquals(3, StreakCalculator.currentStreak(daily, checked, today = d(13)))
    }

    @Test
    fun e2_hoy_marcado_suma_a_la_racha() {
        val checked = setOf(d(10), d(11), d(12), d(13))
        assertEquals(4, StreakCalculator.currentStreak(daily, checked, today = d(13)))
    }

    @Test
    fun ayer_sin_marcar_rompe_la_racha() {
        val checked = setOf(d(10), d(11))
        assertEquals(0, StreakCalculator.currentStreak(daily, checked, today = d(13)))
    }

    @Test
    fun no_cuenta_antes_de_crear_el_habito() {
        val nuevo = sampleHabit(days = DayOfWeek.entries.toSet(), createdAt = d(12))
        assertEquals(2, StreakCalculator.currentStreak(nuevo, setOf(d(12), d(13)), today = d(13)))
    }
}
