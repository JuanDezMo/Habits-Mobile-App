package co.com.buildinghabittracker.domain.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/** Pruebas de las reglas A3, A5 y A6. El nombre de cada prueba dice qué regla verifica. */
class PinRulesTest {

    private val now = Instant.parse("2026-11-15T10:00:00Z")

    @Test
    fun a3_pin_valido_tiene_exactamente_4_digitos() {
        assertTrue(PinRules.isValidPin("1357"))
        assertFalse(PinRules.isValidPin("135"))
        assertFalse(PinRules.isValidPin("13579"))
        assertFalse(PinRules.isValidPin("12a4"))
    }

    @Test
    fun a6_menos_de_5_fallos_no_bloquea() {
        (0..4).forEach { assertEquals(Duration.ZERO, PinRules.lockDuration(it)) }
    }

    @Test
    fun a6_el_bloqueo_empieza_en_30_segundos_y_se_duplica() {
        assertEquals(30.seconds, PinRules.lockDuration(5))
        assertEquals(1.minutes, PinRules.lockDuration(6))
        assertEquals(2.minutes, PinRules.lockDuration(7))
        assertEquals(4.minutes, PinRules.lockDuration(8))
        assertEquals(8.minutes, PinRules.lockDuration(9))
    }

    @Test
    fun a6_el_bloqueo_tiene_tope_de_15_minutos() {
        assertEquals(15.minutes, PinRules.lockDuration(10))
        assertEquals(15.minutes, PinRules.lockDuration(50))
    }

    @Test
    fun a6_el_quinto_fallo_bloquea_hasta_dentro_de_30_segundos() {
        val state = PinRules.afterFailure(previousFailedAttempts = 4, now = now)
        assertEquals(5, state.failedAttempts)
        assertEquals(now + 30.seconds, state.lockedUntil)
    }

    @Test
    fun a6_un_fallo_sin_llegar_a_5_no_bloquea() {
        val state = PinRules.afterFailure(previousFailedAttempts = 1, now = now)
        assertEquals(2, state.failedAttempts)
        assertNull(state.lockedUntil)
        assertEquals(3, PinRules.attemptsLeftBeforeLock(state.failedAttempts))
    }

    @Test
    fun a6_tiempo_restante_de_bloqueo() {
        assertEquals(20.seconds, PinRules.remainingLock(now + 20.seconds, now))
        assertEquals(Duration.ZERO, PinRules.remainingLock(now - 1.seconds, now))
        assertEquals(Duration.ZERO, PinRules.remainingLock(null, now))
    }

    @Test
    fun a5_pide_pin_desde_5_minutos_en_segundo_plano() {
        assertFalse(PinRules.requiresPinAfterBackground(now, now + 4.minutes + 59.seconds))
        assertTrue(PinRules.requiresPinAfterBackground(now, now + 5.minutes))
    }
}
