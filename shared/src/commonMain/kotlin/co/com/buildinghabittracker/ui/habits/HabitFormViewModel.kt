package co.com.buildinghabittracker.ui.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.buildinghabittracker.domain.habit.Habit
import co.com.buildinghabittracker.domain.habit.HabitErrors
import co.com.buildinghabittracker.domain.habit.HabitRepository
import co.com.buildinghabittracker.domain.habit.HabitValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * Estado del formulario de hábito.
 *
 * @property isEdit `true` al editar un hábito existente; `false` al crear uno nuevo.
 * @property showArchiveConfirm el diálogo "¿Archivar…?" está abierto.
 * @property isDone ya se guardó o archivó: la pantalla vuelve a la lista.
 */
data class HabitFormUiState(
    val isEdit: Boolean = false,
    val isLoading: Boolean = false,
    val title: String = "",
    val description: String = "",
    val emoji: String = HabitValidator.DEFAULT_EMOJI,
    val color: String = HabitValidator.DEFAULT_COLOR,
    // C3: un hábito nuevo empieza con todos los días marcados (como en el prototipo).
    val days: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
    val errors: HabitErrors = HabitErrors(),
    val showArchiveConfirm: Boolean = false,
    val isSaving: Boolean = false,
    val isDone: Boolean = false,
) {
    val allDaysSelected: Boolean get() = days.size == DayOfWeek.entries.size
}

/**
 * ViewModel del formulario para crear y editar hábitos (C1–C3) y archivarlos (C5).
 *
 * @param habitId el hábito a editar, o [Habit.NEW_ID] para crear uno nuevo. Llega desde la ruta.
 * @param clock y [timeZone] se usan para saber qué día es hoy (`createdAt`). En las pruebas
 * se reemplazan por valores fijos.
 */
class HabitFormViewModel(
    private val habitId: Long,
    private val habitRepository: HabitRepository,
    private val clock: Clock,
    private val timeZone: TimeZone,
) : ViewModel() {

    private val _state = MutableStateFlow(HabitFormUiState(isEdit = habitId != Habit.NEW_ID))
    val state: StateFlow<HabitFormUiState> = _state.asStateFlow()

    // El hábito tal como estaba guardado. Al editar se conservan sus datos que el formulario no muestra.
    private var original: Habit? = null

    init {
        if (habitId != Habit.NEW_ID) loadHabit()
    }

    private fun loadHabit() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val habit = habitRepository.getHabit(habitId)
            original = habit
            if (habit == null) {
                _state.update { it.copy(isLoading = false, isDone = true) }
                return@launch
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    title = habit.title,
                    description = habit.description.orEmpty(),
                    emoji = habit.emoji,
                    color = habit.color,
                    days = habit.days,
                )
            }
        }
    }

    fun onTitleChange(value: String) =
        _state.update { it.copy(title = value, errors = it.errors.copy(title = null)) }

    fun onDescriptionChange(value: String) =
        _state.update { it.copy(description = value, errors = it.errors.copy(description = null)) }

    fun onEmojiSelected(emoji: String) = _state.update { it.copy(emoji = emoji) }

    fun onColorSelected(color: String) = _state.update { it.copy(color = color) }

    /** Marca o desmarca un día. */
    fun onDayToggled(day: DayOfWeek) = _state.update {
        val days = if (day in it.days) it.days - day else it.days + day
        it.copy(days = days, errors = it.errors.copy(noDays = false))
    }

    /** C3: atajo "Todos los días". Si ya están todos, los quita. */
    fun onAllDaysToggled() = _state.update {
        val days = if (it.allDaysSelected) emptySet() else DayOfWeek.entries.toSet()
        it.copy(days = days, errors = it.errors.copy(noDays = false))
    }

    /** Valida (C1–C3) y guarda. Si hay errores, los muestra y no guarda nada. */
    fun onSave() {
        val current = _state.value
        if (current.isSaving) return
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val habits = habitRepository.observeHabits().first()
            val errors = HabitValidator.validate(current.title, current.description, current.days, habits, habitId)
            if (!errors.isValid) {
                _state.update { it.copy(isSaving = false, errors = errors) }
                return@launch
            }
            val description = current.description.trim().ifEmpty { null }
            val saved = original
            if (saved == null) {
                habitRepository.createHabit(
                    Habit(
                        title = current.title.trim(),
                        description = description,
                        emoji = current.emoji,
                        color = current.color,
                        days = current.days,
                        createdAt = clock.now().toLocalDateTime(timeZone).date,
                    ),
                )
            } else {
                // Al editar se conservan id, fecha de creación, archivado y recordatorio.
                habitRepository.updateHabit(
                    saved.copy(
                        title = current.title.trim(),
                        description = description,
                        emoji = current.emoji,
                        color = current.color,
                        days = current.days,
                    ),
                )
            }
            _state.update { it.copy(isSaving = false, isDone = true) }
        }
    }

    fun onArchiveRequested() = _state.update { it.copy(showArchiveConfirm = true) }

    fun onArchiveCancelled() = _state.update { it.copy(showArchiveConfirm = false) }

    /** C5: archiva el hábito (no se elimina) y vuelve a la lista. */
    fun onArchiveConfirmed() {
        if (original == null) return
        _state.update { it.copy(showArchiveConfirm = false) }
        viewModelScope.launch {
            habitRepository.setArchived(habitId, archived = true)
            _state.update { it.copy(isDone = true) }
        }
    }
}
