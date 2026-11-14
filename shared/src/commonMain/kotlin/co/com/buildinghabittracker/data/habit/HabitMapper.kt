package co.com.buildinghabittracker.data.habit

import co.com.buildinghabittracker.data.local.HabitEntity
import co.com.buildinghabittracker.domain.habit.Habit

/**
 * Mappers entre la tabla (`HabitEntity`, capa data) y el modelo (`Habit`, capa domain).
 *
 * Aquí son casi una copia campo por campo, porque los TypeConverters ya resolvieron las
 * fechas y los días. Igual se mantienen separados: si la tabla cambia (por ejemplo, una
 * columna nueva en una migración), el modelo de domain y las pantallas no se enteran.
 */
fun HabitEntity.toDomain(): Habit = Habit(
    id = id,
    title = title,
    description = description,
    emoji = emoji,
    color = color,
    days = days,
    reminderTime = reminderTime,
    isArchived = isArchived,
    createdAt = createdAt,
)

fun Habit.toEntity(): HabitEntity = HabitEntity(
    id = id,
    title = title,
    description = description,
    emoji = emoji,
    color = color,
    days = days,
    reminderTime = reminderTime,
    isArchived = isArchived,
    createdAt = createdAt,
)
