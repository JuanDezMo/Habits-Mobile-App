package co.com.buildinghabittracker.ui.habits

import androidx.compose.runtime.Composable
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.habits_description
import co.com.buildinghabittracker.resources.habits_open_example
import co.com.buildinghabittracker.resources.habits_title
import co.com.buildinghabittracker.ui.components.PlaceholderAction
import co.com.buildinghabittracker.ui.components.PlaceholderScreen
import co.com.buildinghabittracker.ui.testing.TestTags
import org.jetbrains.compose.resources.stringResource

/** Id de un hábito de ejemplo, solo para probar la navegación al Detalle en la iteración 0. */
private const val EXAMPLE_HABIT_ID = 1L

/** Pantalla Hábitos (provisional). En la iteración 2 tendrá la lista, crear, editar y archivar. */
@Composable
fun HabitsScreen(onOpenHabit: (habitId: Long) -> Unit) {
    PlaceholderScreen(
        title = stringResource(Res.string.habits_title),
        description = stringResource(Res.string.habits_description),
        screenTestTag = TestTags.HABITS_SCREEN,
        actions = listOf(
            PlaceholderAction(
                label = stringResource(Res.string.habits_open_example),
                testTag = TestTags.HABITS_OPEN_EXAMPLE_BUTTON,
                onClick = { onOpenHabit(EXAMPLE_HABIT_ID) },
            ),
        ),
    )
}
