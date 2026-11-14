package co.com.buildinghabittracker.ui.register

import co.com.buildinghabittracker.domain.auth.FieldError
import co.com.buildinghabittracker.fakes.FakeClock
import co.com.buildinghabittracker.fakes.FakeSecretHasher
import co.com.buildinghabittracker.fakes.FakeUserRepository
import co.com.buildinghabittracker.ui.MainDispatcherTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Pruebas del registro (A1–A4) usando repositorio y hasher falsos. */
class RegisterViewModelTest : MainDispatcherTest() {

    private val repository = FakeUserRepository()
    private val viewModel = RegisterViewModel(repository, FakeSecretHasher(), FakeClock())

    private fun typePin(pin: String) = pin.forEach { viewModel.onPinDigit(it) }

    private fun completeDataStep() {
        viewModel.onNameChange("Ana")
        viewModel.onEmailChange("ana@correo.com")
        viewModel.onContinueFromData()
    }

    @Test
    fun a2_no_avanza_con_datos_invalidos() {
        viewModel.onEmailChange("ana")
        viewModel.onContinueFromData()
        val state = viewModel.state.value
        assertEquals(RegisterStep.DATA, state.step)
        assertEquals(FieldError.REQUIRED, state.nameError)
        assertEquals(FieldError.INVALID_EMAIL, state.emailError)
    }

    @Test
    fun a3_pin_que_no_coincide_vuelve_a_crear_pin() {
        completeDataStep()
        typePin("1357")
        assertEquals(RegisterStep.CONFIRM_PIN, viewModel.state.value.step)
        typePin("2468")
        val state = viewModel.state.value
        assertEquals(RegisterStep.CREATE_PIN, state.step)
        assertTrue(state.pinMismatch)
    }

    @Test
    fun a1_volver_conserva_nombre_y_email_y_borra_el_pin() {
        completeDataStep()
        typePin("1357")
        typePin("1357")
        assertEquals(RegisterStep.SECURITY, viewModel.state.value.step)
        viewModel.onBack()
        assertEquals(RegisterStep.CREATE_PIN, viewModel.state.value.step)
        viewModel.onBack()
        val state = viewModel.state.value
        assertEquals(RegisterStep.DATA, state.step)
        assertEquals("Ana", state.name)
        assertEquals("ana@correo.com", state.email)
    }

    @Test
    fun a4_no_guarda_con_pregunta_o_respuesta_cortas() {
        completeDataStep()
        typePin("1357")
        typePin("1357")
        viewModel.onQuestionChange("¿Qu")
        viewModel.onAnswerChange("ab")
        viewModel.onFinish()
        assertEquals(FieldError.TOO_SHORT, viewModel.state.value.questionError)
        assertEquals(FieldError.TOO_SHORT, viewModel.state.value.answerError)
        assertNull(repository.user)
    }

    @Test
    fun a1_a3_a4_registro_completo_guarda_pin_y_respuesta_cifrados() {
        completeDataStep()
        typePin("1357")
        typePin("1357")
        viewModel.onQuestionChange("¿Nombre de mi primera mascota?")
        viewModel.onAnswerChange("  Firuláis ")
        viewModel.onFinish()

        assertTrue(viewModel.state.value.isRegistered)
        val saved = assertNotNull(repository.user)
        assertEquals("Ana", saved.name)
        assertEquals("hashed:1357", saved.pin.hash)
        // A4: la respuesta se guarda normalizada (sin mayúsculas, acentos ni espacios extra).
        assertEquals("hashed:firulais", saved.securityAnswer.hash)
        assertEquals(0, saved.failedAttempts)
    }
}
