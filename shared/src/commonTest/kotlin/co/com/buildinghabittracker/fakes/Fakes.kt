package co.com.buildinghabittracker.fakes

import co.com.buildinghabittracker.domain.security.SecretHasher
import co.com.buildinghabittracker.domain.user.HashedSecret
import co.com.buildinghabittracker.domain.user.User
import co.com.buildinghabittracker.domain.user.UserRepository
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Implementaciones falsas para pruebas ("fakes").
 *
 * Reemplazan a las reales sin que el ViewModel lo note, porque cumplen la misma interfaz
 * (la "L" de SOLID: sustitución). Son rápidas y no tocan disco ni cifrado real.
 */

/** Guarda al usuario en una variable, en memoria. */
class FakeUserRepository(var user: User? = null) : UserRepository {
    override suspend fun getUser(): User? = user
    override suspend fun createUser(user: User) { this.user = user }
    override suspend fun updateLockState(failedAttempts: Int, lockedUntil: Instant?) {
        user = user?.copy(failedAttempts = failedAttempts, lockedUntil = lockedUntil)
    }
    override suspend fun updatePin(pin: HashedSecret) { user = user?.copy(pin = pin) }
}

/** "Cifra" agregando un prefijo: instantáneo y fácil de leer en las pruebas. */
class FakeSecretHasher : SecretHasher {
    override suspend fun hash(secret: String) = HashedSecret(hash = "hashed:$secret", salt = "salt")
    override suspend fun verify(secret: String, hashed: HashedSecret) = hashed.hash == "hashed:$secret"
}

/** Reloj que solo avanza cuando la prueba lo pide con [advanceBy]. */
class FakeClock(var current: Instant = Instant.parse("2026-11-15T10:00:00Z")) : Clock {
    override fun now(): Instant = current
    fun advanceBy(duration: Duration) { current += duration }
}

/** Usuario de ejemplo con PIN 1357 y respuesta "firulais". */
fun sampleUser(
    failedAttempts: Int = 0,
    lockedUntil: Instant? = null,
) = User(
    name = "Ana",
    email = "ana@correo.com",
    pin = HashedSecret("hashed:1357", "salt"),
    securityQuestion = "¿Nombre de mi primera mascota?",
    securityAnswer = HashedSecret("hashed:firulais", "salt"),
    failedAttempts = failedAttempts,
    lockedUntil = lockedUntil,
    createdAt = Instant.parse("2026-11-01T08:00:00Z"),
)
