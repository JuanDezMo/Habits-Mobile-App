package co.com.buildinghabittracker.domain.checkin

import co.com.buildinghabittracker.fakes.sampleHabit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Pruebas de D1 y D3: solo hoy o ayer, en días programados, hábitos activos. */
class CheckInRulesTest {
    private val daily = sampleHabit(days = DayOfWeek.entries.toSet())
    private val today = LocalDate(2026, 11, 13) // viernes

    @Test
    fun d1_permite_hoy_y_ayer() {
        assertTrue(CheckInRules.canToggle(daily, today, today))
        assertTrue(CheckInRules.canToggle(daily, LocalDate(2026, 11, 12), today))
    }

    @Test
    fun d1_no_permite_anteayer_ni_mañana() {
        assertFalse(CheckInRules.canToggle(daily, LocalDate(2026, 11, 11), today))
        assertFalse(CheckInRules.canToggle(daily, LocalDate(2026, 11, 14), today))
    }

    @Test
    fun d1_no_permite_dias_no_programados() {
        // Gym es lunes, miércoles y viernes: el jueves 12 (ayer) no toca.
        assertFalse(CheckInRules.canToggle(sampleHabit(), LocalDate(2026, 11, 12), today))
    }

    @Test
    fun no_permite_antes_de_crear_el_habito() {
        val creadoHoy = sampleHabit(days = DayOfWeek.entries.toSet(), createdAt = today)
        assertFalse(CheckInRules.canToggle(creadoHoy, LocalDate(2026, 11, 12), today))
    }

    @Test
    fun c5_no_permite_habitos_archivados() {
        assertFalse(CheckInRules.canToggle(daily.copy(isArchived = true), today, today))
    }
}
