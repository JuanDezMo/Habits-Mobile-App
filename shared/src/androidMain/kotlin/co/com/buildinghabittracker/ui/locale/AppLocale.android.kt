package co.com.buildinghabittracker.ui.locale

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/** Android: cambia el Locale por defecto y la configuración que leen los textos. */
actual object LocalAppLocale {
    // El idioma del teléfono al abrir la app, para poder volver a él.
    private var default: Locale? = null

    actual val current: String
        @Composable get() = Locale.getDefault().toString()

    @Composable
    @Suppress("DEPRECATION") // updateConfiguration es la forma que entiende Compose Resources.
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = Configuration(LocalConfiguration.current)
        if (default == null) default = Locale.getDefault()
        val locale = if (value == null) default!! else Locale.forLanguageTag(value)
        Locale.setDefault(locale)
        configuration.setLocale(locale)
        val resources = LocalContext.current.resources
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return LocalConfiguration.provides(configuration)
    }
}
