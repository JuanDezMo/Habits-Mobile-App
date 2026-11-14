package co.com.buildinghabittracker.ui.settings

import co.com.buildinghabittracker.fakes.FakeClock
import co.com.buildinghabittracker.fakes.FakeSecretHasher
import co.com.buildinghabittracker.fakes.FakeUserRepository
import co.com.buildinghabittracker.fakes.sampleUser
import co.com.buildinghabittracker.ui.MainDispatcherTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Pruebas de Cambiar PIN (A8) y su bloqueo (A6). El PIN de sampleUser es 1357. */
class ChangePinViewModelTest : MainDispatcherTest() {

    private val repository = FakeUserRepository(sampleUser())
    // `by lazy`: se crea dentro de cada prueba, cuando el dispatcher de pruebas ya está listo.
    private val vm by lazy { ChangePinViewModel(repository, FakeSecretHasher(), FakeClock()) }

    private fun type(pin: String) = pin.forEach { vm.onPinDigit(it) }

    @Test
    fun a8_pin_actual_nuevo_dos_veces_guarda_el_nuevo() {
        type("1357")
        assertEquals(ChangePinStep.NEW, vm.state.value.step)
        type("2468")
        assertEquals(ChangePinStep.CONFIRM, vm.state.value.step)
        type("2468")
        assertTrue(vm.state.value.isChanged)
        assertEquals("hashed:2468", repository.user?.pin?.hash)
    }

    @Test
    fun a8_si_el_nuevo_no_coincide_vuelve_a_crearlo() {
        type("1357")
        type("2468")
        type("1111")
        val state = vm.state.value
        assertEquals(ChangePinStep.NEW, state.step)
        assertTrue(state.pinMismatch)
        assertFalse(state.isChanged)
        assertEquals("hashed:1357", repository.user?.pin?.hash)
    }

    @Test
    fun a8_a6_fallos_del_pin_actual_cuentan_y_bloquean() {
        repeat(4) { type("0000") }
        assertEquals(1, vm.state.value.attemptsLeft)
        assertFalse(vm.state.value.isLocked)
        type("0000")
        assertTrue(vm.state.value.isLocked)
        assertEquals(5, repository.user?.failedAttempts)
        assertNotNull(repository.user?.lockedUntil)
        // Bloqueado: el teclado no acepta números.
        type("1357")
        assertEquals(ChangePinStep.CURRENT, vm.state.value.step)
    }
}
