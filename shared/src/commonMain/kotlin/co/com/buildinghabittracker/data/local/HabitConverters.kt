package co.com.buildinghabittracker.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * TypeConverters: le enseñan a Room a guardar tipos que SQLite no conoce.
 * Cada par convierte en las dos direcciones: Kotlin → texto (al guardar) y texto → Kotlin (al leer).
 *
 * Se registran en `AppDatabase` con `@TypeConverters(HabitConverters::class)`.
 */
class HabitConverters {

    /** {MONDAY, FRIDAY} → "MONDAY,FRIDAY". Se ordenan de lunes a domingo para que siempre se lea igual. */
    @TypeConverter
    fun daysToText(days: Set<DayOfWeek>): String =
        days.sortedBy { it.ordinal }.joinToString(",") { it.name }

    @TypeConverter
    fun textToDays(text: String): Set<DayOfWeek> =
        if (text.isEmpty()) emptySet() else text.split(",").map { DayOfWeek.valueOf(it) }.toSet()

    /** 2026-11-15 → "2026-11-15" (formato ISO). */
    @TypeConverter
    fun dateToText(date: LocalDate): String = date.toString()

    @TypeConverter
    fun textToDate(text: String): LocalDate = LocalDate.parse(text)

    /** 07:00 → "07:00". Un `null` se guarda como `null`. */
    @TypeConverter
    fun timeToText(time: LocalTime?): String? = time?.toString()

    @TypeConverter
    fun textToTime(text: String?): LocalTime? = text?.let { LocalTime.parse(it) }
}
