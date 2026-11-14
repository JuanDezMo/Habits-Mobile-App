package co.com.buildinghabittracker.ui.recover

import co.com.buildinghabittracker.domain.auth.AppLock
import co.com.buildinghabittracker.fakes.FakeClock
import co.com.buildinghabittracker.fakes.FakeSecretHasher
import co.com.buildinghabittracker.fakes.FakeUserRepository
import co.com.buildinghabittracker.fakes.sampleUser
import co.com.buildinghabittracker.ui.MainDispatcherTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Pruebas de "Olvidé mi PIN" (A4, A6, A7). La respuesta correcta es "firulais". */
class RecoverViewModelTest : MainDispatcherTest() {

    private val clock = FakeClock()
    private val appLock = AppLock(clock)

    private fun createViewModel(repository: FakeUserRepository) =
        RecoverViewModel(repository, FakeSecretHasher(), clock, appLock)

    private fun RecoverViewModel.type(pin: String) = pin.forEach { onPinDigit(it) }

    @Test
    fun muestra_la_pregunta_del_usuario() {
        val viewModel = createViewModel(FakeUserRepository(sampleUser()))
        assertEquals("¿Nombre de mi primera mascota?", viewModel.state.value.question)
    }

    @Test
    fun a6_respuesta_incorrecta_suma_al_mismo_contador_que_el_pin() {
        val repository = FakeUserRepository(sampleUser(failedAttempts = 2))
        val viewModel = createViewModel(repository)
        viewModel.onAnswerChange("michi")
        viewModel.onVerifyAnswer()
        assertEquals(3, repository.user?.failedAttempts)
        assertEquals(2, viewModel.state.value.attemptsLeft)
        assertEquals(RecoverStep.ANSWER, viewModel.state.value.step)
    }

    @Test
    fun a4_a7_respuesta_correcta_sin_importar_mayusculas_ni_acentos_reinicia_el_contador() {
        val repository = FakeUserRepository(sampleUser(failedAttempts = 3))
        val viewModel = createViewModel(repository)
        viewModel.onAnswerChange("  FIRULÁIS ")
        viewModel.onVerifyAnswer()
        assertEquals(RecoverStep.NEW_PIN, viewModel.state.value.step)
        assertEquals(0, repository.user?.failedAttempts)
    }

    @Test
    fun a7_crea_pin_nuevo_sin_perder_datos_y_desbloquea() {
        val repository = FakeUserRepository(sampleUser())
        val viewModel = createViewModel(repository)
        viewModel.onAnswerChange("firulais")
        viewModel.onVerifyAnswer()
        viewModel.type("2468")
        viewModel.type("2468")

        assertTrue(viewModel.state.value.isRecovered)
        assertTrue(appLock.isUnlocked)
        assertEquals("hashed:2468", repository.user?.pin?.hash)
        assertEquals("Ana", repository.user?.name)
    }

    @Test
    fun a7_si_el_pin_nuevo_no_coincide_se_crea_otra_vez() {
        val repository = FakeUserRepository(sampleUser())
        val viewModel = createViewModel(repository)
        viewModel.onAnswerChange("firulais")
        viewModel.onVerifyAnswer()
        viewModel.type("2468")
        viewModel.type("1111")
        assertEquals(RecoverStep.NEW_PIN, viewModel.state.value.step)
        assertTrue(viewModel.state.value.pinMismatch)
        assertEquals("hashed:1357", repository.user?.pin?.hash)
    }
}
