package co.com.buildinghabittracker.ui.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

/** iOS: guarda el idioma en "AppleLanguages", que es lo que leen los textos traducidos. */
actual object LocalAppLocale {
    private const val LANGUAGES_KEY = "AppleLanguages"
    private val default: String = (NSLocale.preferredLanguages.firstOrNull() as? String) ?: "en"
    private val local = staticCompositionLocalOf { default }

    actual val current: String
        @Composable get() = local.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val defaults = NSUserDefaults.standardUserDefaults
        if (value == null) {
            defaults.removeObjectForKey(LANGUAGES_KEY)
        } else {
            defaults.setObject(listOf(value), LANGUAGES_KEY)
        }
        return local.provides(value ?: default)
    }
}
