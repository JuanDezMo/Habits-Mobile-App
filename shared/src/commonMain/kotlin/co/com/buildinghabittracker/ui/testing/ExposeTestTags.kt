package co.com.buildinghabittracker.ui.testing

import androidx.compose.ui.Modifier

/**
 * Hace visibles los `testTag` para herramientas externas como Maestro y Appium.
 *
 * Ejemplo de `expect`/`actual`: aquí solo se DECLARA la función (`expect`) y cada plataforma
 * escribe su propia versión (`actual`):
 * - androidMain: activa `testTagsAsResourceId`; sin esto Android no expone los identificadores.
 * - iosMain: no hace nada, porque iOS ya los expone como `accessibilityIdentifier`.
 *
 * Se aplica en la raíz de la app ([co.com.buildinghabittracker.HabitApp]) y, además, en cada
 * diálogo: un diálogo abre su propia ventana y no hereda lo de la raíz.
 * Está activo en todas las versiones (debug y release).
 */
expect fun Modifier.exposeTestTagsForUiTests(): Modifier
