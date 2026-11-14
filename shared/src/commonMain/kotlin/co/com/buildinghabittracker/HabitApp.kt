package co.com.buildinghabittracker

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.rememberNavController
import co.com.buildinghabittracker.domain.auth.AppLock
import co.com.buildinghabittracker.ui.navigation.AppNavHost
import co.com.buildinghabittracker.ui.navigation.HabitsRoute
import co.com.buildinghabittracker.ui.navigation.PinRoute
import co.com.buildinghabittracker.ui.navigation.TodayRoute
import co.com.buildinghabittracker.ui.navigation.navigateClearingHistory
import co.com.buildinghabittracker.ui.testing.exposeTestTagsForUiTests
import co.com.buildinghabittracker.ui.theme.HabitTrackerTheme
import org.koin.compose.koinInject

/**
 * Raíz de la app: es lo primero que dibujan Android (MainActivity) e iOS (MainViewController).
 *
 * Aquí se aplican cuatro cosas que afectan a TODA la app:
 * 1. El tema claro u oscuro (regla G2).
 * 2. Los identificadores de prueba visibles para Maestro y Appium.
 * 3. La navegación entre pantallas.
 * 4. El bloqueo por tiempo en segundo plano (regla A5).
 */
@Composable
fun HabitApp() {
    HabitTrackerTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .exposeTestTagsForUiTests()
        ) {
            val navController = rememberNavController()
            val appLock: AppLock = koinInject()
            // Pantalla a la que se vuelve después del PIN (Hoy, salvo que se haya bloqueado en Hábitos).
            var resumeRoute: Any by remember { mutableStateOf(TodayRoute) }

            // A5: al ir a segundo plano se anota la hora; al volver, si pasaron 5 minutos, se pide el PIN.
            LifecycleEventEffect(Lifecycle.Event.ON_STOP) { appLock.onAppBackground() }
            LifecycleEventEffect(Lifecycle.Event.ON_START) {
                if (appLock.onAppForeground()) {
                    val current = navController.currentDestination
                    resumeRoute = if (current?.hasRoute(HabitsRoute::class) == true) HabitsRoute else TodayRoute
                    navController.navigateClearingHistory(PinRoute)
                }
            }

            AppNavHost(
                navController = navController,
                afterUnlock = { resumeRoute.also { resumeRoute = TodayRoute } },
            )
        }
    }
}
