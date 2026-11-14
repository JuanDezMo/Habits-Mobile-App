package co.com.buildinghabittracker.ui.recover

import androidx.compose.foundation.background
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
import co.com.buildinghabittracker.resources.pin_locked
import co.com.buildinghabittracker.resources.recover_answer_label
import co.com.buildinghabittracker.resources.recover_confirm_pin_title
import co.com.buildinghabittracker.resources.recover_new_pin_subtitle
import co.com.buildinghabittracker.resources.recover_new_pin_title
import co.com.buildinghabittracker.resources.recover_subtitle
import co.com.buildinghabittracker.resources.recover_title
import co.com.buildinghabittracker.resources.recover_verify
import co.com.buildinghabittracker.resources.recover_wrong_answer
import co.com.buildinghabittracker.resources.register_pin_mismatch
import co.com.buildinghabittracker.ui.auth.toMinutesSeconds
import co.com.buildinghabittracker.ui.components.BackPill
import co.com.buildinghabittracker.ui.components.FormField
import co.com.buildinghabittracker.ui.components.Lobe
import co.com.buildinghabittracker.ui.components.NoticePill
import co.com.buildinghabittracker.ui.components.PillButton
import co.com.buildinghabittracker.ui.components.PinPad
import co.com.buildinghabittracker.ui.testing.TestTags
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import co.com.buildinghabittracker.ui.theme.HabitPalette
import co.com.buildinghabittracker.ui.theme.ParkTheme

/** Pantalla "Olvidé mi PIN" (con estado). Ver [RecoverContent]. */
@Composable
fun RecoverScreen(
    onRecovered: () -> Unit,
    onBack: () -> Unit,
    viewModel: RecoverViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isRecovered) {
        if (state.isRecovered) onRecovered()
    }

    RecoverContent(
        state = state,
        onAnswerChange = viewModel::onAnswerChange,
        onVerify = viewModel::onVerifyAnswer,
        onPinDigit = viewModel::onPinDigit,
        onPinDelete = viewModel::onPinDelete,
        onBack = onBack,
    )
}

/** Recuperar PIN (sin estado): lóbulo lila; pregunta y respuesta, luego el PIN nuevo. */
@Composable
fun RecoverContent(
    state: RecoverUiState,
    onAnswerChange: (String) -> Unit,
    onVerify: () -> Unit,
    onPinDigit: (Char) -> Unit,
    onPinDelete: () -> Unit,
    onBack: () -> Unit,
) {
    val answering = state.step == RecoverStep.ANSWER
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ParkTheme.colors.water)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .imePadding()
            .testTag(TestTags.RECOVER_SCREEN),
    ) {
        Lobe(color = HabitPalette.lilac) {
            if (answering) {
                BackPill(
                    onClick = onBack,
                    containerColor = ParkTheme.colors.water,
                    modifier = Modifier.testTag(TestTags.RECOVER_BACK_BUTTON),
                )
                Spacer(Modifier.height(18.dp))
            }
            val title = when (state.step) {
                RecoverStep.ANSWER -> Res.string.recover_title
                RecoverStep.NEW_PIN -> Res.string.recover_new_pin_title
                else -> Res.string.recover_confirm_pin_title
            }
            Text(stringResource(title), style = MaterialTheme.typography.displaySmall, color = ParkTheme.colors.onAccent)
            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(if (answering) Res.string.recover_subtitle else Res.string.recover_new_pin_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = ParkTheme.colors.onAccent,
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (answering) {
                Text(
                    state.question,
                    style = MaterialTheme.typography.headlineSmall,
                    color = ParkTheme.colors.ink,
                    modifier = Modifier.testTag(TestTags.RECOVER_QUESTION_TEXT),
                )
                FormField(
                    value = state.answer,
                    onValueChange = onAnswerChange,
                    label = stringResource(Res.string.recover_answer_label),
                    error = null,
                    inputTestTag = TestTags.RECOVER_ANSWER_INPUT,
                    errorTestTag = TestTags.RECOVER_ERROR_TEXT,
                    enabled = !state.isLocked,
                )
                when {
                    state.isLocked -> NoticePill(
                        stringResource(Res.string.pin_locked, state.lockRemaining.toMinutesSeconds()),
                        modifier = Modifier.fillMaxWidth().testTag(TestTags.RECOVER_LOCK_TEXT),
                    )
                    state.attemptsLeft != null -> NoticePill(
                        stringResource(Res.string.recover_wrong_answer, state.attemptsLeft),
                        modifier = Modifier.fillMaxWidth().testTag(TestTags.RECOVER_ERROR_TEXT),
                    )
                }
                Spacer(Modifier.height(4.dp))
                PillButton(
                    text = stringResource(Res.string.recover_verify),
                    onClick = onVerify,
                    enabled = !state.isLocked && !state.isVerifying && state.answer.isNotBlank(),
                    modifier = Modifier.testTag(TestTags.RECOVER_VERIFY_BUTTON),
                )
            } else {
                if (state.pinMismatch) {
                    NoticePill(
                        stringResource(Res.string.register_pin_mismatch),
                        modifier = Modifier.fillMaxWidth().testTag(TestTags.RECOVER_PIN_MISMATCH_TEXT),
                    )
                }
                PinPad(
                    enteredDigits = state.pin.length,
                    onDigit = onPinDigit,
                    onDelete = onPinDelete,
                    pad = TestTags.PinPad.RECOVER,
                )
            }
        }
    }
}
