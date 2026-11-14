package co.com.buildinghabittracker.ui.habits

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.com.buildinghabittracker.domain.habit.Habit
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.common_back
import co.com.buildinghabittracker.resources.habits_add
import co.com.buildinghabittracker.resources.habits_empty_active
import co.com.buildinghabittracker.resources.habits_empty_archived
import co.com.buildinghabittracker.resources.habits_tab_active
import co.com.buildinghabittracker.resources.habits_tab_archived
import co.com.buildinghabittracker.resources.habits_title
import co.com.buildinghabittracker.resources.habits_unarchive
import co.com.buildinghabittracker.resources.habits_unarchive_blocked
import co.com.buildinghabittracker.ui.testing.TestTags
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla Hábitos (con estado): lista de activos y archivados (C5).
 *
 * @param onAddHabit abre el formulario vacío.
 * @param onEditHabit abre el formulario con un hábito activo. En la iteración 5 tocar la fila
 * abrirá el Detalle, y desde ahí se editará.
 */
@Composable
fun HabitsScreen(
    onBack: () -> Unit,
    onAddHabit: () -> Unit,
    onEditHabit: (habitId: Long) -> Unit,
    viewModel: HabitsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HabitsContent(
        state = state,
        onBack = onBack,
        onTabSelected = viewModel::onTabSelected,
        onAddHabit = onAddHabit,
        onEditHabit = onEditHabit,
        onUnarchive = viewModel::onUnarchive,
        onUnarchiveMessageShown = viewModel::onUnarchiveMessageShown,
    )
}

/** Pantalla Hábitos (sin estado): solo dibuja [state] y avisa los eventos. */
@Composable
fun HabitsContent(
    state: HabitsUiState,
    onBack: () -> Unit,
    onTabSelected: (HabitsTab) -> Unit,
    onAddHabit: () -> Unit,
    onEditHabit: (Long) -> Unit,
    onUnarchive: (Long) -> Unit,
    onUnarchiveMessageShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val blockedMessage = stringResource(Res.string.habits_unarchive_blocked)

    // C1: aviso cuando no se puede desarchivar porque el título ya está en uso.
    LaunchedEffect(state.unarchiveBlocked) {
        if (state.unarchiveBlocked) {
            snackbarHostState.showSnackbar(blockedMessage)
            onUnarchiveMessageShown()
        }
    }

    Scaffold(
        modifier = Modifier.testTag(TestTags.HABITS_SCREEN),
        snackbarHost = {
            SnackbarHost(snackbarHostState, modifier = Modifier.testTag(TestTags.HABITS_UNARCHIVE_BLOCKED_TEXT))
        },
        floatingActionButton = {
            val addLabel = stringResource(Res.string.habits_add)
            FloatingActionButton(
                onClick = onAddHabit,
                modifier = Modifier.testTag(TestTags.HABITS_ADD_BUTTON).semantics { contentDescription = addLabel },
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TextButton(onClick = onBack, modifier = Modifier.testTag(TestTags.HABITS_BACK_BUTTON)) {
                Text("← " + stringResource(Res.string.common_back))
            }
            Text(stringResource(Res.string.habits_title), style = MaterialTheme.typography.headlineMedium)
            HabitsTabs(state, onTabSelected)
            if (!state.isLoading && state.visible.isEmpty()) {
                EmptyState(state.tab)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    // Espacio al final para que el botón + no tape la última fila.
                    contentPadding = PaddingValues(bottom = 88.dp),
                ) {
                    items(state.visible, key = { it.id }) { habit ->
                        HabitRow(habit, onEditHabit, onUnarchive)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitsTabs(state: HabitsUiState, onTabSelected: (HabitsTab) -> Unit) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        SegmentedButton(
            selected = state.tab == HabitsTab.ACTIVE,
            onClick = { onTabSelected(HabitsTab.ACTIVE) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            modifier = Modifier.testTag(TestTags.HABITS_TAB_ACTIVE),
        ) { Text(stringResource(Res.string.habits_tab_active, state.active.size)) }
        SegmentedButton(
            selected = state.tab == HabitsTab.ARCHIVED,
            onClick = { onTabSelected(HabitsTab.ARCHIVED) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            modifier = Modifier.testTag(TestTags.HABITS_TAB_ARCHIVED),
        ) { Text(stringResource(Res.string.habits_tab_archived, state.archived.size)) }
    }
}

@Composable
private fun EmptyState(tab: HabitsTab) {
    val text = if (tab == HabitsTab.ACTIVE) Res.string.habits_empty_active else Res.string.habits_empty_archived
    Text(
        stringResource(text),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp).testTag(TestTags.HABITS_EMPTY_TEXT),
    )
}

/**
 * Una fila de la lista. Activo: tocarla abre el formulario.
 * Archivado: no se edita; solo tiene el botón "Desarchivar".
 */
@Composable
private fun HabitRow(habit: Habit, onEditHabit: (Long) -> Unit, onUnarchive: (Long) -> Unit) {
    val rowModifier = Modifier.fillMaxWidth().testTag(TestTags.habitsRow(habit.id))
    Card(modifier = if (habit.isArchived) rowModifier else rowModifier.clickable { onEditHabit(habit.id) }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HabitEmoji(habit.emoji, habit.color)
            Column(modifier = Modifier.weight(1f)) {
                Text(habit.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    daysLabel(habit.days),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (habit.isArchived) {
                TextButton(
                    onClick = { onUnarchive(habit.id) },
                    modifier = Modifier.testTag(TestTags.habitsUnarchive(habit.id)),
                ) { Text(stringResource(Res.string.habits_unarchive)) }
            }
        }
    }
}
