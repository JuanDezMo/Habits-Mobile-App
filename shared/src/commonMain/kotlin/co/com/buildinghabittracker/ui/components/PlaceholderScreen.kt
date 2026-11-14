package co.com.buildinghabittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import co.com.buildinghabittracker.ui.theme.ParkTheme

/** Un botón de una pantalla provisional: su texto, su identificador de prueba y qué hace al tocarlo. */
data class PlaceholderAction(
    val label: String,
    val testTag: String,
    val onClick: () -> Unit,
)

/**
 * Pantalla provisional (Hoy y Detalle, hasta sus iteraciones): lóbulo de color con el título
 * y la explicación, y botones para navegar.
 *
 * @param lobeColor color del lóbulo de esa pantalla.
 * @param screenTestTag identificador de toda la pantalla; Maestro lo usa para saber dónde está.
 */
@Composable
fun PlaceholderScreen(
    title: String,
    description: String,
    screenTestTag: String,
    actions: List<PlaceholderAction>,
    lobeColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ParkTheme.colors.water)
            .navigationBarsPadding()
            .testTag(screenTestTag),
    ) {
        Lobe(color = lobeColor) {
            Text(title, style = MaterialTheme.typography.displaySmall, color = ParkTheme.colors.onAccent)
            Spacer(Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = ParkTheme.colors.onAccent)
        }
        Column(
            modifier = Modifier.padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            actions.forEach { action ->
                PillButton(
                    text = action.label,
                    onClick = action.onClick,
                    modifier = Modifier.testTag(action.testTag),
                )
            }
        }
    }
}
