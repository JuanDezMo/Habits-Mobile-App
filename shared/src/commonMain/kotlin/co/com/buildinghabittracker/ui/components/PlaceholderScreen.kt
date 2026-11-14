package co.com.buildinghabittracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/** Un botón de una pantalla provisional: su texto, su identificador de prueba y qué hace al tocarlo. */
data class PlaceholderAction(
    val label: String,
    val testTag: String,
    val onClick: () -> Unit,
)

/**
 * Pantalla provisional de la iteración 0: título, explicación y botones para navegar.
 * Cada pantalla real la reemplaza en su iteración.
 *
 * @param screenTestTag identificador de toda la pantalla; Maestro lo usa para saber dónde está.
 */
@Composable
fun PlaceholderScreen(
    title: String,
    description: String,
    screenTestTag: String,
    actions: List<PlaceholderAction>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp)
            .testTag(screenTestTag),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        actions.forEach { action ->
            Button(
                onClick = action.onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(action.testTag),
            ) {
                Text(action.label)
            }
        }
    }
}
