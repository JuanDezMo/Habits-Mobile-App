package co.com.buildinghabittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.com.buildinghabittracker.domain.auth.PinRules
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.pin_delete
import co.com.buildinghabittracker.resources.pin_dots_description
import co.com.buildinghabittracker.ui.testing.TestTags
import co.com.buildinghabittracker.ui.theme.ParkTheme
import org.jetbrains.compose.resources.stringResource

/**
 * Teclado numérico con los 4 puntos del PIN. Se reutiliza en Registro, PIN y Recuperar.
 *
 * Es un componente "tonto": no sabe qué significa el PIN. Solo muestra [enteredDigits]
 * puntos llenos y avisa cada tecla con [onDigit] u [onDelete]. La lógica está en los ViewModels.
 *
 * @param pad indica la pantalla, para que los identificadores de prueba no se repitan.
 * @param leftKey lo que va a la izquierda del 0 (por ejemplo "Olvidé mi PIN"); vacío si es `null`.
 */
@Composable
fun PinPad(
    enteredDigits: Int,
    onDigit: (Char) -> Unit,
    onDelete: () -> Unit,
    pad: TestTags.PinPad,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    leftKey: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        PinDots(enteredDigits = enteredDigits, pad = pad)
        Spacer(Modifier.height(6.dp))
        val rows = listOf(listOf('1', '2', '3'), listOf('4', '5', '6'), listOf('7', '8', '9'))
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(KEY_GAP)) {
                row.forEach { digit -> DigitKey(digit, onDigit, pad, enabled) }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(KEY_GAP), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(KEY_SIZE), contentAlignment = Alignment.Center) { leftKey?.invoke() }
            DigitKey('0', onDigit, pad, enabled)
            val deleteLabel = stringResource(Res.string.pin_delete)
            val canDelete = enabled && enteredDigits > 0
            Box(
                modifier = Modifier
                    .size(KEY_SIZE)
                    .clip(CircleShape)
                    .clickable(enabled = canDelete, role = Role.Button, onClick = onDelete)
                    .alpha(if (canDelete) 1f else 0.4f)
                    .testTag(pad.delete)
                    .semantics { contentDescription = deleteLabel },
                contentAlignment = Alignment.Center,
            ) {
                Text(deleteLabel, style = MaterialTheme.typography.labelMedium, color = ParkTheme.colors.ink)
            }
        }
    }
}

private val KEY_SIZE = 76.dp
private val KEY_GAP = 22.dp

@Composable
private fun DigitKey(digit: Char, onDigit: (Char) -> Unit, pad: TestTags.PinPad, enabled: Boolean) {
    Box(
        modifier = Modifier
            .size(KEY_SIZE)
            .clip(CircleShape)
            .background(ParkTheme.colors.field)
            .clickable(enabled = enabled, role = Role.Button) { onDigit(digit) }
            .alpha(if (enabled) 1f else 0.4f)
            .testTag(pad.key(digit.digitToInt())),
        contentAlignment = Alignment.Center,
    ) {
        Text(digit.toString(), style = MaterialTheme.typography.headlineMedium, color = ParkTheme.colors.ink, textAlign = TextAlign.Center)
    }
}

@Composable
private fun PinDots(enteredDigits: Int, pad: TestTags.PinPad) {
    val description = stringResource(Res.string.pin_dots_description, enteredDigits, PinRules.PIN_LENGTH)
    val ink = ParkTheme.colors.ink
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .testTag(pad.dots)
            .semantics { contentDescription = description },
    ) {
        repeat(PinRules.PIN_LENGTH) { index ->
            val filled = index < enteredDigits
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(2.dp, ink, CircleShape)
                    .then(if (filled) Modifier.background(ink, CircleShape) else Modifier),
            )
        }
    }
}
