package co.com.buildinghabittracker.domain.user

import kotlin.time.Instant

/**
 * Un secreto guardado de forma segura: nunca el PIN o la respuesta, solo su hash y la sal.
 * Ambos van en Base64 para poder guardarlos como texto.
 */
data class HashedSecret(
    val hash: String,
    val salt: String,
)

/**
 * El usuario de la app (regla A1: hay uno solo por teléfono).
 *
 * Es un modelo de `domain`: Kotlin puro, sin anotaciones de Room. La capa `data` lo convierte
 * desde y hacia `UserEntity` (la tabla `user`).
 *
 * @property failedAttempts intentos fallidos seguidos de PIN o respuesta (regla A6).
 * @property lockedUntil hasta cuándo está bloqueado; `null` si no hay bloqueo.
 */
data class User(
    val name: String,
    val email: String,
    val pin: HashedSecret,
    val securityQuestion: String,
    val securityAnswer: HashedSecret,
    val failedAttempts: Int,
    val lockedUntil: Instant?,
    val createdAt: Instant,
)
