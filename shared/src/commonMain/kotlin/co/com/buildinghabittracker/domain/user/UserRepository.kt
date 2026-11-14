package co.com.buildinghabittracker.domain.user

import kotlin.time.Instant

/**
 * Contrato para guardar y leer al usuario. Dice QUÉ se necesita, no CÓMO.
 *
 * - En la app: lo implementa `UserRepositoryImpl` con Room (capa data).
 * - En las pruebas: lo implementa `FakeUserRepository`, que guarda todo en memoria.
 *
 * Así los ViewModels dependen de esta interfaz y no de Room (la "D" de SOLID).
 * Las funciones son `suspend` porque leer o escribir en disco no debe bloquear la pantalla.
 */
interface UserRepository {
    /** El usuario registrado, o `null` si todavía no hay registro. */
    suspend fun getUser(): User?

    /** Guarda al usuario al terminar el registro (regla A1). */
    suspend fun createUser(user: User)

    /** Guarda los intentos fallidos y el bloqueo (regla A6). */
    suspend fun updateLockState(failedAttempts: Int, lockedUntil: Instant?)

    /** Reemplaza el PIN (reglas A7 y A8). */
    suspend fun updatePin(pin: HashedSecret)
}
