package co.com.buildinghabittracker.domain.settings

import kotlinx.coroutines.flow.Flow

/** Idiomas de la app (reglas G1 y G4). [code] es el código que usan los textos traducidos. */
enum class AppLanguage(val code: String) {
    SPANISH("es"),
    ENGLISH("en");

    companion object {
        /** G1: la primera vez se usa el idioma del teléfono; si no es español ni inglés, inglés. */
        fun fromDeviceLanguage(code: String): AppLanguage =
            if (code.lowercase().startsWith("es")) SPANISH else ENGLISH
    }
}

/** Tema de la app (reglas G2 y G4). Por defecto sigue al teléfono. */
enum class AppTheme { SYSTEM, LIGHT, DARK }

/**
 * Preferencias guardadas en el teléfono.
 *
 * @property language `null` mientras el usuario no haya elegido: se usa el idioma del teléfono (G1).
 */
data class AppSettings(
    val language: AppLanguage? = null,
    val theme: AppTheme = AppTheme.SYSTEM,
)

/**
 * Contrato para leer y guardar las preferencias (G4). Lo implementa DataStore en la capa data.
 * Existen antes del registro, por eso no van en Room.
 */
interface SettingsRepository {
    /** Las preferencias actuales; emite de nuevo cada vez que cambian. */
    val settings: Flow<AppSettings>

    suspend fun setLanguage(language: AppLanguage)

    suspend fun setTheme(theme: AppTheme)
}

/** El idioma del teléfono. Cada plataforma lo lee a su manera (ver DeviceLanguage en data). */
fun interface DeviceLanguage {
    /** Código de idioma del teléfono, por ejemplo "es" o "fr". */
    fun code(): String
}
