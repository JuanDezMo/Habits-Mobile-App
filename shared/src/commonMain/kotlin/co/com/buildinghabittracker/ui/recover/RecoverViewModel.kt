package co.com.buildinghabittracker.ui.recover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.buildinghabittracker.domain.auth.AnswerNormalizer
import co.com.buildinghabittracker.domain.auth.AppLock
import co.com.buildinghabittracker.domain.auth.PinRules
import co.com.buildinghabittracker.domain.security.SecretHasher
import co.com.buildinghabittracker.domain.user.User
import co.com.buildinghabittracker.domain.user.UserRepository
import co.com.buildinghabittracker.ui.auth.launchLockCountdown
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

/** Pasos de la recuperación (regla A7). */
enum class RecoverStep { ANSWER, NEW_PIN, CONFIRM_PIN }

data class RecoverUiState(
    val step: RecoverStep = RecoverStep.ANSWER,
    val question: String = "",
    val answer: String = "",
    val isVerifying: Boolean = false,
    val attemptsLeft: Int? = null,
    val lockRemaining: Duration = Duration.ZERO,
    val pin: String = "",
    val pinMismatch: Boolean = false,
    val isRecovered: Boolean = false,
) {
    val isLocked: Boolean get() = lockRemaining > Duration.ZERO
}

/**
 * ViewModel de "Olvidé mi PIN" (reglas A4, A6, A7).
 *
 * 1. Responde la pregunta: los fallos cuentan para el mismo bloqueo que el PIN (A6).
 * 2. Si acierta, el contador vuelve a 0 y crea un PIN nuevo dos veces.
 * 3. Se guarda el PIN nuevo sin tocar los demás datos, y la app queda desbloqueada.
 */
class RecoverViewModel(
    private val userRepository: UserRepository,
    private val hasher: SecretHasher,
    private val clock: Clock,
    private val appLock: AppLock,
) : ViewModel() {

    private val _state = MutableStateFlow(RecoverUiState())
    val state: StateFlow<RecoverUiState> = _state.asStateFlow()

    private var user: User? = null
    private var newPin: String? = null
    private var countdown: Job? = null

    init {
        viewModelScope.launch {
            val loaded = userRepository.getUser() ?: return@launch
            user = loaded
            _state.update { it.copy(question = loaded.securityQuestion) }
            loaded.lockedUntil?.let { startCountdown(it) }
        }
    }

    fun onAnswerChange(value: String) = _state.update { it.copy(answer = value) }

    fun onVerifyAnswer() {
        val current = _state.value
        val currentUser = user ?: return
        if (current.isLocked || current.isVerifying || current.answer.isBlank()) return
        _state.update { it.copy(isVerifying = true) }
        viewModelScope.launch {
            val normalized = AnswerNormalizer.normalize(current.answer)
            if (hasher.verify(normalized, currentUser.securityAnswer)) {
                // A7: demostró ser el dueño → el contador de fallos vuelve a 0.
                userRepository.updateLockState(failedAttempts = 0, lockedUntil = null)
                user = currentUser.copy(failedAttempts = 0, lockedUntil = null)
                _state.update { it.copy(isVerifying = false, step = RecoverStep.NEW_PIN, answer = "") }
            } else {
                val lock = PinRules.afterFailure(currentUser.failedAttempts, clock.now())
                userRepository.updateLockState(lock.failedAttempts, lock.lockedUntil)
                user = currentUser.copy(failedAttempts = lock.failedAttempts, lockedUntil = lock.lockedUntil)
                _state.update {
                    it.copy(isVerifying = false, attemptsLeft = PinRules.attemptsLeftBeforeLock(lock.failedAttempts))
                }
                lock.lockedUntil?.let { startCountdown(it) }
            }
        }
    }

    fun onPinDigit(digit: Char) {
        val current = _state.value
        if (current.step == RecoverStep.ANSWER || current.pin.length >= PinRules.PIN_LENGTH) return
        val pin = current.pin + digit
        _state.update { it.copy(pin = pin, pinMismatch = false) }
        if (pin.length == PinRules.PIN_LENGTH) onPinCompleted(pin)
    }

    fun onPinDelete() = _state.update { it.copy(pin = it.pin.dropLast(1)) }

    private fun onPinCompleted(pin: String) {
        when (_state.value.step) {
            RecoverStep.NEW_PIN -> {
                newPin = pin
                _state.update { it.copy(step = RecoverStep.CONFIRM_PIN, pin = "") }
            }
            RecoverStep.CONFIRM_PIN -> {
                if (pin == newPin) {
                    savePin(pin)
                } else {
                    newPin = null
                    _state.update { it.copy(step = RecoverStep.NEW_PIN, pin = "", pinMismatch = true) }
                }
            }
            RecoverStep.ANSWER -> Unit
        }
    }

    private fun savePin(pin: String) {
        viewModelScope.launch {
            userRepository.updatePin(hasher.hash(pin))
            newPin = null
            appLock.unlock()
            _state.update { it.copy(pin = "", isRecovered = true) }
        }
    }

    private fun startCountdown(lockedUntil: Instant) {
        countdown?.cancel()
        countdown = viewModelScope.launchLockCountdown(lockedUntil, clock) { remaining ->
            _state.update { it.copy(lockRemaining = remaining) }
        }
    }
}
