package co.com.buildinghabittracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

/** Los 3 pasos de Cambiar PIN (A8): actual, nuevo y confirmación. */
enum class ChangePinStep { CURRENT, NEW, CONFIRM }

data class ChangePinUiState(
    val step: ChangePinStep = ChangePinStep.CURRENT,
    val pin: String = "",
    val isVerifying: Boolean = false,
    /** Intentos que quedan antes del bloqueo; solo se muestra después de un fallo del PIN actual. */
    val attemptsLeft: Int? = null,
    /** Tiempo de bloqueo restante (A6); cero si puede escribir. */
    val lockRemaining: Duration = Duration.ZERO,
    val pinMismatch: Boolean = false,
    val isChanged: Boolean = false,
) {
    val isLocked: Boolean get() = lockRemaining > Duration.ZERO
}

/**
 * ViewModel de Cambiar PIN (regla A8).
 *
 * - Paso 1: el PIN actual. Los fallos cuentan para el bloqueo de A6, igual que en la entrada;
 *   si se bloquea, la pantalla muestra la cuenta regresiva y no deja escribir.
 * - Pasos 2 y 3: el PIN nuevo dos veces. Si no coinciden, se vuelve al paso 2.
 * Al terminar se guarda el PIN nuevo cifrado y se reinician los intentos.
 */
class ChangePinViewModel(
    private val userRepository: UserRepository,
    private val hasher: SecretHasher,
    private val clock: Clock,
) : ViewModel() {

    private val _state = MutableStateFlow(ChangePinUiState())
    val state: StateFlow<ChangePinUiState> = _state.asStateFlow()

    private var user: User? = null
    private var newPin: String? = null
    private var countdown: Job? = null

    init {
        viewModelScope.launch {
            val loaded = userRepository.getUser() ?: return@launch
            user = loaded
            // A6: si ya había un bloqueo guardado, se respeta aquí también.
            loaded.lockedUntil?.let { startCountdown(it) }
        }
    }

    fun onPinDigit(digit: Char) {
        val current = _state.value
        if (current.isLocked || current.isVerifying || current.pin.length >= PinRules.PIN_LENGTH) return
        val pin = current.pin + digit
        _state.update { it.copy(pin = pin, pinMismatch = if (current.step == ChangePinStep.NEW) it.pinMismatch else false) }
        if (pin.length == PinRules.PIN_LENGTH) onPinCompleted(pin)
    }

    fun onPinDelete() {
        if (_state.value.isVerifying) return
        _state.update { it.copy(pin = it.pin.dropLast(1)) }
    }

    private fun onPinCompleted(pin: String) {
        when (_state.value.step) {
            ChangePinStep.CURRENT -> verifyCurrent(pin)
            ChangePinStep.NEW -> {
                newPin = pin
                _state.update { it.copy(step = ChangePinStep.CONFIRM, pin = "", pinMismatch = false) }
            }
            ChangePinStep.CONFIRM -> {
                if (pin == newPin) {
                    save(pin)
                } else {
                    newPin = null
                    _state.update { it.copy(step = ChangePinStep.NEW, pin = "", pinMismatch = true) }
                }
            }
        }
    }

    private fun verifyCurrent(pin: String) {
        val currentUser = user ?: return
        _state.update { it.copy(isVerifying = true) }
        viewModelScope.launch {
            if (hasher.verify(pin, currentUser.pin)) {
                userRepository.updateLockState(failedAttempts = 0, lockedUntil = null)
                user = currentUser.copy(failedAttempts = 0, lockedUntil = null)
                _state.update { it.copy(isVerifying = false, step = ChangePinStep.NEW, pin = "", attemptsLeft = null) }
            } else {
                // A8 + A6: el fallo cuenta para el mismo bloqueo del PIN de entrada.
                val lock = PinRules.afterFailure(currentUser.failedAttempts, clock.now())
                userRepository.updateLockState(lock.failedAttempts, lock.lockedUntil)
                user = currentUser.copy(failedAttempts = lock.failedAttempts, lockedUntil = lock.lockedUntil)
                _state.update {
                    it.copy(pin = "", isVerifying = false, attemptsLeft = PinRules.attemptsLeftBeforeLock(lock.failedAttempts))
                }
                lock.lockedUntil?.let { startCountdown(it) }
            }
        }
    }

    private fun save(pin: String) {
        _state.update { it.copy(isVerifying = true) }
        viewModelScope.launch {
            userRepository.updatePin(hasher.hash(pin))
            userRepository.updateLockState(failedAttempts = 0, lockedUntil = null)
            newPin = null
            _state.update { it.copy(isVerifying = false, pin = "", isChanged = true) }
        }
    }

    private fun startCountdown(lockedUntil: Instant) {
        countdown?.cancel()
        countdown = viewModelScope.launchLockCountdown(lockedUntil, clock) { remaining ->
            _state.update { it.copy(lockRemaining = remaining) }
        }
    }
}
