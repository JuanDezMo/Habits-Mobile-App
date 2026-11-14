package co.com.buildinghabittracker.ui.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.buildinghabittracker.domain.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** A dónde debe ir la app al abrirse. */
enum class StartDestination { LOADING, REGISTER, PIN }

/**
 * Primer ViewModel que se ejecuta: revisa si ya hay usuario (regla A1).
 * - Sin usuario → Registro.
 * - Con usuario → PIN (regla A5: siempre se pide al abrir).
 */
class StartViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _destination = MutableStateFlow(StartDestination.LOADING)
    val destination: StateFlow<StartDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            val user = userRepository.getUser()
            _destination.value = if (user == null) StartDestination.REGISTER else StartDestination.PIN
        }
    }
}
