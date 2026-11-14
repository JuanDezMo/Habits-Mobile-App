package co.com.buildinghabittracker.ui.pin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.pin_attempts_left
import co.com.buildinghabittracker.resources.pin_forgot
import co.com.buildinghabittracker.resources.pin_greeting
import co.com.buildinghabittracker.resources.pin_locked
import co.com.buildinghabittracker.resources.pin_subtitle
import co.com.buildinghabittracker.ui.auth.toMinutesSeconds
import co.com.buildinghabittracker.ui.components.PinPad
import co.com.buildinghabittracker.ui.testing.TestTags
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** Pantalla de PIN (con estado). Ver [PinContent] para el dibujo. */
@Composable
fun PinScreen(
    onUnlocked: () -> Unit,
    onForgotPin: () -> Unit,
    viewModel: PinViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isUnlocked) {
        if (state.isUnlocked) onUnlocked()
    }

    PinContent(
        state = state,
        onPinDigit = viewModel::onPinDigit,
        onPinDelete = viewModel::onPinDelete,
        onForgotPin = onForgotPin,
    )
}

/** Pantalla de PIN (sin estado): saludo, mensajes de error o bloqueo, y el teclado. */
@Composable
fun PinContent(
    state: PinUiState,
    onPinDigit: (Char) -> Unit,
    onPinDelete: () -> Unit,
    onForgotPin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp)
            .testTag(TestTags.PIN_SCREEN),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Text(stringResource(Res.string.pin_greeting, state.userName), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(Res.string.pin_subtitle), color = MaterialTheme.colorScheme.onSurfaceVariant)

        when {
            state.isLocked -> Text(
                stringResource(Res.string.pin_locked, state.lockRemaining.toMinutesSeconds()),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag(TestTags.PIN_LOCK_TEXT),
            )
            state.attemptsLeft != null -> Text(
                stringResource(Res.string.pin_attempts_left, state.attemptsLeft),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag(TestTags.PIN_ERROR_TEXT),
            )
        }

        PinPad(
            enteredDigits = state.pin.length,
            onDigit = onPinDigit,
            onDelete = onPinDelete,
            pad = TestTags.PinPad.LOGIN,
            enabled = !state.isLocked && !state.isVerifying,
        )
        TextButton(onClick = onForgotPin, modifier = Modifier.testTag(TestTags.PIN_FORGOT_BUTTON)) {
            Text(stringResource(Res.string.pin_forgot))
        }
    }
}
