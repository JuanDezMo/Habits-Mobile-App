package co.com.buildinghabittracker.ui.detail

import androidx.compose.runtime.Composable
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.common_back
import co.com.buildinghabittracker.resources.detail_description
import co.com.buildinghabittracker.resources.detail_title
import co.com.buildinghabittracker.ui.components.PlaceholderAction
import co.com.buildinghabittracker.ui.components.PlaceholderScreen
import co.com.buildinghabittracker.ui.testing.TestTags
import co.com.buildinghabittracker.ui.theme.HabitPalette
import org.jetbrains.compose.resources.stringResource

/**
 * Detalle de un hábito (provisional). En la iteración 5 mostrará racha, cumplimiento y calendario.
 *
 * @param habitId el hábito a mostrar; llega desde la ruta [co.com.buildinghabittracker.ui.navigation.DetailRoute].
 */
@Composable
fun DetailScreen(habitId: Long, onBack: () -> Unit) {
    PlaceholderScreen(
        // `detail_title` tiene un parámetro (%1$d) que se reemplaza por el id.
        title = stringResource(Res.string.detail_title, habitId),
        description = stringResource(Res.string.detail_description),
        screenTestTag = TestTags.DETAIL_SCREEN,
        lobeColor = HabitPalette.coral,
        actions = listOf(
            PlaceholderAction(stringResource(Res.string.common_back), TestTags.DETAIL_BACK_BUTTON, onBack),
        ),
    )
}
