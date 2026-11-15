package co.com.buildinghabittracker.data.checkin

import co.com.buildinghabittracker.data.local.CheckInDao
import co.com.buildinghabittracker.data.local.CheckInEntity
import co.com.buildinghabittracker.domain.checkin.CheckIn
import co.com.buildinghabittracker.domain.checkin.CheckInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

/** Implementación real de [CheckInRepository] con Room. Solo traduce; no tiene reglas. */
class CheckInRepositoryImpl(
    private val dao: CheckInDao,
) : CheckInRepository {

    override fun observeCheckIns(): Flow<List<CheckIn>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun add(checkIn: CheckIn) = dao.insert(checkIn.toEntity())

    override suspend fun remove(habitId: Long, date: LocalDate) = dao.delete(habitId, date)
}

internal fun CheckInEntity.toDomain() = CheckIn(
    habitId = habitId,
    date = date,
    checkedAt = Instant.parse(checkedAt),
)

internal fun CheckIn.toEntity() = CheckInEntity(
    habitId = habitId,
    date = date,
    checkedAt = checkedAt.toString(),
)
