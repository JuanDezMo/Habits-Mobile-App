package co.com.buildinghabittracker.data.habit

import co.com.buildinghabittracker.data.local.HabitDao
import co.com.buildinghabittracker.domain.habit.Habit
import co.com.buildinghabittracker.domain.habit.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación real de [HabitRepository] usando Room.
 * Solo traduce: llama al DAO y convierte con los mappers. No tiene reglas de negocio.
 */
class HabitRepositoryImpl(
    private val dao: HabitDao,
) : HabitRepository {

    // `map` convierte cada lista que emite Room (entities) en una lista de modelos de domain.
    override fun observeHabits(): Flow<List<Habit>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getHabit(id: Long): Habit? = dao.get(id)?.toDomain()

    override suspend fun createHabit(habit: Habit): Long = dao.insert(habit.toEntity())

    override suspend fun updateHabit(habit: Habit) = dao.update(habit.toEntity())

    override suspend fun setArchived(id: Long, archived: Boolean) = dao.setArchived(id, archived)
}
