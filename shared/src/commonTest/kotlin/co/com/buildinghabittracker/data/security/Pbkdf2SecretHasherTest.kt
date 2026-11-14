package co.com.buildinghabittracker.data.security

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Pruebas del cifrado REAL (regla A3). A diferencia de las demás, aquí no hay fakes:
 * se verifica que PBKDF2 funciona de verdad en cada plataforma.
 */
class Pbkdf2SecretHasherTest {

    private val hasher = Pbkdf2SecretHasher()

    @Test
    fun a3_el_pin_correcto_se_verifica_y_el_incorrecto_no() = runTest {
        val hashed = hasher.hash("1357")
        assertTrue(hasher.verify("1357", hashed))
        assertFalse(hasher.verify("1358", hashed))
    }

    @Test
    fun a3_el_hash_no_contiene_el_pin_y_cambia_con_cada_sal() = runTest {
        val first = hasher.hash("1357")
        val second = hasher.hash("1357")
        assertFalse(first.hash.contains("1357"))
        assertNotEquals(first.salt, second.salt)
        assertNotEquals(first.hash, second.hash)
    }
}
