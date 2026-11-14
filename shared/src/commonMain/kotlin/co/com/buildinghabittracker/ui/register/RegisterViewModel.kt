package co.com.buildinghabittracker.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.buildinghabittracker.domain.auth.AnswerNormalizer
import co.com.buildinghabittracker.domain.auth.FieldError
import co.com.buildinghabittracker.domain.auth.PinRules
import co.com.buildinghabittracker.domain.auth.RegistrationRules
import co.com.buildinghabittracker.domain.security.SecretHasher
import co.com.buildinghabittracker.domain.user.User
import co.com.buildinghabittracker.domain.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

/** Los 4 pasos del registro (regla A1). */
enum class RegisterStep { DATA, CREATE_PIN, CONFIRM_PIN, SECURITY }

/**
 * TODO lo que la pantalla de registro necesita para dibujarse. Es inmutable (`val`):
 * para cambiar algo, el ViewModel crea una copia con `copy(...)`.
 */
data class RegisterUiState(
    val step: RegisterStep = RegisterStep.DATA,
    val name: String = "",
    val email: String = "",
    val nameError: FieldError? = null,
    val emailError: FieldError? = null,
    val pin: String = "",
    val pinMismatch: Boolean = false,
    val question: String = "",
    val answer: String = "",
    val questionError: FieldError? = null,
    val answerError: FieldError? = null,
    val isSaving: Boolean = false,
    val isRegistered: Boolean = false,
)

/**
 * ViewModel del registro: recibe lo que hace el usuario y actualiza [state].
 *
 * Reglas que aplica: A1 (pasos y volver), A2 (nombre y email), A3 (PIN), A4 (pregunta).
 * Nada se guarda hasta el último paso: si la app se cierra antes, este ViewModel desaparece
 * y el registro empieza de nuevo.
 */
class RegisterViewModel(
    private val userRepository: UserRepository,
    private val hasher: SecretHasher,
    private val clock: Clock,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    // El PIN del paso 2, para compararlo en el paso 3. No es parte del UiState: la pantalla no lo muestra.
    private var createdPin: String? = null

    fun onNameChange(value: String) = _state.update { it.copy(name = value, nameError = null) }

    fun onEmailChange(value: String) = _state.update { it.copy(email = value, emailError = null) }

    fun onQuestionChange(value: String) = _state.update { it.copy(question = value, questionError = null) }

    fun onAnswerChange(value: String) = _state.update { it.copy(answer = value, answerError = null) }

    /** Paso 1 → 2, solo si nombre y email son válidos (A2). */
    fun onContinueFromData() {
        val current = _state.value
        val nameError = RegistrationRules.validateName(current.name)
        val emailError = RegistrationRules.validateEmail(current.email)
        if (nameError != null || emailError != null) {
            _state.update { it.copy(nameError = nameError, emailError = emailError) }
            return
        }
        _state.update { it.copy(step = RegisterStep.CREATE_PIN, pin = "", pinMismatch = false) }
    }

    fun onPinDigit(digit: Char) {
        val current = _state.value
        if (current.pin.length >= PinRules.PIN_LENGTH) return
        val pin = current.pin + digit
        _state.update { it.copy(pin = pin, pinMismatch = false) }
        if (pin.length == PinRules.PIN_LENGTH) onPinCompleted(pin)
    }

    fun onPinDelete() = _state.update { it.copy(pin = it.pin.dropLast(1)) }

    private fun onPinCompleted(pin: String) {
        when (_state.value.step) {
            RegisterStep.CREATE_PIN -> {
                createdPin = pin
                _state.update { it.copy(step = RegisterStep.CONFIRM_PIN, pin = "") }
            }
            RegisterStep.CONFIRM_PIN -> {
                if (pin == createdPin) {
                    _state.update { it.copy(step = RegisterStep.SECURITY, pin = "") }
                } else {
                    // A3: si no coinciden, se vuelve a crear el PIN desde cero.
                    createdPin = null
                    _state.update { it.copy(step = RegisterStep.CREATE_PIN, pin = "", pinMismatch = true) }
                }
            }
            else -> Unit
        }
    }

    /** A1: volver al paso anterior conservando lo escrito. Volver a "Crear PIN" borra el PIN. */
    fun onBack() {
        val previous = when (_state.value.step) {
            RegisterStep.DATA -> return
            RegisterStep.CREATE_PIN -> RegisterStep.DATA
            RegisterStep.CONFIRM_PIN, RegisterStep.SECURITY -> RegisterStep.CREATE_PIN
        }
        if (previous == RegisterStep.CREATE_PIN) createdPin = null
        _state.update { it.copy(step = previous, pin = "", pinMismatch = false) }
    }

    /** Paso 4: valida la pregunta (A4) y guarda al usuario con el PIN y la respuesta cifrados (A3). */
    fun onFinish() {
        val current = _state.value
        val pin = createdPin ?: return
        if (current.isSaving) return
        val questionError = RegistrationRules.validateQuestion(current.question)
        val answerError = RegistrationRules.validateAnswer(current.answer)
        if (questionError != null || answerError != null) {
            _state.update { it.copy(questionError = questionError, answerError = answerError) }
            return
        }
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val user = User(
                name = current.name.trim(),
                email = current.email.trim(),
                pin = hasher.hash(pin),
                securityQuestion = current.question.trim(),
                securityAnswer = hasher.hash(AnswerNormalizer.normalize(current.answer)),
                failedAttempts = 0,
                lockedUntil = null,
                createdAt = clock.now(),
            )
            userRepository.createUser(user)
            createdPin = null
            _state.update { it.copy(isSaving = false, isRegistered = true) }
        }
    }
}
