package co.com.buildinghabittracker

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.rememberNavController
import co.com.buildinghabittracker.domain.auth.AppLock
import co.com.buildinghabittracker.domain.settings.AppSettings
import co.com.buildinghabittracker.domain.settings.AppTheme
import co.com.buildinghabittracker.domain.settings.SettingsRepository
import co.com.buildinghabittracker.ui.locale.LocalAppLocale
import co.com.buildinghabittracker.ui.navigation.AppNavHost
import co.com.buildinghabittracker.ui.navigation.ChangePinRoute
import co.com.buildinghabittracker.ui.navigation.HabitFormRoute
import co.com.buildinghabittracker.ui.navigation.HabitsRoute
import co.com.buildinghabittracker.ui.navigation.PinRoute
import co.com.buildinghabittracker.ui.navigation.SettingsRoute
import co.com.buildinghabittracker.ui.navigation.TodayRoute
import co.com.buildinghabittracker.ui.navigation.navigateClearingHistory
import co.com.buildinghabittracker.ui.testing.exposeTestTagsForUiTests
import co.com.buildinghabittracker.ui.theme.HabitTrackerTheme
import co.com.buildinghabittracker.ui.theme.LightParkColors
import org.koin.compose.koinInject

/**
 * Raíz de la app: es lo primero que dibujan Android (MainActivity) e iOS (MainViewController).
 *
 * Aquí se aplican cinco cosas que afectan a TODA la app:
 * 1. El tema claro u oscuro elegido en Ajustes (G2, G4).
 * 2. El idioma elegido en Ajustes, al instante (G1, G4).
 * 3. Los identificadores de prueba visibles para Maestro y Appium.
 * 4. La navegación entre pantallas.
 * 5. El bloqueo por tiempo en segundo plano (regla A5).
 */
@Composable
fun HabitApp() {
    val settingsRepository: SettingsRepository = koinInject()
    // `null` mientras DataStore lee el archivo (milisegundos): así no parpadea otro idioma o tema.
    val settings: AppSettings? by settingsRepository.settings.collectAsState(initial = null)
    val loaded = settings
    if (loaded == null) {
        Box(Modifier.fillMaxSize().background(LightParkColors.water))
        return
    }

    val darkTheme = when (loaded.theme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }
    val languageCode = loaded.language?.code

    CompositionLocalProvider(LocalAppLocale provides languageCode) {
        HabitTrackerTheme(darkTheme = darkTheme) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .exposeTestTagsForUiTests()
            ) {
                // El navController vive FUERA del `key`: al cambiar de idioma se redibujan los
                // textos, pero no se pierde en qué pantalla está el usuario.
                val navController = rememberNavController()
                val appLock: AppLock = koinInject()
                // Pantalla a la que se vuelve después del PIN (A5): la pestaña donde estaba.
                // Lo que no se guardó en un formulario se pierde, igual que al cerrar la app.
                var resumeRoute: Any by remember { mutableStateOf(TodayRoute) }

                // A5: al ir a segundo plano se anota la hora; al volver, si pasaron 5 minutos, se pide el PIN.
                LifecycleEventEffect(Lifecycle.Event.ON_STOP) { appLock.onAppBackground() }
                LifecycleEventEffect(Lifecycle.Event.ON_START) {
                    if (appLock.onAppForeground()) {
                        val current = navController.currentDestination
                        resumeRoute = when {
                            current?.hasRoute(HabitsRoute::class) == true ||
                                current?.hasRoute(HabitFormRoute::class) == true -> HabitsRoute
                            current?.hasRoute(SettingsRoute::class) == true ||
                                current?.hasRoute(ChangePinRoute::class) == true -> SettingsRoute
                            else -> TodayRoute
                        }
                        navController.navigateClearingHistory(PinRoute)
                    }
                }

                // Los textos traducidos se eligen al componer: `key` obliga a componer de nuevo
                // cuando cambia el idioma.
                key(languageCode) {
                    AppNavHost(
                        navController = navController,
                        afterUnlock = { resumeRoute.also { resumeRoute = TodayRoute } },
                    )
                }
            }
        }
    }
}
