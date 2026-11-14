package co.com.buildinghabittracker.domain.habit

import kotlinx.coroutines.flow.Flow

/**
 * Contrato para guardar y leer hábitos. Dice QUÉ se necesita, no CÓMO.
 *
 * - En la app: lo implementa `HabitRepositoryImpl` con Room (capa data).
 * - En las pruebas: lo implementa `FakeHabitRepository`, que guarda todo en memoria.
 *
 * C5: no hay función para eliminar. Un hábito solo se archiva o se desarchiva.
 */
interface HabitRepository {
    /**
     * Todos los hábitos, activos y archivados, ordenados por fecha de creación.
     * Es un `Flow`: cada vez que algo cambia en la tabla, emite la lista nueva y la pantalla
     * se actualiza sola.
     */
    fun observeHabits(): Flow<List<Habit>>

    /** Un hábito por su id, o `null` si no existe. */
    suspend fun getHabit(id: Long): Habit?

    /** Guarda un hábito nuevo y devuelve el id que le asignó la base de datos. */
    suspend fun createHabit(habit: Habit): Long

    /** Reemplaza un hábito existente (mismo id) con los datos nuevos. */
    suspend fun updateHabit(habit: Habit)

    /** C5: archiva (`true`) o desarchiva (`false`) un hábito. */
    suspend fun setArchived(id: Long, archived: Boolean)
}
