package co.com.buildinghabittracker.data.user

import co.com.buildinghabittracker.data.local.UserDao
import co.com.buildinghabittracker.domain.user.HashedSecret
import co.com.buildinghabittracker.domain.user.User
import co.com.buildinghabittracker.domain.user.UserRepository
import kotlin.time.Instant

/**
 * Implementación real de [UserRepository] usando Room.
 * Solo traduce: llama al DAO y convierte con los mappers. No tiene reglas de negocio.
 */
class UserRepositoryImpl(
    private val dao: UserDao,
) : UserRepository {

    override suspend fun getUser(): User? = dao.get()?.toDomain()

    override suspend fun createUser(user: User) = dao.insert(user.toEntity())

    override suspend fun updateLockState(failedAttempts: Int, lockedUntil: Instant?) =
        dao.updateLockState(failedAttempts, lockedUntil?.toString())

    override suspend fun updatePin(pin: HashedSecret) =
        dao.updatePin(hash = pin.hash, salt = pin.salt)
}
