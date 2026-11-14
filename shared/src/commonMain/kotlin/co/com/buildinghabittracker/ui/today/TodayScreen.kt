package co.com.buildinghabittracker.ui.today

import androidx.compose.runtime.Composable
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.today_description
import co.com.buildinghabittracker.resources.today_title
import co.com.buildinghabittracker.ui.components.PlaceholderScreen
import co.com.buildinghabittracker.ui.testing.TestTags
import co.com.buildinghabittracker.ui.theme.ParkTheme
import org.jetbrains.compose.resources.stringResource

/** Pantalla Hoy (provisional). En la iteración 4 mostrará los hábitos del día y el check-in. */
@Composable
fun TodayScreen() {
    PlaceholderScreen(
        title = stringResource(Res.string.today_title),
        description = stringResource(Res.string.today_description),
        screenTestTag = TestTags.TODAY_SCREEN,
        lobeColor = ParkTheme.colors.land,
        actions = emptyList(),
    )
}
