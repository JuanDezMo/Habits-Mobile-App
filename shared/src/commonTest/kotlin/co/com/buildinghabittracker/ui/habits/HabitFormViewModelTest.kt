package co.com.buildinghabittracker.ui.habits

import co.com.buildinghabittracker.domain.common.FieldError
import co.com.buildinghabittracker.domain.habit.Habit
import co.com.buildinghabittracker.domain.habit.HabitValidator
import co.com.buildinghabittracker.fakes.FakeClock
import co.com.buildinghabittracker.fakes.FakeHabitRepository
import co.com.buildinghabittracker.fakes.sampleHabit
import co.com.buildinghabittracker.ui.MainDispatcherTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Pruebas del formulario: crear y editar (C1–C3) y archivar (C5). */
class HabitFormViewModelTest : MainDispatcherTest() {

    private val gym = sampleHabit(id = 1, title = "Gym")

    private fun viewModel(habitId: Long = Habit.NEW_ID, repository: FakeHabitRepository) =
        HabitFormViewModel(habitId, repository, FakeClock(), TimeZone.UTC)

    @Test
    fun c2_c3_un_habito_nuevo_empieza_con_valores_por_defecto() {
        val state = viewModel(repository = FakeHabitRepository()).state.value
        assertFalse(state.isEdit)
        assertEquals(HabitValidator.DEFAULT_EMOJI, state.emoji)
        assertEquals(HabitValidator.DEFAULT_COLOR, state.color)
        assertTrue(state.allDaysSelected)
    }

    @Test
    fun crear_guarda_el_habito_con_la_fecha_de_hoy() {
        val repository = FakeHabitRepository()
        val vm = viewModel(repository = repository)
        vm.onTitleChange("  Leer ")
        vm.onDescriptionChange("   ")
        vm.onEmojiSelected("📖")
        vm.onSave()

        assertTrue(vm.state.value.isDone)
        val saved = repository.saved.single()
        assertEquals("Leer", saved.title)
        assertNull(saved.description) // C2: descripción vacía = sin descripción
        assertEquals("📖", saved.emoji)
        assertEquals(LocalDate(2026, 11, 15), saved.createdAt) // FakeClock: 2026-11-15T10:00Z
        assertFalse(saved.isArchived)
    }

    @Test
    fun c1_no_guarda_un_titulo_repetido() {
        val repository = FakeHabitRepository(listOf(gym))
        val vm = viewModel(repository = repository)
        vm.onTitleChange("GYM")
        vm.onSave()

        assertEquals(FieldError.DUPLICATE, vm.state.value.errors.title)
        assertFalse(vm.state.value.isDone)
        assertEquals(1, repository.saved.size)
    }

    @Test
    fun c3_quitar_todos_los_dias_impide_guardar() {
        val repository = FakeHabitRepository()
        val vm = viewModel(repository = repository)
        vm.onTitleChange("Leer")
        vm.onAllDaysToggled() // estaban todos → se quitan
        vm.onSave()

        assertTrue(vm.state.value.errors.noDays)
        assertTrue(repository.saved.isEmpty())
    }

    @Test
    fun editar_carga_el_habito_y_conserva_fecha_y_id() {
        val repository = FakeHabitRepository(listOf(gym))
        val vm = viewModel(habitId = gym.id, repository = repository)
        assertEquals("Gym", vm.state.value.title)
        assertEquals(gym.days, vm.state.value.days)

        vm.onTitleChange("Gimnasio")
        vm.onDayToggled(DayOfWeek.SATURDAY)
        vm.onSave()

        val saved = repository.saved.single()
        assertEquals(gym.id, saved.id)
        assertEquals("Gimnasio", saved.title)
        assertTrue(DayOfWeek.SATURDAY in saved.days)
        assertEquals(gym.createdAt, saved.createdAt)
    }

    @Test
    fun c5_archivar_pide_confirmacion_y_no_elimina() {
        val repository = FakeHabitRepository(listOf(gym))
        val vm = viewModel(habitId = gym.id, repository = repository)

        vm.onArchiveRequested()
        assertTrue(vm.state.value.showArchiveConfirm)
        vm.onArchiveCancelled()
        assertFalse(repository.saved.single().isArchived)

        vm.onArchiveRequested()
        vm.onArchiveConfirmed()
        assertTrue(vm.state.value.isDone)
        assertTrue(repository.saved.single().isArchived)
    }
}
