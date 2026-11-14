package co.com.buildinghabittracker.data.user

import co.com.buildinghabittracker.data.local.UserEntity
import co.com.buildinghabittracker.domain.user.HashedSecret
import co.com.buildinghabittracker.domain.user.User
import kotlin.time.Instant

/**
 * Mappers: convierten entre la tabla (`UserEntity`, capa data) y el modelo (`User`, capa domain).
 * Así el resto de la app nunca ve detalles de Room, como las fechas guardadas en texto.
 */
fun UserEntity.toDomain(): User = User(
    name = name,
    email = email,
    pin = HashedSecret(hash = pinHash, salt = pinSalt),
    securityQuestion = securityQuestion,
    securityAnswer = HashedSecret(hash = answerHash, salt = answerSalt),
    failedAttempts = failedAttempts,
    lockedUntil = lockedUntil?.let { Instant.parse(it) },
    createdAt = Instant.parse(createdAt),
)

fun User.toEntity(): UserEntity = UserEntity(
    name = name,
    email = email,
    pinHash = pin.hash,
    pinSalt = pin.salt,
    securityQuestion = securityQuestion,
    answerHash = securityAnswer.hash,
    answerSalt = securityAnswer.salt,
    failedAttempts = failedAttempts,
    lockedUntil = lockedUntil?.toString(),
    createdAt = createdAt.toString(),
)
