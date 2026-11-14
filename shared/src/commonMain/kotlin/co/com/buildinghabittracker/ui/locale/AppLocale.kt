package co.com.buildinghabittracker.ui.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

/**
 * Idioma elegido en Ajustes (regla G4), aplicado al instante y sin reiniciar la app.
 *
 * Los textos traducidos (Compose Multiplatform Resources) se eligen según el idioma de la
 * plataforma. Este objeto lo cambia desde la app, con `expect`/`actual`:
 * - Android: cambia la configuración de la Activity (LocalConfiguration).
 * - iOS: guarda el idioma en `AppleLanguages` de NSUserDefaults.
 *
 * Uso: `CompositionLocalProvider(LocalAppLocale provides "es") { ... }`.
 * Con `null` se vuelve al idioma del teléfono (G1).
 */
expect object LocalAppLocale {
    val current: String @Composable get

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}
