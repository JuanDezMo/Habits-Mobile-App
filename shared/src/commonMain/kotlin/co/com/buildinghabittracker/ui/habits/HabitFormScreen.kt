package co.com.buildinghabittracker.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.com.buildinghabittracker.domain.habit.HabitValidator
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.common_back
import co.com.buildinghabittracker.resources.common_cancel
import co.com.buildinghabittracker.resources.form_all_days
import co.com.buildinghabittracker.resources.form_archive
import co.com.buildinghabittracker.resources.form_archive_dialog_text
import co.com.buildinghabittracker.resources.form_archive_dialog_title
import co.com.buildinghabittracker.resources.form_clear_days
import co.com.buildinghabittracker.resources.form_color_description
import co.com.buildinghabittracker.resources.form_color_label
import co.com.buildinghabittracker.resources.form_days_error
import co.com.buildinghabittracker.resources.form_days_label
import co.com.buildinghabittracker.resources.form_description_label
import co.com.buildinghabittracker.resources.form_edit_title
import co.com.buildinghabittracker.resources.form_emoji_label
import co.com.buildinghabittracker.resources.form_new_title
import co.com.buildinghabittracker.resources.form_save
import co.com.buildinghabittracker.resources.form_title_label
import co.com.buildinghabittracker.ui.components.FormField
import co.com.buildinghabittracker.ui.testing.TestTags
import co.com.buildinghabittracker.ui.testing.exposeTestTagsForUiTests
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Formulario de hábito (con estado). Sirve para crear (habitId = 0) y para editar.
 *
 * @param onDone se llama al guardar o archivar: la navegación vuelve a la lista.
 */
@Composable
fun HabitFormScreen(
    habitId: Long,
    onDone: () -> Unit,
    onBack: () -> Unit,
    // `parametersOf(habitId)` le entrega el id al ViewModel (ver viewModelModule en Koin.kt).
    viewModel: HabitFormViewModel = koinViewModel { parametersOf(habitId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isDone) {
        if (state.isDone) onDone()
    }

    HabitFormContent(
        state = state,
        onBack = onBack,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onEmojiSelected = viewModel::onEmojiSelected,
        onColorSelected = viewModel::onColorSelected,
        onDayToggled = viewModel::onDayToggled,
        onAllDaysToggled = viewModel::onAllDaysToggled,
        onSave = viewModel::onSave,
        onArchiveRequested = viewModel::onArchiveRequested,
        onArchiveConfirmed = viewModel::onArchiveConfirmed,
        onArchiveCancelled = viewModel::onArchiveCancelled,
    )
}

/** Formulario de hábito (sin estado): solo dibuja [state] y avisa los eventos. */
@Composable
fun HabitFormContent(
    state: HabitFormUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onEmojiSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onDayToggled: (DayOfWeek) -> Unit,
    onAllDaysToggled: () -> Unit,
    onSave: () -> Unit,
    onArchiveRequested: () -> Unit,
    onArchiveConfirmed: () -> Unit,
    onArchiveCancelled: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag(TestTags.FORM_SCREEN),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(onClick = onBack, modifier = Modifier.testTag(TestTags.FORM_BACK_BUTTON)) {
            Text("← " + stringResource(Res.string.common_back))
        }
        Text(
            stringResource(if (state.isEdit) Res.string.form_edit_title else Res.string.form_new_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        if (state.isLoading) return@Column

        FormField(
            value = state.title,
            onValueChange = onTitleChange,
            label = stringResource(Res.string.form_title_label),
            error = state.errors.title,
            inputTestTag = TestTags.FORM_TITLE_INPUT,
            errorTestTag = TestTags.FORM_TITLE_ERROR,
            maxLength = HabitValidator.TITLE_MAX,
        )
        FormField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = stringResource(Res.string.form_description_label),
            error = state.errors.description,
            inputTestTag = TestTags.FORM_DESCRIPTION_INPUT,
            errorTestTag = TestTags.FORM_DESCRIPTION_ERROR,
            maxLength = HabitValidator.DESCRIPTION_MAX,
            singleLine = false,
        )
        EmojiPicker(state.emoji, onEmojiSelected)
        ColorPicker(state.color, onColorSelected)
        DaysPicker(state, onDayToggled, onAllDaysToggled)

        Button(
            onClick = onSave,
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth().testTag(TestTags.FORM_SAVE_BUTTON),
        ) { Text(stringResource(Res.string.form_save)) }

        // C5: solo un hábito que ya existe se puede archivar.
        if (state.isEdit) {
            OutlinedButton(
                onClick = onArchiveRequested,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().testTag(TestTags.FORM_ARCHIVE_BUTTON),
            ) { Text(stringResource(Res.string.form_archive)) }
        }
    }

    if (state.showArchiveConfirm) {
        ArchiveDialog(state.title.trim(), onArchiveConfirmed, onArchiveCancelled)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

/** C2: emoji del hábito, de una lista fija. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmojiPicker(selected: String, onSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(stringResource(Res.string.form_emoji_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HabitValidator.EMOJIS.forEachIndexed { index, emoji ->
                val isSelected = emoji == selected
                val shape = RoundedCornerShape(12.dp)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface, shape)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            shape = shape,
                        )
                        .selectable(selected = isSelected, role = Role.RadioButton) { onSelected(emoji) }
                        .testTag(TestTags.formEmoji(index)),
                    contentAlignment = Alignment.Center,
                ) { Text(emoji, fontSize = 22.sp) }
            }
        }
    }
}

/** C2: color del hábito, de una lista fija. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColorPicker(selected: String, onSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(stringResource(Res.string.form_color_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            HabitValidator.COLORS.forEachIndexed { index, color ->
                val isSelected = color == selected
                val description = stringResource(Res.string.form_color_description, index + 1)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .border(
                            width = 2.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface,
                            shape = CircleShape,
                        )
                        .padding(5.dp)
                        .background(colorFromHex(color), CircleShape)
                        .selectable(selected = isSelected, role = Role.RadioButton) { onSelected(color) }
                        .semantics { contentDescription = description }
                        .testTag(TestTags.formColor(index)),
                )
            }
        }
    }
}

/** C3: días de la semana (al menos uno) con el atajo "Todos los días". Los 7 caben en una fila. */
@Composable
private fun DaysPicker(state: HabitFormUiState, onDayToggled: (DayOfWeek) -> Unit, onAllDaysToggled: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(stringResource(Res.string.form_days_label))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DayOfWeek.entries.forEach { day ->
                val isOn = day in state.days
                val fullName = dayFullName(day)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(if (isOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, CircleShape)
                        .border(1.dp, if (isOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        .toggleable(value = isOn, role = Role.Checkbox) { onDayToggled(day) }
                        .semantics { contentDescription = fullName }
                        .testTag(TestTags.formDay(day)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        dayShortName(day),
                        fontWeight = FontWeight.Bold,
                        color = if (isOn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        TextButton(onClick = onAllDaysToggled, modifier = Modifier.testTag(TestTags.FORM_ALL_DAYS_BUTTON)) {
            Text(stringResource(if (state.allDaysSelected) Res.string.form_clear_days else Res.string.form_all_days))
        }
        if (state.errors.noDays) {
            Text(
                stringResource(Res.string.form_days_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag(TestTags.FORM_DAYS_ERROR),
            )
        }
    }
}

/** C5: confirmación antes de archivar. */
@Composable
private fun ArchiveDialog(title: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        // El diálogo es otra ventana: hay que exponer sus identificadores para Maestro aquí también.
        modifier = Modifier.exposeTestTagsForUiTests(),
        title = { Text(stringResource(Res.string.form_archive_dialog_title, title)) },
        text = { Text(stringResource(Res.string.form_archive_dialog_text)) },
        confirmButton = {
            TextButton(onClick = onConfirm, modifier = Modifier.testTag(TestTags.FORM_ARCHIVE_CONFIRM_BUTTON)) {
                Text(stringResource(Res.string.form_archive), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag(TestTags.FORM_ARCHIVE_CANCEL_BUTTON)) {
                Text(stringResource(Res.string.common_cancel))
            }
        },
    )
}
