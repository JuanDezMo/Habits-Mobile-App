package co.com.buildinghabittracker.ui.pin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class PinUiState(
    val userName: String = "",
    val pin: String = "",
    val isVerifying: Boolean = false,
    /** Intentos que quedan antes del bloqueo; solo se muestra después de un fallo. */
    val attemptsLeft: Int? = null,
    /** Tiempo de bloqueo restante (A6); cero si puede escribir. */
    val lockRemaining: Duration = Duration.ZERO,
    val isUnlocked: Boolean = false,
) {
    val isLocked: Boolean get() = lockRemaining > Duration.ZERO
}

/**
 * ViewModel de la pantalla de PIN (reglas A3, A5, A6).
 *
 * Al cargar revisa si hay un bloqueo guardado: cerrar y abrir la app no lo salta.
 */
class PinViewModel(
    private val userRepository: UserRepository,
    private val hasher: SecretHasher,
    private val clock: Clock,
    private val appLock: AppLock,
) : ViewModel() {

    private val _state = MutableStateFlow(PinUiState())
    val state: StateFlow<PinUiState> = _state.asStateFlow()

    private var user: User? = null
    private var countdown: Job? = null

    init {
        viewModelScope.launch {
            val loaded = userRepository.getUser() ?: return@launch
            user = loaded
            _state.update { it.copy(userName = loaded.name) }
            loaded.lockedUntil?.let { startCountdown(it) }
        }
    }

    fun onPinDigit(digit: Char) {
        val current = _state.value
        if (current.isLocked || current.isVerifying || current.pin.length >= PinRules.PIN_LENGTH) return
        val pin = current.pin + digit
        _state.update { it.copy(pin = pin) }
        if (pin.length == PinRules.PIN_LENGTH) verify(pin)
    }

    fun onPinDelete() {
        if (_state.value.isVerifying) return
        _state.update { it.copy(pin = it.pin.dropLast(1)) }
    }

    private fun verify(pin: String) {
        val currentUser = user ?: return
        _state.update { it.copy(isVerifying = true) }
        viewModelScope.launch {
            if (hasher.verify(pin, currentUser.pin)) {
                userRepository.updateLockState(failedAttempts = 0, lockedUntil = null)
                user = currentUser.copy(failedAttempts = 0, lockedUntil = null)
                appLock.unlock()
                _state.update { it.copy(isVerifying = false, isUnlocked = true) }
            } else {
                val lock = PinRules.afterFailure(currentUser.failedAttempts, clock.now())
                userRepository.updateLockState(lock.failedAttempts, lock.lockedUntil)
                user = currentUser.copy(failedAttempts = lock.failedAttempts, lockedUntil = lock.lockedUntil)
                _state.update {
                    it.copy(
                        pin = "",
                        isVerifying = false,
                        attemptsLeft = PinRules.attemptsLeftBeforeLock(lock.failedAttempts),
                    )
                }
                lock.lockedUntil?.let { startCountdown(it) }
            }
        }
    }

    private fun startCountdown(lockedUntil: kotlin.time.Instant) {
        countdown?.cancel()
        countdown = viewModelScope.launchLockCountdown(lockedUntil, clock) { remaining ->
            _state.update { it.copy(lockRemaining = remaining) }
        }
    }
}
