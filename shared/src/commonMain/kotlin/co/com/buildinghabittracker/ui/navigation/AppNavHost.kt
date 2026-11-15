package co.com.buildinghabittracker.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import co.com.buildinghabittracker.ui.detail.DetailScreen
import co.com.buildinghabittracker.ui.habits.HabitFormScreen
import co.com.buildinghabittracker.ui.habits.HabitsScreen
import co.com.buildinghabittracker.ui.pin.PinScreen
import co.com.buildinghabittracker.ui.recover.RecoverScreen
import co.com.buildinghabittracker.ui.register.RegisterScreen
import co.com.buildinghabittracker.ui.settings.ChangePinScreen
import co.com.buildinghabittracker.ui.settings.SettingsScreen
import co.com.buildinghabittracker.ui.start.StartScreen
import co.com.buildinghabittracker.ui.today.TodayScreen

/** Clave con la que Cambiar PIN avisa a Ajustes que terminó bien. */
private const val PIN_CHANGED_KEY = "pin_changed"

/**
 * Mapa de navegación de la app.
 *
 * Las pantallas NO conocen al `navController`: reciben funciones como `onUnlocked` u `onBack`.
 * Así cada pantalla se puede probar sola, y toda la navegación queda en este archivo.
 *
 *   Arranque ─┬─► Registro ──► PIN ──► [Hoy | Hábitos | Ajustes]   ← barra inferior
 *             └─► PIN ──► Recuperar PIN ──► Hoy
 *   Hábitos ──► Formulario (crear / editar)      Ajustes ──► Cambiar PIN
 *
 * La barra inferior solo se ve en las tres pestañas principales ([MainTab]).
 * El Detalle existe pero todavía no tiene entrada: se conecta en la iteración 5.
 *
 * @param afterUnlock a dónde ir después del PIN: Hoy, o la pestaña donde estaba si la app
 * se bloqueó por estar 5 minutos en segundo plano (regla A5).
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    afterUnlock: () -> Any,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = MainTab.entries.firstOrNull { tab ->
        backStackEntry?.destination?.hasRoute(tab.route::class) == true
    }

    Box(Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = StartRoute) {
            composable<StartRoute> {
                StartScreen(
                    onNeedsRegister = { navController.navigateClearingHistory(RegisterRoute) },
                    onNeedsPin = { navController.navigateClearingHistory(PinRoute) },
                )
            }
            composable<RegisterRoute> {
                // A1: al terminar el registro se va a la pantalla de PIN.
                RegisterScreen(onRegistered = { navController.navigateClearingHistory(PinRoute) })
            }
            composable<PinRoute> {
                PinScreen(
                    onUnlocked = { navController.navigateClearingHistory(afterUnlock()) },
                    onForgotPin = { navController.navigate(RecoverRoute) },
                )
            }
            composable<RecoverRoute> {
                // A7: con el PIN nuevo, el usuario entra a Hoy.
                RecoverScreen(
                    onRecovered = { navController.navigateClearingHistory(TodayRoute) },
                    onBack = { navController.popBackStack() },
                )
            }
            composable<TodayRoute> {
                // Sin hábitos, Hoy ofrece "Crear mi primer hábito".
                TodayScreen(onCreateHabit = { navController.navigate(HabitFormRoute()) })
            }
            composable<HabitsRoute> {
                HabitsScreen(
                    onAddHabit = { navController.navigate(HabitFormRoute()) },
                    onEditHabit = { habitId -> navController.navigate(HabitFormRoute(habitId)) },
                )
            }
            composable<HabitFormRoute> { entry ->
                val route = entry.toRoute<HabitFormRoute>()
                HabitFormScreen(
                    habitId = route.habitId,
                    onDone = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                )
            }
            composable<SettingsRoute> { entry ->
                // Cambiar PIN deja un "recado" en esta pantalla al terminar bien.
                val pinChanged by entry.savedStateHandle.getStateFlow(PIN_CHANGED_KEY, false).collectAsState()
                SettingsScreen(
                    pinChanged = pinChanged,
                    onPinChangedShown = { entry.savedStateHandle[PIN_CHANGED_KEY] = false },
                    onChangePin = { navController.navigate(ChangePinRoute) },
                )
            }
            composable<ChangePinRoute> {
                ChangePinScreen(
                    onChanged = {
                        navController.previousBackStackEntry?.savedStateHandle?.set(PIN_CHANGED_KEY, true)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable<DetailRoute> { entry ->
                val route = entry.toRoute<DetailRoute>()
                DetailScreen(habitId = route.habitId, onBack = { navController.popBackStack() })
            }
        }

        if (currentTab != null) {
            MainNavBar(
                selected = currentTab,
                onSelect = { tab -> navController.navigateToTab(tab) },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

/**
 * Cambia de pestaña sin apilar pantallas: Hoy queda siempre en la base, y cada pestaña
 * recuerda dónde estaba (por ejemplo, la pestaña Archivados de Hábitos).
 */
private fun NavHostController.navigateToTab(tab: MainTab) {
    navigate(tab.route) {
        popUpTo(TodayRoute) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Navega y borra el historial: "Atrás" no puede volver a la pantalla anterior.
 * Se usa en los puntos de seguridad (después del registro, del PIN y de la recuperación).
 */
fun NavHostController.navigateClearingHistory(route: Any) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
    }
}
