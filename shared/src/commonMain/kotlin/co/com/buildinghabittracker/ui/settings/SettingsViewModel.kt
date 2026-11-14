package co.com.buildinghabittracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.buildinghabittracker.domain.settings.AppLanguage
import co.com.buildinghabittracker.domain.settings.AppTheme
import co.com.buildinghabittracker.domain.settings.DeviceLanguage
import co.com.buildinghabittracker.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado de Ajustes.
 *
 * @property language el idioma que se está usando: el elegido o, si no hay, el del teléfono (G1).
 */
data class SettingsUiState(
    val language: AppLanguage = AppLanguage.ENGLISH,
    val theme: AppTheme = AppTheme.SYSTEM,
)

/**
 * ViewModel de Ajustes (regla G4): idioma y tema.
 * Guardar es suficiente: la raíz de la app observa las preferencias y cambia al instante.
 */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val deviceLanguage: DeviceLanguage,
) : ViewModel() {

    val state: StateFlow<SettingsUiState> = settingsRepository.settings
        .map { settings ->
            SettingsUiState(
                language = settings.language ?: AppLanguage.fromDeviceLanguage(deviceLanguage.code()),
                theme = settings.theme,
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SettingsUiState(language = AppLanguage.fromDeviceLanguage(deviceLanguage.code())),
        )

    fun onLanguageSelected(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }

    fun onThemeSelected(theme: AppTheme) {
        viewModelScope.launch { settingsRepository.setTheme(theme) }
    }
}
