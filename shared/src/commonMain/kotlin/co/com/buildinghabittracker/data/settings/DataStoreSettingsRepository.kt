package co.com.buildinghabittracker.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import co.com.buildinghabittracker.domain.settings.AppLanguage
import co.com.buildinghabittracker.domain.settings.AppSettings
import co.com.buildinghabittracker.domain.settings.AppTheme
import co.com.buildinghabittracker.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

/** Nombre del archivo de preferencias en el teléfono. */
const val SETTINGS_FILE_NAME = "settings.preferences_pb"

/**
 * Crea el DataStore de preferencias. Cada plataforma entrega la RUTA del archivo
 * (Android: la carpeta privada de la app; iOS: Documents). Debe existir uno solo por app.
 */
fun createSettingsDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })

/**
 * Implementación de [SettingsRepository] con DataStore Preferences: guarda pares clave-valor
 * en un archivo. Aquí se guardan los nombres de los enums, por ejemplo "SPANISH" o "DARK".
 */
class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    private val languageKey = stringPreferencesKey("language")
    private val themeKey = stringPreferencesKey("theme")

    override val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            language = prefs[languageKey]?.let { saved -> AppLanguage.entries.firstOrNull { it.name == saved } },
            theme = prefs[themeKey]?.let { saved -> AppTheme.entries.firstOrNull { it.name == saved } } ?: AppTheme.SYSTEM,
        )
    }

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { it[languageKey] = language.name }
    }

    override suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { it[themeKey] = theme.name }
    }
}
