package co.com.buildinghabittracker.ui.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.buildinghabittracker.domain.habit.Habit
import co.com.buildinghabittracker.domain.habit.HabitRepository
import co.com.buildinghabittracker.domain.habit.HabitValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Las dos pestañas de la lista. */
enum class HabitsTab { ACTIVE, ARCHIVED }

/**
 * Estado de la pantalla Hábitos.
 *
 * @property unarchiveBlocked `true` si el último intento de desarchivar no se pudo por C1.
 * La pantalla muestra el aviso y luego llama a `onUnarchiveMessageShown`.
 */
data class HabitsUiState(
    val isLoading: Boolean = true,
    val tab: HabitsTab = HabitsTab.ACTIVE,
    val active: List<Habit> = emptyList(),
    val archived: List<Habit> = emptyList(),
    val unarchiveBlocked: Boolean = false,
) {
    /** Los hábitos de la pestaña elegida. */
    val visible: List<Habit> get() = if (tab == HabitsTab.ACTIVE) active else archived
}

/**
 * ViewModel de la lista de hábitos (C5: activos y archivados, desarchivar).
 *
 * La lista viene de un `Flow` de Room: cuando el formulario guarda o archiva un hábito,
 * esta pantalla se entera sola, sin recargar nada.
 */
class HabitsViewModel(
    private val habitRepository: HabitRepository,
) : ViewModel() {

    // Lo que decide el usuario en esta pantalla (no viene de la base de datos).
    private val tab = MutableStateFlow(HabitsTab.ACTIVE)
    private val unarchiveBlocked = MutableStateFlow(false)

    /**
     * `combine` junta tres fuentes en un solo estado: los hábitos guardados, la pestaña y el aviso.
     * Si cualquiera cambia, se arma un estado nuevo.
     * `stateIn` lo convierte en un `StateFlow` que la pantalla puede observar.
     */
    val state: StateFlow<HabitsUiState> =
        combine(habitRepository.observeHabits(), tab, unarchiveBlocked) { habits, tab, blocked ->
            HabitsUiState(
                isLoading = false,
                tab = tab,
                active = habits.filter { !it.isArchived },
                archived = habits.filter { it.isArchived },
                unarchiveBlocked = blocked,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HabitsUiState())

    fun onTabSelected(selected: HabitsTab) = tab.update { selected }

    /** C5: vuelve a activar un hábito, salvo que su título ya lo use otro activo (C1). */
    fun onUnarchive(habitId: Long) {
        viewModelScope.launch {
            val habits = habitRepository.observeHabits().first()
            val habit = habits.firstOrNull { it.id == habitId } ?: return@launch
            if (!HabitValidator.canUnarchive(habit, habits)) {
                unarchiveBlocked.update { true }
                return@launch
            }
            habitRepository.setArchived(habitId, archived = false)
            tab.update { HabitsTab.ACTIVE }
        }
    }

    fun onUnarchiveMessageShown() = unarchiveBlocked.update { false }
}
