package co.com.buildinghabittracker.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Rutas de navegación (Navigation Compose con rutas tipadas).
 *
 * Cada pantalla es un tipo. Si la pantalla necesita datos, la ruta es una `data class`
 * con esos datos (ver [DetailRoute]); si no, es un `object`.
 * `@Serializable` permite que Navigation guarde y restaure la ruta.
 */
@Serializable
object StartRoute

@Serializable
object RegisterRoute

@Serializable
object PinRoute

@Serializable
object RecoverRoute

@Serializable
object TodayRoute

@Serializable
object HabitsRoute

/** Formulario de hábito. `habitId = 0` crea uno nuevo; otro valor edita ese hábito. */
@Serializable
data class HabitFormRoute(val habitId: Long = 0L)

/** Detalle de un hábito: necesita saber cuál, por eso lleva su id. */
@Serializable
data class DetailRoute(val habitId: Long)
