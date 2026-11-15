package co.com.buildinghabittracker.ui.today

import app.cash.turbine.test
import co.com.buildinghabittracker.domain.checkin.CheckIn
import co.com.buildinghabittracker.fakes.FakeCheckInRepository
import co.com.buildinghabittracker.fakes.FakeClock
import co.com.buildinghabittracker.fakes.FakeHabitRepository
import co.com.buildinghabittracker.fakes.FakeTodayProvider
import co.com.buildinghabittracker.fakes.FakeUserRepository
import co.com.buildinghabittracker.fakes.sampleHabit
import co.com.buildinghabittracker.fakes.sampleUser
import co.com.buildinghabittracker.ui.MainDispatcherTest
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

/**
 * Pruebas de Hoy (D1–D3, E1, E2).
 * Hoy es el viernes 13 de noviembre de 2026; la zona horaria es UTC para que las horas sean exactas.
 */
class TodayViewModelTest : MainDispatcherTest() {
    private val today = LocalDate(2026, 11, 13)
    private val yesterday = LocalDate(2026, 11, 12)
    private val clock = FakeClock(Instant.parse("2026-11-13T20:00:00Z"))

    private val gym = sampleHabit(id = 1, title = "Gym") // lunes, miércoles, viernes
    private val leer = sampleHabit(id = 2, title = "Leer", days = DayOfWeek.entries.toSet())
    private val yoga = sampleHabit(id = 3, title = "Yoga", days = setOf(DayOfWeek.TUESDAY))

    private fun viewModel(
        habits: FakeHabitRepository = FakeHabitRepository(listOf(gym, leer, yoga)),
        checkIns: FakeCheckInRepository = FakeCheckInRepository(),
        todayProvider: FakeTodayProvider = FakeTodayProvider(today),
    ) = TodayViewModel(habits, checkIns, FakeUserRepository(sampleUser()), todayProvider, clock, TimeZone.UTC)

    @Test
    fun muestra_solo_los_habitos_que_tocan_hoy_en_orden_de_creacion() = runTest {
        viewModel().state.test {
            val state = expectMostRecentItem()
            assertEquals("Ana", state.userName)
            assertEquals(today, state.date)
            // Yoga (martes) no toca hoy y no se muestra.
            assertEquals(listOf(1L, 2L), state.today.map { it.habit.id })
            assertEquals(0, state.doneCount)
        }
    }

    @Test
    fun marcar_guarda_el_check_in_y_sube_la_racha() = runTest {
        val checkIns = FakeCheckInRepository()
        val vm = viewModel(checkIns = checkIns)
        vm.state.test {
            val leerItem = expectMostRecentItem().today.first { it.habit.id == 2L }
            vm.onToggle(leerItem)
            val state = expectMostRecentItem()
            val updated = state.today.first { it.habit.id == 2L }
            assertTrue(updated.isDone)
            assertEquals(1, updated.streak)
            assertEquals(1, state.doneCount)
            assertEquals(listOf(CheckIn(2, today, clock.now())), checkIns.saved)
        }
    }

    @Test
    fun d3_desmarcar_quita_el_check_in_y_ofrece_deshacer() = runTest {
        val checkIns = FakeCheckInRepository(listOf(CheckIn(2, today, clock.now())))
        val vm = viewModel(checkIns = checkIns)
        vm.state.test {
            vm.onToggle(expectMostRecentItem().today.first { it.habit.id == 2L })
            val state = expectMostRecentItem()
            assertFalse(state.today.first { it.habit.id == 2L }.isDone)
            val undo = assertNotNull(state.undo)
            assertEquals("Leer", undo.title)
            assertTrue(checkIns.saved.isEmpty())

            vm.onUndo(undo)
            val restored = expectMostRecentItem()
            assertTrue(restored.today.first { it.habit.id == 2L }.isDone)
            assertNull(restored.undo)
        }
    }

    @Test
    fun cerrar_el_aviso_sin_deshacer_lo_quita() = runTest {
        val vm = viewModel(checkIns = FakeCheckInRepository(listOf(CheckIn(2, today, clock.now()))))
        vm.state.test {
            vm.onToggle(expectMostRecentItem().today.first { it.habit.id == 2L })
            val undo = assertNotNull(expectMostRecentItem().undo)
            vm.onUndoDismissed(undo)
            assertNull(expectMostRecentItem().undo)
        }
    }

    @Test
    fun ayer_muestra_los_que_tocaban_ayer_sin_marcar() = runTest {
        viewModel().state.test {
            // Ayer fue jueves: solo Leer (diario) tocaba. Gym (L-M-V) no.
            val yesterdayItems = expectMostRecentItem().yesterday
            assertEquals(listOf(2L), yesterdayItems.map { it.habit.id })
            assertEquals(yesterday, yesterdayItems.first().date)
            assertFalse(yesterdayItems.first().isDone)
        }
    }

    @Test
    fun ayer_marcado_hoy_se_queda_con_check_hasta_medianoche() = runTest {
        val vm = viewModel()
        vm.state.test {
            vm.onToggle(expectMostRecentItem().yesterday.first())
            val item = expectMostRecentItem().yesterday.single()
            assertTrue(item.isDone)
        }
    }

    @Test
    fun ayer_marcado_desde_ayer_no_aparece() = runTest {
        val markedYesterday = CheckIn(2, yesterday, Instant.parse("2026-11-12T21:00:00Z"))
        viewModel(checkIns = FakeCheckInRepository(listOf(markedYesterday))).state.test {
            assertTrue(expectMostRecentItem().yesterday.isEmpty())
        }
    }

    @Test
    fun a_medianoche_cambia_el_dia() = runTest {
        val todayProvider = FakeTodayProvider(today)
        viewModel(todayProvider = todayProvider).state.test {
            assertEquals(listOf(1L, 2L), expectMostRecentItem().today.map { it.habit.id })
            // Sábado 14: ni Gym ni Yoga tocan; solo Leer.
            todayProvider.today.value = LocalDate(2026, 11, 14)
            val state = expectMostRecentItem()
            assertEquals(LocalDate(2026, 11, 14), state.date)
            assertEquals(listOf(2L), state.today.map { it.habit.id })
        }
    }

    @Test
    fun d1_no_marca_un_dia_que_no_es_hoy_ni_ayer() = runTest {
        val checkIns = FakeCheckInRepository()
        val vm = viewModel(checkIns = checkIns)
        vm.state.test {
            expectMostRecentItem()
            vm.onToggle(TodayHabit(leer, LocalDate(2026, 11, 10), isDone = false))
            assertTrue(checkIns.saved.isEmpty())
        }
    }

    @Test
    fun los_archivados_no_aparecen_y_sin_activos_se_ofrece_crear() = runTest {
        val habits = FakeHabitRepository(listOf(gym.copy(isArchived = true)))
        viewModel(habits = habits).state.test {
            val state = expectMostRecentItem()
            assertFalse(state.hasHabits)
            assertTrue(state.today.isEmpty())
            assertTrue(state.yesterday.isEmpty())
        }
    }
}
