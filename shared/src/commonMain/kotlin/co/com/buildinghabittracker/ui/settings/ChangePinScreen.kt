package co.com.buildinghabittracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.changepin_confirm_title
import co.com.buildinghabittracker.resources.changepin_current_subtitle
import co.com.buildinghabittracker.resources.changepin_current_title
import co.com.buildinghabittracker.resources.changepin_new_subtitle
import co.com.buildinghabittracker.resources.changepin_new_title
import co.com.buildinghabittracker.resources.pin_attempts_left
import co.com.buildinghabittracker.resources.pin_locked
import co.com.buildinghabittracker.resources.register_pin_mismatch
import co.com.buildinghabittracker.ui.auth.toMinutesSeconds
import co.com.buildinghabittracker.ui.components.BackPill
import co.com.buildinghabittracker.ui.components.Lobe
import co.com.buildinghabittracker.ui.components.NoticePill
import co.com.buildinghabittracker.ui.components.PinPad
import co.com.buildinghabittracker.ui.testing.TestTags
import co.com.buildinghabittracker.ui.theme.HabitPalette
import co.com.buildinghabittracker.ui.theme.ParkTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Cambiar PIN (con estado), desde Ajustes (A8).
 *
 * @param onChanged se llama cuando el PIN nuevo quedó guardado: se vuelve a Ajustes con aviso.
 */
@Composable
fun ChangePinScreen(
    onChanged: () -> Unit,
    onBack: () -> Unit,
    viewModel: ChangePinViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.isChanged) {
        if (state.isChanged) onChanged()
    }
    ChangePinContent(state, viewModel::onPinDigit, viewModel::onPinDelete, onBack)
}

/** Cambiar PIN (sin estado). Diseño: lóbulo lila con el paso; avisos en cápsula; teclado. */
@Composable
fun ChangePinContent(
    state: ChangePinUiState,
    onPinDigit: (Char) -> Unit,
    onPinDelete: () -> Unit,
    onBack: () -> Unit,
) {
    val (title, subtitle) = when (state.step) {
        ChangePinStep.CURRENT -> Res.string.changepin_current_title to Res.string.changepin_current_subtitle
        ChangePinStep.NEW -> Res.string.changepin_new_title to Res.string.changepin_new_subtitle
        ChangePinStep.CONFIRM -> Res.string.changepin_confirm_title to Res.string.changepin_new_subtitle
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ParkTheme.colors.water)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .testTag(TestTags.CHANGEPIN_SCREEN),
    ) {
        Lobe(color = HabitPalette.lilac) {
            BackPill(
                onClick = onBack,
                containerColor = ParkTheme.colors.water,
                modifier = Modifier.testTag(TestTags.CHANGEPIN_BACK_BUTTON),
            )
            Spacer(Modifier.height(18.dp))
            Text(stringResource(title), style = MaterialTheme.typography.displaySmall, color = ParkTheme.colors.onAccent)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(subtitle), style = MaterialTheme.typography.bodyMedium, color = ParkTheme.colors.onAccent)
        }
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when {
                state.isLocked -> NoticePill(
                    stringResource(Res.string.pin_locked, state.lockRemaining.toMinutesSeconds()),
                    modifier = Modifier.fillMaxWidth().testTag(TestTags.CHANGEPIN_LOCK_TEXT),
                )
                state.attemptsLeft != null && state.step == ChangePinStep.CURRENT -> NoticePill(
                    stringResource(Res.string.pin_attempts_left, state.attemptsLeft),
                    modifier = Modifier.fillMaxWidth().testTag(TestTags.CHANGEPIN_ERROR_TEXT),
                )
                state.pinMismatch -> NoticePill(
                    stringResource(Res.string.register_pin_mismatch),
                    modifier = Modifier.fillMaxWidth().testTag(TestTags.CHANGEPIN_MISMATCH_TEXT),
                )
            }
            PinPad(
                enteredDigits = state.pin.length,
                onDigit = onPinDigit,
                onDelete = onPinDelete,
                pad = TestTags.PinPad.CHANGE,
                enabled = !state.isLocked && !state.isVerifying,
            )
        }
    }
}
