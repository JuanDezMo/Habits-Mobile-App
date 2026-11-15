package co.com.buildinghabittracker.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

/**
 * Tabla `check_in` de Room: una fila por hábito marcado en un día.
 *
 * - Índice único (habitId, date): la base de datos misma impide dos check-ins del mismo
 *   hábito el mismo día (regla D2), aunque el código se equivoque.
 * - Llave foránea a `habit`: no puede existir un check-in de un hábito que no existe.
 * - `date` va como texto ISO ("2026-11-14") con [HabitConverters]; `checkedAt` como texto
 *   ISO con hora ("2026-11-14T21:30:00Z"), igual que las fechas de `user`.
 */
@Entity(
    tableName = "check_in",
    indices = [Index(value = ["habitId", "date"], unique = true)],
    foreignKeys = [
        ForeignKey(entity = HabitEntity::class, parentColumns = ["id"], childColumns = ["habitId"]),
    ],
)
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val date: LocalDate,
    val checkedAt: String,
)
