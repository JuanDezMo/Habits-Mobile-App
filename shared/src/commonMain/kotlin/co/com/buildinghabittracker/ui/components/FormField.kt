package co.com.buildinghabittracker.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.fillMaxWidth
import co.com.buildinghabittracker.domain.auth.FieldError
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.error_invalid_email
import co.com.buildinghabittracker.resources.error_required
import co.com.buildinghabittracker.resources.error_too_long
import co.com.buildinghabittracker.resources.error_too_short
import org.jetbrains.compose.resources.stringResource

/**
 * Campo de texto con etiqueta y mensaje de error traducido.
 *
 * @param error error de domain; aquí se convierte en el texto del idioma elegido.
 * @param minLength / maxLength se usan para armar mensajes como "Máximo 30 caracteres".
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
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = error != null,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        supportingText = error?.let { fieldError ->
            { Text(fieldErrorText(fieldError, minLength, maxLength), modifier = Modifier.testTag(errorTestTag)) }
        },
        modifier = modifier.fillMaxWidth().testTag(inputTestTag),
    )
}

/** Traduce un [FieldError] de domain al texto que ve el usuario. */
@Composable
fun fieldErrorText(error: FieldError, minLength: Int, maxLength: Int): String = when (error) {
    FieldError.REQUIRED -> stringResource(Res.string.error_required)
    FieldError.TOO_SHORT -> stringResource(Res.string.error_too_short, minLength)
    FieldError.TOO_LONG -> stringResource(Res.string.error_too_long, maxLength)
    FieldError.INVALID_EMAIL -> stringResource(Res.string.error_invalid_email)
}
