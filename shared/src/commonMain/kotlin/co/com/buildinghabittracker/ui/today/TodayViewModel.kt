package co.com.buildinghabittracker.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.buildinghabittracker.domain.checkin.CheckIn
import co.com.buildinghabittracker.domain.checkin.CheckInRepository
import co.com.buildinghabittracker.domain.checkin.CheckInRules
import co.com.buildinghabittracker.domain.checkin.StreakCalculator
import co.com.buildinghabittracker.domain.checkin.yesterday
import co.com.buildinghabittracker.domain.common.TodayProvider
import co.com.buildinghabittracker.domain.habit.Habit
import co.com.buildinghabittracker.domain.habit.HabitRepository
import co.com.buildinghabittracker.domain.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * Un hábito como se ve en Hoy.
 *
 * @property date el día que se marca con esta cápsula: hoy, o ayer en la sección "Ayer".
 * @property streak racha actual (E1, E2); solo se muestra en las cápsulas de hoy.
 */
data class TodayHabit(
    val habit: Habit,
    val date: LocalDate,
    val isDone: Boolean,
    val streak: Int = 0,
)

/**
 * Aviso "Meditar desmarcado · Deshacer".
 * @property eventId cambia en cada desmarcado, para que el aviso salga aunque sea el mismo hábito.
 */
data class UndoUnmark(
    val habitId: Long,
    val date: LocalDate,
    val title: String,
    val eventId: Long,
)

/**
 * Estado de la pantalla Hoy.
 *
 * @property hasHabits `false` si no hay ningún hábito activo: se muestra "Crear mi primer hábito".
 * @property today los hábitos que tocan hoy, en orden de creación (no se mueven al marcar).
 * @property yesterday "Ayer": los que tocaban ayer y siguen sin marcar, más los que se
 * marcaron hoy para ayer (quedan con ✓ hasta medianoche, para poder desmarcarlos).
 */
data class TodayUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val date: LocalDate? = null,
    val hasHabits: Boolean = false,
    val today: List<TodayHabit> = emptyList(),
    val yesterday: List<TodayHabit> = emptyList(),
    val undo: UndoUnmark? = null,
) {
    val doneCount: Int get() = today.count { it.isDone }
    val pending: List<TodayHabit> get() = today.filter { !it.isDone }
}

/**
 * ViewModel de Hoy (D1–D3, E1, E2).
 *
 * Junta cuatro fuentes con `combine`: los hábitos, los check-ins, el día de hoy y el aviso
 * de deshacer. Si cualquiera cambia (por ejemplo, al marcar o a medianoche), se arma un
 * estado nuevo y la pantalla se actualiza sola.
 */
class TodayViewModel(
    private val habitRepository: HabitRepository,
    private val checkInRepository: CheckInRepository,
    private val userRepository: UserRepository,
    private val todayProvider: TodayProvider,
    private val clock: Clock,
    private val timeZone: TimeZone,
) : ViewModel() {

    private val undo = MutableStateFlow<UndoUnmark?>(null)
    private var nextEventId = 0L

    private val userName = flow { emit(userRepository.getUser()?.name.orEmpty()) }

    private val content = combine(
        habitRepository.observeHabits(),
        checkInRepository.observeCheckIns(),
        todayProvider.observeToday(),
    ) { habits, checkIns, today -> buildState(habits, checkIns, today) }

    val state: StateFlow<TodayUiState> =
        combine(content, userName, undo) { content, name, undo ->
            content.copy(userName = name, undo = undo)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodayUiState())

    private fun buildState(habits: List<Habit>, checkIns: List<CheckIn>, today: LocalDate): TodayUiState {
        val active = habits.filter { !it.isArchived }
        val byHabit = checkIns.groupBy { it.habitId }
        val yesterday = today.yesterday()

        val todayList = active
            .filter { CheckInRules.isScheduled(it, today) }
            .map { habit ->
                val dates = byHabit[habit.id].orEmpty().map { it.date }.toSet()
                TodayHabit(
                    habit = habit,
                    date = today,
                    isDone = today in dates,
                    streak = StreakCalculator.currentStreak(habit, dates, today),
                )
            }

        val yesterdayList = active
            .filter { CheckInRules.isScheduled(it, yesterday) }
            .mapNotNull { habit ->
                val checkIn = byHabit[habit.id].orEmpty().firstOrNull { it.date == yesterday }
                when {
                    checkIn == null -> TodayHabit(habit, yesterday, isDone = false)
                    // Se marcó hoy para ayer: sigue visible con ✓ hasta medianoche.
                    checkIn.checkedAt.toLocalDateTime(timeZone).date == today -> TodayHabit(habit, yesterday, isDone = true)
                    // Ya estaba marcado desde ayer: no hace falta mostrarlo.
                    else -> null
                }
            }

        return TodayUiState(
            isLoading = false,
            date = today,
            hasHabits = active.isNotEmpty(),
            today = todayList,
            yesterday = yesterdayList,
        )
    }

    /** Toque en una cápsula: marca si no estaba hecho; desmarca (con aviso para deshacer) si sí. */
    fun onToggle(item: TodayHabit) {
        val today = state.value.date ?: return
        // D1 y D3: solo hoy o ayer, en días programados. La pantalla no ofrece otros días,
        // pero la regla se revisa aquí también.
        if (!CheckInRules.canToggle(item.habit, item.date, today)) return
        viewModelScope.launch {
            if (item.isDone) {
                checkInRepository.remove(item.habit.id, item.date)
                undo.update { UndoUnmark(item.habit.id, item.date, item.habit.title, nextEventId++) }
            } else {
                checkInRepository.add(CheckIn(item.habit.id, item.date, clock.now()))
            }
        }
    }

    /** "Deshacer": vuelve a marcar lo que se acaba de desmarcar. */
    fun onUndo(event: UndoUnmark) {
        undo.update { null }
        viewModelScope.launch {
            checkInRepository.add(CheckIn(event.habitId, event.date, clock.now()))
        }
    }

    /** El aviso se cerró sin deshacer. */
    fun onUndoDismissed(event: UndoUnmark) {
        undo.update { if (it?.eventId == event.eventId) null else it }
    }
}
