package co.com.buildinghabittracker.ui.habits

import app.cash.turbine.test
import co.com.buildinghabittracker.fakes.FakeHabitRepository
import co.com.buildinghabittracker.fakes.sampleHabit
import co.com.buildinghabittracker.ui.MainDispatcherTest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Pruebas de la lista de hábitos (C5).
 *
 * `state` solo se calcula mientras alguien lo observa (`WhileSubscribed`), por eso se usa
 * Turbine (`test { }`): se suscribe y deja leer cada estado que emite.
 */
class HabitsViewModelTest : MainDispatcherTest() {

    @Test
    fun separa_activos_y_archivados() = runTest {
        val repository = FakeHabitRepository(
            listOf(sampleHabit(id = 1, title = "Gym"), sampleHabit(id = 2, title = "Correr", isArchived = true)),
        )
        HabitsViewModel(repository).state.test {
            val state = expectMostRecentItem()
            assertEquals(listOf(1L), state.active.map { it.id })
            assertEquals(listOf(2L), state.archived.map { it.id })
            assertEquals(HabitsTab.ACTIVE, state.tab)
        }
    }

    @Test
    fun c5_desarchivar_lo_vuelve_activo_y_muestra_activos() = runTest {
        val repository = FakeHabitRepository(listOf(sampleHabit(id = 2, title = "Correr", isArchived = true)))
        val vm = HabitsViewModel(repository)
        vm.state.test {
            vm.onTabSelected(HabitsTab.ARCHIVED)
            vm.onUnarchive(2)
            val state = expectMostRecentItem()
            assertEquals(listOf(2L), state.active.map { it.id })
            assertEquals(HabitsTab.ACTIVE, state.tab)
        }
    }

    @Test
    fun c1_no_desarchiva_si_hay_un_activo_con_el_mismo_titulo() = runTest {
        val repository = FakeHabitRepository(
            listOf(sampleHabit(id = 1, title = "Correr"), sampleHabit(id = 2, title = "correr", isArchived = true)),
        )
        val vm = HabitsViewModel(repository)
        vm.state.test {
            vm.onUnarchive(2)
            assertTrue(expectMostRecentItem().unarchiveBlocked)
            assertTrue(repository.saved.first { it.id == 2L }.isArchived)

            vm.onUnarchiveMessageShown()
            assertFalse(expectMostRecentItem().unarchiveBlocked)
        }
    }
}
