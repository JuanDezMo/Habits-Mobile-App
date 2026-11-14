package co.com.buildinghabittracker.domain.security

import co.com.buildinghabittracker.domain.user.HashedSecret

/**
 * Convierte un secreto (PIN o respuesta de seguridad) en un hash que no se puede revertir.
 *
 * - En la app: `Pbkdf2SecretHasher` (capa data), lento a propósito para frenar ataques.
 * - En las pruebas: `FakeSecretHasher`, instantáneo.
 */
interface SecretHasher {
    /** Genera una sal nueva y devuelve el hash del secreto. */
    suspend fun hash(secret: String): HashedSecret

    /** `true` si el secreto produce el mismo hash guardado. */
    suspend fun verify(secret: String, hashed: HashedSecret): Boolean
}
