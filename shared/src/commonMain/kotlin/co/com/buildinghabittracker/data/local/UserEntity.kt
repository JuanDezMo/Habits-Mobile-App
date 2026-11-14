package co.com.buildinghabittracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla `user` de Room. Solo tiene una fila (regla A1), por eso el id siempre es 1.
 *
 * Diferencia con el modelo `User` de domain: aquí todo es texto o número, porque es lo que
 * guarda SQLite. Las fechas van en texto ISO, por ejemplo "2026-11-15T10:04:30Z".
 */
@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Int = SINGLE_USER_ID,
    val name: String,
    val email: String,
    val pinHash: String,
    val pinSalt: String,
    val securityQuestion: String,
    val answerHash: String,
    val answerSalt: String,
    val failedAttempts: Int,
    val lockedUntil: String?,
    val createdAt: String,
) {
    companion object {
        const val SINGLE_USER_ID = 1
    }
}
