package co.com.buildinghabittracker.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.nav_habits
import co.com.buildinghabittracker.resources.nav_settings
import co.com.buildinghabittracker.resources.nav_today
import co.com.buildinghabittracker.ui.testing.TestTags
import co.com.buildinghabittracker.ui.theme.ParkTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Las tres pestañas principales. Cada una sabe su ruta, su texto y su identificador. */
enum class MainTab(val route: Any, val label: StringResource, val testTag: String) {
    TODAY(TodayRoute, Res.string.nav_today, TestTags.NAV_TODAY),
    HABITS(HabitsRoute, Res.string.nav_habits, TestTags.NAV_HABITS),
    SETTINGS(SettingsRoute, Res.string.nav_settings, TestTags.NAV_SETTINGS),
}

/**
 * Espacio que deben dejar abajo las pantallas con barra, para que nada quede tapado.
 * Se suma al margen de la barra de navegación del sistema.
 */
val NavBarSpace = 100.dp

/**
 * Barra inferior del sistema "Mapa de parque": una cápsula de tinta flotante; la pestaña
 * activa va en una cápsula amarilla.
 */
@Composable
fun MainNavBar(selected: MainTab, onSelect: (MainTab) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(start = 12.dp, end = 12.dp, bottom = 16.dp)
            .fillMaxWidth()
            .height(64.dp)
            .clip(CircleShape)
            .background(ParkTheme.colors.ink)
            .padding(6.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MainTab.entries.forEach { tab ->
            val isSelected = tab == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 52.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) ParkTheme.colors.notice else Color.Transparent)
                    .selectable(selected = isSelected, role = Role.Tab) { onSelect(tab) }
                    .testTag(tab.testTag),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(tab.label),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) ParkTheme.colors.onAccent else ParkTheme.colors.water,
                )
            }
        }
    }
}
