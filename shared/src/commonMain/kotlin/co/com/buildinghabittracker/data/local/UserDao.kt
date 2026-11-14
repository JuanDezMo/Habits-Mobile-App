package co.com.buildinghabittracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO (Data Access Object): las consultas SQL de la tabla `user`.
 * Room genera el código que las ejecuta a partir de estas anotaciones.
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = 1")
    suspend fun get(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)

    @Query("UPDATE user SET failedAttempts = :failedAttempts, lockedUntil = :lockedUntil WHERE id = 1")
    suspend fun updateLockState(failedAttempts: Int, lockedUntil: String?)

    @Query("UPDATE user SET pinHash = :hash, pinSalt = :salt WHERE id = 1")
    suspend fun updatePin(hash: String, salt: String)
}
