package co.com.buildinghabittracker.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.com.buildinghabittracker.domain.common.FieldError
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.error_duplicate_title
import co.com.buildinghabittracker.resources.error_invalid_email
import co.com.buildinghabittracker.resources.error_required
import co.com.buildinghabittracker.resources.error_too_long
import co.com.buildinghabittracker.resources.error_too_short
import co.com.buildinghabittracker.ui.theme.ParkTheme
import org.jetbrains.compose.resources.stringResource

/**
 * Campo de texto del sistema "Mapa de parque": fondo blanco redondeado, sin línea inferior.
 * Con error, el borde se pone del color de error y el mensaje aparece debajo.
 *
 * @param error error de domain; aquí se convierte en el texto del idioma elegido.
 * @param minLength / maxLength se usan para armar mensajes como "Máximo 30 caracteres".
 * @param singleLine `false` para textos largos, como la descripción de un hábito.
 */
@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: FieldError?,
    inputTestTag: String,
    errorTestTag: String,
    modifier: Modifier = Modifier,
    minLength: Int = 0,
    maxLength: Int = 0,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    enabled: Boolean = true,
) {
    val colors = ParkTheme.colors
    val shape = MaterialTheme.shapes.small
    Column(modifier = modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 2,
            enabled = enabled,
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = MaterialTheme.typography.titleLarge,
            shape = shape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.field,
                unfocusedContainerColor = colors.field,
                disabledContainerColor = colors.field,
                errorContainerColor = colors.field,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedLabelColor = colors.ink,
                unfocusedLabelColor = colors.inkSoft,
                errorLabelColor = colors.error,
                cursorColor = colors.ink,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (error != null) Modifier.border(2.dp, colors.error, shape) else Modifier)
                .testTag(inputTestTag),
        )
        if (error != null) {
            Text(
                fieldErrorText(error, minLength, maxLength),
                color = colors.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 18.dp, top = 6.dp).testTag(errorTestTag),
            )
        }
    }
}

/** Traduce un [FieldError] de domain al texto que ve el usuario. */
@Composable
fun fieldErrorText(error: FieldError, minLength: Int, maxLength: Int): String = when (error) {
    FieldError.REQUIRED -> stringResource(Res.string.error_required)
    FieldError.TOO_SHORT -> stringResource(Res.string.error_too_short, minLength)
    FieldError.TOO_LONG -> stringResource(Res.string.error_too_long, maxLength)
    FieldError.INVALID_EMAIL -> stringResource(Res.string.error_invalid_email)
    FieldError.DUPLICATE -> stringResource(Res.string.error_duplicate_title)
}
