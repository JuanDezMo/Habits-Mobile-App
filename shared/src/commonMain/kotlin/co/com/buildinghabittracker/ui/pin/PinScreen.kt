package co.com.buildinghabittracker.ui.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import co.com.buildinghabittracker.ui.components.Lobe
import co.com.buildinghabittracker.ui.components.NoticePill
import co.com.buildinghabittracker.ui.components.PinPad
import co.com.buildinghabittracker.ui.testing.TestTags
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import co.com.buildinghabittracker.ui.theme.HabitPalette
import co.com.buildinghabittracker.ui.theme.ParkTheme

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

/**
 * Pantalla de PIN (sin estado): lóbulo verde con el saludo, avisos de error o bloqueo en
 * cápsula amarilla, y el teclado con "Olvidé mi PIN" a la izquierda del 0.
 */
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
            .background(ParkTheme.colors.water)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .testTag(TestTags.PIN_SCREEN),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Lobe(color = ParkTheme.colors.land) {
            Text(
                stringResource(Res.string.pin_greeting, state.userName),
                style = MaterialTheme.typography.displaySmall,
                color = ParkTheme.colors.onAccent,
            )
            Spacer(Modifier.height(8.dp))
            Text(stringResource(Res.string.pin_subtitle), style = MaterialTheme.typography.bodyMedium, color = ParkTheme.colors.onAccent)
        }
        when {
            state.isLocked -> NoticePill(
                stringResource(Res.string.pin_locked, state.lockRemaining.toMinutesSeconds()),
                modifier = Modifier.padding(horizontal = 18.dp).testTag(TestTags.PIN_LOCK_TEXT),
            )
            state.attemptsLeft != null -> NoticePill(
                stringResource(Res.string.pin_attempts_left, state.attemptsLeft),
                modifier = Modifier.padding(horizontal = 18.dp).testTag(TestTags.PIN_ERROR_TEXT),
            )
        }
        Spacer(Modifier.height(18.dp))
        PinPad(
            enteredDigits = state.pin.length,
            onDigit = onPinDigit,
            onDelete = onPinDelete,
            pad = TestTags.PinPad.LOGIN,
            enabled = !state.isLocked && !state.isVerifying,
            leftKey = {
                Text(
                    stringResource(Res.string.pin_forgot),
                    style = MaterialTheme.typography.labelMedium,
                    color = ParkTheme.colors.ink,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickableForgot(onForgotPin)
                        .testTag(TestTags.PIN_FORGOT_BUTTON),
                )
            },
        )
        Spacer(Modifier.height(24.dp))
    }
}

/** El texto "Olvidé mi PIN" ocupa toda la tecla para que sea fácil de tocar (48 dp o más). */
private fun Modifier.clickableForgot(onClick: () -> Unit): Modifier =
    this.fillMaxSize()
        .clip(CircleShape)
        .clickable(role = Role.Button, onClick = onClick)
        .wrapContentSize()
