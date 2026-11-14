package co.com.buildinghabittracker.data.local

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Los TypeConverters guardan texto legible y lo leen de vuelta sin perder nada. */
class HabitConvertersTest {

    private val converters = HabitConverters()

    @Test
    fun dias_se_guardan_por_nombre_de_lunes_a_domingo() {
        val days = setOf(DayOfWeek.FRIDAY, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        val text = converters.daysToText(days)
        assertEquals("MONDAY,WEDNESDAY,FRIDAY", text)
        assertEquals(days, converters.textToDays(text))
    }

    @Test
    fun fecha_en_formato_iso() {
        val date = LocalDate(2026, 11, 15)
        assertEquals("2026-11-15", converters.dateToText(date))
        assertEquals(date, converters.textToDate("2026-11-15"))
    }

    @Test
    fun hora_opcional() {
        assertEquals("07:00", converters.timeToText(LocalTime(7, 0)))
        assertEquals(LocalTime(7, 0), converters.textToTime("07:00"))
        assertNull(converters.timeToText(null))
        assertNull(converters.textToTime(null))
    }
}
