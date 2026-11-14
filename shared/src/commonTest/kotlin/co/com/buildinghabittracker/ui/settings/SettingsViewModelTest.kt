package co.com.buildinghabittracker.ui.settings

import app.cash.turbine.test
import co.com.buildinghabittracker.domain.settings.AppLanguage
import co.com.buildinghabittracker.domain.settings.AppSettings
import co.com.buildinghabittracker.domain.settings.AppTheme
import co.com.buildinghabittracker.domain.settings.DeviceLanguage
import co.com.buildinghabittracker.fakes.FakeSettingsRepository
import co.com.buildinghabittracker.ui.MainDispatcherTest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Pruebas de Ajustes (G1, G4). */
class SettingsViewModelTest : MainDispatcherTest() {

    @Test
    fun g1_sin_eleccion_muestra_el_idioma_del_telefono() = runTest {
        val vm = SettingsViewModel(FakeSettingsRepository(), DeviceLanguage { "es-CO" })
        vm.state.test {
            assertEquals(AppLanguage.SPANISH, expectMostRecentItem().language)
        }
    }

    @Test
    fun g4_elegir_idioma_y_tema_los_guarda() = runTest {
        val repository = FakeSettingsRepository()
        val vm = SettingsViewModel(repository, DeviceLanguage { "es" })
        vm.state.test {
            vm.onLanguageSelected(AppLanguage.ENGLISH)
            vm.onThemeSelected(AppTheme.DARK)
            val state = expectMostRecentItem()
            assertEquals(AppLanguage.ENGLISH, state.language)
            assertEquals(AppTheme.DARK, state.theme)
        }
        assertEquals(AppSettings(AppLanguage.ENGLISH, AppTheme.DARK), repository.current)
    }
}
