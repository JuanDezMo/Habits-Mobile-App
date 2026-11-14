package co.com.buildinghabittracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import co.com.buildinghabittracker.ui.detail.DetailScreen
import co.com.buildinghabittracker.ui.habits.HabitFormScreen
import co.com.buildinghabittracker.ui.habits.HabitsScreen
import co.com.buildinghabittracker.ui.pin.PinScreen
import co.com.buildinghabittracker.ui.recover.RecoverScreen
import co.com.buildinghabittracker.ui.register.RegisterScreen
import co.com.buildinghabittracker.ui.start.StartScreen
import co.com.buildinghabittracker.ui.today.TodayScreen

/**
 * Mapa de navegación de la app.
 *
 * Las pantallas NO conocen al `navController`: reciben funciones como `onUnlocked` u `onBack`.
 * Así cada pantalla se puede probar sola, y toda la navegación queda en este archivo.
 *
 *   Arranque ─┬─► Registro ──► PIN ──► Hoy ──► Hábitos ──► Formulario (crear / editar)
 *             └─► PIN ──► Recuperar PIN ──► Hoy
 *
 * El Detalle existe pero todavía no tiene entrada: se conecta en la iteración 5.
 *
 * @param afterUnlock a dónde ir después del PIN: Hoy, o la pantalla donde estaba si la app
 * se bloqueó por estar 5 minutos en segundo plano (regla A5).
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    afterUnlock: () -> Any,
) {
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
            TodayScreen(onOpenHabits = { navController.navigate(HabitsRoute) })
        }
        composable<HabitsRoute> {
            HabitsScreen(
                onBack = { navController.popBackStack() },
                onAddHabit = { navController.navigate(HabitFormRoute()) },
                onEditHabit = { habitId -> navController.navigate(HabitFormRoute(habitId)) },
            )
        }
        composable<HabitFormRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<HabitFormRoute>()
            HabitFormScreen(
                habitId = route.habitId,
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
            )
        }
        composable<DetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<DetailRoute>()
            DetailScreen(habitId = route.habitId, onBack = { navController.popBackStack() })
        }
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
