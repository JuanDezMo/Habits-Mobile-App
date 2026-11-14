package co.com.buildinghabittracker.ui.pin

import co.com.buildinghabittracker.domain.auth.AppLock
import co.com.buildinghabittracker.fakes.FakeClock
import co.com.buildinghabittracker.fakes.FakeSecretHasher
import co.com.buildinghabittracker.fakes.FakeUserRepository
import co.com.buildinghabittracker.fakes.sampleUser
import co.com.buildinghabittracker.ui.MainDispatcherTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

/** Pruebas de la pantalla de PIN (A3, A6) con el usuario de ejemplo (PIN 1357). */
class PinViewModelTest : MainDispatcherTest() {

    private val clock = FakeClock()
    private val appLock = AppLock(clock)

    private fun createViewModel(repository: FakeUserRepository) =
        PinViewModel(repository, FakeSecretHasher(), clock, appLock)

    private fun PinViewModel.type(pin: String) = pin.forEach { onPinDigit(it) }

    @Test
    fun pin_correcto_desbloquea_y_reinicia_el_contador() {
        val repository = FakeUserRepository(sampleUser(failedAttempts = 3))
        val viewModel = createViewModel(repository)
        viewModel.type("1357")
        assertTrue(viewModel.state.value.isUnlocked)
        assertTrue(appLock.isUnlocked)
        assertEquals(0, repository.user?.failedAttempts)
    }

    @Test
    fun a6_pin_incorrecto_suma_un_fallo_y_muestra_intentos_restantes() {
        val repository = FakeUserRepository(sampleUser())
        val viewModel = createViewModel(repository)
        viewModel.type("0000")
        val state = viewModel.state.value
        assertFalse(state.isUnlocked)
        assertEquals("", state.pin)
        assertEquals(4, state.attemptsLeft)
        assertEquals(1, repository.user?.failedAttempts)
    }

    @Test
    fun a6_el_quinto_fallo_bloquea_30_segundos_e_ignora_el_teclado() {
        val repository = FakeUserRepository(sampleUser(failedAttempts = 4))
        val viewModel = createViewModel(repository)
        viewModel.type("0000")
        assertTrue(viewModel.state.value.isLocked)
        assertEquals(clock.now() + 30.seconds, repository.user?.lockedUntil)

        viewModel.type("1357")
        assertEquals("", viewModel.state.value.pin)
        assertFalse(viewModel.state.value.isUnlocked)
    }

    @Test
    fun a6_el_bloqueo_guardado_se_respeta_al_abrir_la_app() {
        val repository = FakeUserRepository(sampleUser(failedAttempts = 5, lockedUntil = clock.now() + 20.seconds))
        val viewModel = createViewModel(repository)
        assertTrue(viewModel.state.value.isLocked)
        assertEquals(20.seconds, viewModel.state.value.lockRemaining)
    }

    @Test
    fun a6_despues_del_bloqueo_se_puede_volver_a_intentar() {
        val repository = FakeUserRepository(sampleUser(failedAttempts = 5, lockedUntil = clock.now() + 20.seconds))
        clock.advanceBy(21.seconds)
        val viewModel = createViewModel(repository)
        assertFalse(viewModel.state.value.isLocked)
        viewModel.type("1357")
        assertTrue(viewModel.state.value.isUnlocked)
        assertNull(repository.user?.lockedUntil)
    }
}
