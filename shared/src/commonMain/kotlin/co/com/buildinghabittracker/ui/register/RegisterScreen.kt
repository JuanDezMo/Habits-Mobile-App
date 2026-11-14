package co.com.buildinghabittracker.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.com.buildinghabittracker.domain.auth.RegistrationRules
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.common_continue
import co.com.buildinghabittracker.resources.register_answer_label
import co.com.buildinghabittracker.resources.register_confirm_pin_subtitle
import co.com.buildinghabittracker.resources.register_confirm_pin_title
import co.com.buildinghabittracker.resources.register_create_pin_subtitle
import co.com.buildinghabittracker.resources.register_create_pin_title
import co.com.buildinghabittracker.resources.register_data_subtitle
import co.com.buildinghabittracker.resources.register_data_title
import co.com.buildinghabittracker.resources.register_email_label
import co.com.buildinghabittracker.resources.register_finish
import co.com.buildinghabittracker.resources.register_name_label
import co.com.buildinghabittracker.resources.register_pin_mismatch
import co.com.buildinghabittracker.resources.register_question_label
import co.com.buildinghabittracker.resources.register_security_subtitle
import co.com.buildinghabittracker.resources.register_security_title
import co.com.buildinghabittracker.resources.register_step
import co.com.buildinghabittracker.ui.components.BackPill
import co.com.buildinghabittracker.ui.components.FormField
import co.com.buildinghabittracker.ui.components.Lobe
import co.com.buildinghabittracker.ui.components.NoticePill
import co.com.buildinghabittracker.ui.components.PillButton
import co.com.buildinghabittracker.ui.components.PinPad
import co.com.buildinghabittracker.ui.testing.TestTags
import co.com.buildinghabittracker.ui.theme.HabitPalette
import co.com.buildinghabittracker.ui.theme.ParkTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla de registro (con estado): pide el ViewModel a Koin, observa su estado y
 * le pasa los eventos. El dibujo está en [RegisterContent].
 */
@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) onRegistered()
    }

    RegisterContent(
        state = state,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onContinue = viewModel::onContinueFromData,
        onPinDigit = viewModel::onPinDigit,
        onPinDelete = viewModel::onPinDelete,
        onQuestionChange = viewModel::onQuestionChange,
        onAnswerChange = viewModel::onAnswerChange,
        onFinish = viewModel::onFinish,
        onBack = viewModel::onBack,
    )
}

/**
 * Pantalla de registro (sin estado): solo dibuja [state] y avisa los eventos.
 * Al no depender del ViewModel, se puede previsualizar y probar con cualquier estado.
 *
 * Diseño: lóbulo rosa con el paso y el título; debajo, los campos o el teclado.
 */
@Composable
fun RegisterContent(
    state: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onContinue: () -> Unit,
    onPinDigit: (Char) -> Unit,
    onPinDelete: () -> Unit,
    onQuestionChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit,
) {
    val (title, subtitle) = when (state.step) {
        RegisterStep.DATA -> Res.string.register_data_title to Res.string.register_data_subtitle
        RegisterStep.CREATE_PIN -> Res.string.register_create_pin_title to Res.string.register_create_pin_subtitle
        RegisterStep.CONFIRM_PIN -> Res.string.register_confirm_pin_title to Res.string.register_confirm_pin_subtitle
        RegisterStep.SECURITY -> Res.string.register_security_title to Res.string.register_security_subtitle
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ParkTheme.colors.water)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .imePadding()
            .testTag(TestTags.REGISTER_SCREEN),
    ) {
        Lobe(color = HabitPalette.pink) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (state.step != RegisterStep.DATA) {
                    BackPill(
                        onClick = onBack,
                        containerColor = ParkTheme.colors.water,
                        modifier = Modifier.testTag(TestTags.REGISTER_BACK_BUTTON),
                    )
                }
                NoticePill(
                    stringResource(Res.string.register_step, state.step.ordinal + 1, RegisterStep.entries.size),
                    color = ParkTheme.colors.water,
                )
            }
            Spacer(Modifier.height(22.dp))
            Text(stringResource(title), style = MaterialTheme.typography.displaySmall, color = ParkTheme.colors.onAccent)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(subtitle), style = MaterialTheme.typography.bodyMedium, color = ParkTheme.colors.onAccent)
        }
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            when (state.step) {
                RegisterStep.DATA -> DataStep(state, onNameChange, onEmailChange, onContinue)
                RegisterStep.CREATE_PIN, RegisterStep.CONFIRM_PIN -> PinStep(state, onPinDigit, onPinDelete)
                RegisterStep.SECURITY -> SecurityStep(state, onQuestionChange, onAnswerChange, onFinish)
            }
        }
    }
}

@Composable
private fun DataStep(
    state: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onContinue: () -> Unit,
) {
    FormField(
        value = state.name,
        onValueChange = onNameChange,
        label = stringResource(Res.string.register_name_label),
        error = state.nameError,
        inputTestTag = TestTags.REGISTER_NAME_INPUT,
        errorTestTag = TestTags.REGISTER_NAME_ERROR,
        maxLength = RegistrationRules.NAME_MAX,
    )
    FormField(
        value = state.email,
        onValueChange = onEmailChange,
        label = stringResource(Res.string.register_email_label),
        error = state.emailError,
        inputTestTag = TestTags.REGISTER_EMAIL_INPUT,
        errorTestTag = TestTags.REGISTER_EMAIL_ERROR,
        maxLength = RegistrationRules.EMAIL_MAX,
        keyboardType = KeyboardType.Email,
    )
    Spacer(Modifier.height(8.dp))
    PillButton(
        text = stringResource(Res.string.common_continue),
        onClick = onContinue,
        modifier = Modifier.testTag(TestTags.REGISTER_CONTINUE_BUTTON),
    )
}

@Composable
private fun PinStep(state: RegisterUiState, onPinDigit: (Char) -> Unit, onPinDelete: () -> Unit) {
    if (state.pinMismatch) {
        NoticePill(
            stringResource(Res.string.register_pin_mismatch),
            modifier = Modifier.fillMaxWidth().testTag(TestTags.REGISTER_PIN_MISMATCH_TEXT),
        )
    }
    PinPad(
        enteredDigits = state.pin.length,
        onDigit = onPinDigit,
        onDelete = onPinDelete,
        pad = TestTags.PinPad.REGISTER,
    )
}

@Composable
private fun SecurityStep(
    state: RegisterUiState,
    onQuestionChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
    onFinish: () -> Unit,
) {
    FormField(
        value = state.question,
        onValueChange = onQuestionChange,
        label = stringResource(Res.string.register_question_label),
        error = state.questionError,
        inputTestTag = TestTags.REGISTER_QUESTION_INPUT,
        errorTestTag = TestTags.REGISTER_QUESTION_ERROR,
        minLength = RegistrationRules.QUESTION_MIN,
        maxLength = RegistrationRules.QUESTION_MAX,
    )
    FormField(
        value = state.answer,
        onValueChange = onAnswerChange,
        label = stringResource(Res.string.register_answer_label),
        error = state.answerError,
        inputTestTag = TestTags.REGISTER_ANSWER_INPUT,
        errorTestTag = TestTags.REGISTER_ANSWER_ERROR,
        minLength = RegistrationRules.ANSWER_MIN,
        maxLength = RegistrationRules.ANSWER_MAX,
    )
    Spacer(Modifier.height(8.dp))
    PillButton(
        text = stringResource(Res.string.register_finish),
        onClick = onFinish,
        enabled = !state.isSaving,
        modifier = Modifier.testTag(TestTags.REGISTER_FINISH_BUTTON),
    )
}
