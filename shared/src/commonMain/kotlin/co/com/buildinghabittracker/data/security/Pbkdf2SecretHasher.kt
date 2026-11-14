package co.com.buildinghabittracker.data.security

import co.com.buildinghabittracker.domain.security.SecretHasher
import co.com.buildinghabittracker.domain.user.HashedSecret
import dev.whyoleg.cryptography.BinarySize.Companion.bytes
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.PBKDF2
import dev.whyoleg.cryptography.algorithms.SHA256
import dev.whyoleg.cryptography.random.CryptographyRandom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.encoding.Base64

/**
 * Regla A3: el PIN (y la respuesta de seguridad) se guardan con PBKDF2-SHA256.
 *
 * - Sal aleatoria de 16 bytes: dos usuarios con el mismo PIN tienen hashes distintos.
 * - 210.000 iteraciones: calcular un hash tarda ~0,3 s. Para el usuario no se nota, pero
 *   probar los 10.000 PIN posibles le costaría a un atacante casi una hora por teléfono.
 *
 * El cálculo corre en `Dispatchers.Default` para no congelar la pantalla.
 */
class Pbkdf2SecretHasher(
    private val iterations: Int = ITERATIONS,
) : SecretHasher {

    private val pbkdf2 = CryptographyProvider.Default.get(PBKDF2)

    override suspend fun hash(secret: String): HashedSecret {
        val salt = CryptographyRandom.nextBytes(SALT_BYTES)
        val hash = derive(secret, salt)
        return HashedSecret(hash = Base64.encode(hash), salt = Base64.encode(salt))
    }

    override suspend fun verify(secret: String, hashed: HashedSecret): Boolean {
        val salt = Base64.decode(hashed.salt)
        val expected = Base64.decode(hashed.hash)
        return constantTimeEquals(derive(secret, salt), expected)
    }

    private suspend fun derive(secret: String, salt: ByteArray): ByteArray =
        withContext(Dispatchers.Default) {
            pbkdf2
                .secretDerivation(SHA256, iterations = iterations, outputSize = HASH_BYTES.bytes, salt = salt)
                .deriveSecretToByteArray(secret.encodeToByteArray())
        }

    /** Compara sin cortar en la primera diferencia, para no revelar información por el tiempo. */
    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var diff = 0
        for (i in a.indices) diff = diff or (a[i].toInt() xor b[i].toInt())
        return diff == 0
    }

    companion object {
        const val ITERATIONS = 210_000
        const val SALT_BYTES = 16
        const val HASH_BYTES = 32
    }
}
