package co.com.buildinghabittracker.ui.recover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.common_back
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
import co.com.buildinghabittracker.ui.components.PinPad
import co.com.buildinghabittracker.ui.testing.TestTags
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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

@Composable
fun RecoverContent(
    state: RecoverUiState,
    onAnswerChange: (String) -> Unit,
    onVerify: () -> Unit,
    onPinDigit: (Char) -> Unit,
    onPinDelete: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag(TestTags.RECOVER_SCREEN),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (state.step == RecoverStep.ANSWER) {
            TextButton(onClick = onBack, modifier = Modifier.testTag(TestTags.RECOVER_BACK_BUTTON)) {
                Text("← " + stringResource(Res.string.common_back))
            }
            Text(stringResource(Res.string.recover_title), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(Res.string.recover_subtitle), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                state.question,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag(TestTags.RECOVER_QUESTION_TEXT),
            )
            OutlinedTextField(
                value = state.answer,
                onValueChange = onAnswerChange,
                label = { Text(stringResource(Res.string.recover_answer_label)) },
                singleLine = true,
                enabled = !state.isLocked,
                modifier = Modifier.fillMaxWidth().testTag(TestTags.RECOVER_ANSWER_INPUT),
            )
            when {
                state.isLocked -> Text(
                    stringResource(Res.string.pin_locked, state.lockRemaining.toMinutesSeconds()),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag(TestTags.RECOVER_LOCK_TEXT),
                )
                state.attemptsLeft != null -> Text(
                    stringResource(Res.string.recover_wrong_answer, state.attemptsLeft),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag(TestTags.RECOVER_ERROR_TEXT),
                )
            }
            Button(
                onClick = onVerify,
                enabled = !state.isLocked && !state.isVerifying && state.answer.isNotBlank(),
                modifier = Modifier.fillMaxWidth().testTag(TestTags.RECOVER_VERIFY_BUTTON),
            ) {
                Text(stringResource(Res.string.recover_verify))
            }
        } else {
            val title = if (state.step == RecoverStep.NEW_PIN) Res.string.recover_new_pin_title else Res.string.recover_confirm_pin_title
            Text(stringResource(title), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(Res.string.recover_new_pin_subtitle), color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (state.pinMismatch) {
                Text(
                    stringResource(Res.string.register_pin_mismatch),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag(TestTags.RECOVER_PIN_MISMATCH_TEXT),
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
