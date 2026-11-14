package co.com.buildinghabittracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Tabla `habit` de Room.
 *
 * Los campos usan tipos de Kotlin (`Set<DayOfWeek>`, `LocalDate`, `LocalTime`), pero SQLite
 * solo guarda texto y números. [HabitConverters] los traduce a texto legible:
 * - days        → "MONDAY,WEDNESDAY,FRIDAY"
 * - reminderTime → "07:00" o null
 * - createdAt   → "2026-11-15"
 */
@Entity(tableName = "habit")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val emoji: String,
    val color: String,
    val days: Set<DayOfWeek>,
    val reminderTime: LocalTime?,
    val isArchived: Boolean,
    val createdAt: LocalDate,
)
