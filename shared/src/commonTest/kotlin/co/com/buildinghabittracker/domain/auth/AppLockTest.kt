package co.com.buildinghabittracker.domain.auth

import co.com.buildinghabittracker.fakes.FakeClock
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

/** Pruebas de la regla A5 con un reloj falso: no hay que esperar 5 minutos de verdad. */
class AppLockTest {

    private val clock = FakeClock()
    private val appLock = AppLock(clock)

    @Test
    fun a5_volver_antes_de_5_minutos_no_pide_pin() {
        appLock.unlock()
        appLock.onAppBackground()
        clock.advanceBy(4.minutes)
        assertFalse(appLock.onAppForeground())
        assertTrue(appLock.isUnlocked)
    }

    @Test
    fun a5_volver_despues_de_5_minutos_pide_pin() {
        appLock.unlock()
        appLock.onAppBackground()
        clock.advanceBy(5.minutes)
        assertTrue(appLock.onAppForeground())
        assertFalse(appLock.isUnlocked)
    }

    @Test
    fun a5_si_no_ha_entrado_no_cuenta_el_tiempo() {
        appLock.onAppBackground()
        clock.advanceBy(10.minutes)
        assertFalse(appLock.onAppForeground())
    }
}
