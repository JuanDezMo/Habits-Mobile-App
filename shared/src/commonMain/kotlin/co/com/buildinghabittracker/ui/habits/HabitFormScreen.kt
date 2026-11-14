package co.com.buildinghabittracker.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.com.buildinghabittracker.domain.habit.HabitValidator
import co.com.buildinghabittracker.resources.Res
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
import co.com.buildinghabittracker.ui.components.BackPill
import co.com.buildinghabittracker.ui.components.FormField
import co.com.buildinghabittracker.ui.components.PillButton
import co.com.buildinghabittracker.ui.components.PillTextButton
import co.com.buildinghabittracker.ui.testing.TestTags
import co.com.buildinghabittracker.ui.testing.exposeTestTagsForUiTests
import co.com.buildinghabittracker.ui.theme.HabitPalette
import co.com.buildinghabittracker.ui.theme.ParkTheme
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

/**
 * Formulario de hábito (sin estado). Diseño: el contenido se desplaza y el botón Guardar
 * queda fijo abajo (también sobre el teclado). Archivar vive solo al final del contenido,
 * lejos de Guardar.
 */
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
            .background(ParkTheme.colors.water)
            .imePadding()
            .testTag(TestTags.FORM_SCREEN),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BackPill(onClick = onBack, modifier = Modifier.testTag(TestTags.FORM_BACK_BUTTON))
            Text(
                stringResource(if (state.isEdit) Res.string.form_edit_title else Res.string.form_new_title),
                style = MaterialTheme.typography.headlineMedium,
                color = ParkTheme.colors.ink,
            )
            if (!state.isLoading) {
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

                // C5: solo un hábito que ya existe se puede archivar. Va aislado, al final.
                if (state.isEdit) {
                    Spacer(Modifier.height(20.dp))
                    PillTextButton(
                        text = stringResource(Res.string.form_archive),
                        onClick = onArchiveRequested,
                        contentColor = ParkTheme.colors.error,
                        modifier = Modifier.fillMaxWidth().testTag(TestTags.FORM_ARCHIVE_BUTTON),
                    )
                }
            }
        }
        // Guardar: fijo abajo, siempre visible.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ParkTheme.colors.water)
                .navigationBarsPadding()
                .padding(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 14.dp),
        ) {
            PillButton(
                text = stringResource(Res.string.form_save),
                onClick = onSave,
                enabled = !state.isSaving && !state.isLoading,
                icon = Icons.Filled.Check,
                modifier = Modifier.testTag(TestTags.FORM_SAVE_BUTTON),
            )
        }
    }

    if (state.showArchiveConfirm) {
        ArchiveDialog(state.title.trim(), onArchiveConfirmed, onArchiveCancelled)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = ParkTheme.colors.ink)
}

/** C2: emoji del hábito, de una lista fija. Círculos blancos; el elegido, en tinta. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmojiPicker(selected: String, onSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(stringResource(Res.string.form_emoji_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HabitValidator.EMOJIS.forEachIndexed { index, emoji ->
                val isSelected = emoji == selected
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) ParkTheme.colors.ink else ParkTheme.colors.field)
                        .selectable(selected = isSelected, role = Role.RadioButton) { onSelected(emoji) }
                        .testTag(TestTags.formEmoji(index)),
                    contentAlignment = Alignment.Center,
                ) { Text(emoji, fontSize = 22.sp) }
            }
        }
    }
}

/** C2: color del hábito, de la paleta fija. El elegido lleva un aro y un ✓. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColorPicker(selected: String, onSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(stringResource(Res.string.form_color_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HabitValidator.COLORS.forEachIndexed { index, color ->
                val isSelected = color == selected
                val description = stringResource(Res.string.form_color_description, index + 1)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .border(2.dp, if (isSelected) ParkTheme.colors.ink else Color.Transparent, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(colorFromHex(color))
                        .selectable(selected = isSelected, role = Role.RadioButton) { onSelected(color) }
                        .semantics { contentDescription = description }
                        .testTag(TestTags.formColor(index)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isSelected) Icon(Icons.Filled.Check, contentDescription = null, tint = ParkTheme.colors.onAccent)
                }
            }
        }
    }
}

/** C3: días de la semana (al menos uno) con el atajo "Todos los días". Los 7 caben en una fila. */
@Composable
private fun DaysPicker(state: HabitFormUiState, onDayToggled: (DayOfWeek) -> Unit, onAllDaysToggled: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionLabel(stringResource(Res.string.form_days_label))
            Spacer(Modifier.weight(1f))
            PillTextButton(
                text = stringResource(if (state.allDaysSelected) Res.string.form_clear_days else Res.string.form_all_days),
                onClick = onAllDaysToggled,
                modifier = Modifier.testTag(TestTags.FORM_ALL_DAYS_BUTTON),
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DayOfWeek.entries.forEach { day ->
                val isOn = day in state.days
                val fullName = dayFullName(day)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isOn) ParkTheme.colors.ink else ParkTheme.colors.field)
                        .toggleable(value = isOn, role = Role.Checkbox) { onDayToggled(day) }
                        .semantics { contentDescription = fullName }
                        .testTag(TestTags.formDay(day)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        dayShortName(day),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isOn) ParkTheme.colors.water else ParkTheme.colors.ink,
                    )
                }
            }
        }
        if (state.errors.noDays) {
            Text(
                stringResource(Res.string.form_days_error),
                color = ParkTheme.colors.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp).testTag(TestTags.FORM_DAYS_ERROR),
            )
        }
    }
}

/** C5: confirmación antes de archivar (diálogo de Material con los colores del mapa). */
@Composable
private fun ArchiveDialog(title: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        // El diálogo es otra ventana: hay que exponer sus identificadores para Maestro aquí también.
        modifier = Modifier.exposeTestTagsForUiTests(),
        containerColor = ParkTheme.colors.water,
        shape = MaterialTheme.shapes.large,
        title = {
            Text(stringResource(Res.string.form_archive_dialog_title, title), style = MaterialTheme.typography.headlineMedium, color = ParkTheme.colors.ink)
        },
        text = {
            Text(stringResource(Res.string.form_archive_dialog_text), style = MaterialTheme.typography.bodyMedium, color = ParkTheme.colors.ink)
        },
        confirmButton = {
            PillTextButton(
                text = stringResource(Res.string.form_archive),
                onClick = onConfirm,
                containerColor = HabitPalette.coral,
                contentColor = ParkTheme.colors.onAccent,
                modifier = Modifier.testTag(TestTags.FORM_ARCHIVE_CONFIRM_BUTTON),
            )
        },
        dismissButton = {
            PillTextButton(
                text = stringResource(Res.string.common_cancel),
                onClick = onDismiss,
                modifier = Modifier.testTag(TestTags.FORM_ARCHIVE_CANCEL_BUTTON),
            )
        },
    )
}
