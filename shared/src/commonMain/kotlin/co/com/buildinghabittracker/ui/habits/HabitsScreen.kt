package co.com.buildinghabittracker.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.com.buildinghabittracker.domain.habit.Habit
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.habits_add
import co.com.buildinghabittracker.resources.habits_archived_stamp
import co.com.buildinghabittracker.resources.habits_empty_active
import co.com.buildinghabittracker.resources.habits_empty_archived
import co.com.buildinghabittracker.resources.habits_summary
import co.com.buildinghabittracker.resources.habits_tab_active
import co.com.buildinghabittracker.resources.habits_tab_archived
import co.com.buildinghabittracker.resources.habits_title
import co.com.buildinghabittracker.resources.habits_unarchive
import co.com.buildinghabittracker.resources.habits_unarchive_blocked
import co.com.buildinghabittracker.ui.components.Lobe
import co.com.buildinghabittracker.ui.navigation.NavBarSpace
import co.com.buildinghabittracker.ui.components.PillTab
import co.com.buildinghabittracker.ui.components.PillTabs
import co.com.buildinghabittracker.ui.components.PillTextButton
import co.com.buildinghabittracker.ui.components.RoundChip
import co.com.buildinghabittracker.ui.testing.TestTags
import co.com.buildinghabittracker.ui.theme.HabitPalette
import co.com.buildinghabittracker.ui.theme.ParkTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla Hábitos (con estado): lista de activos y archivados (C5).
 *
 * @param onAddHabit abre el formulario vacío.
 * La barra inferior (Hoy / Hábitos / Ajustes) la dibuja AppNavHost encima de esta pantalla.
 *
 * @param onEditHabit abre el formulario con un hábito activo. En la iteración 5 tocar la fila
 * abrirá el Detalle, y desde ahí se editará.
 */
@Composable
fun HabitsScreen(
    onAddHabit: () -> Unit,
    onEditHabit: (habitId: Long) -> Unit,
    viewModel: HabitsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HabitsContent(
        state = state,
        onTabSelected = viewModel::onTabSelected,
        onAddHabit = onAddHabit,
        onEditHabit = onEditHabit,
        onUnarchive = viewModel::onUnarchive,
        onUnarchiveMessageShown = viewModel::onUnarchiveMessageShown,
    )
}

/**
 * Pantalla Hábitos (sin estado). Diseño: lóbulo azul con el título y el resumen; pestañas en
 * cápsula; cada hábito es una cápsula de su color; botón + coral.
 */
@Composable
fun HabitsContent(
    state: HabitsUiState,
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
        containerColor = ParkTheme.colors.water,
        // El lóbulo llega hasta arriba; cada pieza maneja sus propios márgenes del sistema.
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            SnackbarHost(
                snackbarHostState,
                modifier = Modifier.navigationBarsPadding().padding(bottom = NavBarSpace).testTag(TestTags.HABITS_UNARCHIVE_BLOCKED_TEXT),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit,
                shape = CircleShape,
                containerColor = HabitPalette.coral,
                contentColor = ParkTheme.colors.onAccent,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                modifier = Modifier.navigationBarsPadding().padding(bottom = NavBarSpace).size(66.dp).testTag(TestTags.HABITS_ADD_BUTTON),
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(Res.string.habits_add), modifier = Modifier.size(30.dp))
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            // Espacio al final para que el botón + no tape la última cápsula.
            contentPadding = PaddingValues(bottom = NavBarSpace + 120.dp),
        ) {
            item {
                Lobe(color = HabitPalette.blue) {
                    Text(stringResource(Res.string.habits_title), style = MaterialTheme.typography.displaySmall, color = ParkTheme.colors.onAccent)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        stringResource(Res.string.habits_summary, state.active.size, state.archived.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = ParkTheme.colors.onAccent,
                    )
                }
            }
            item {
                PillTabs(
                    tabs = listOf(
                        PillTab(stringResource(Res.string.habits_tab_active, state.active.size), TestTags.HABITS_TAB_ACTIVE),
                        PillTab(stringResource(Res.string.habits_tab_archived, state.archived.size), TestTags.HABITS_TAB_ARCHIVED),
                    ),
                    selectedIndex = state.tab.ordinal,
                    onSelect = { onTabSelected(HabitsTab.entries[it]) },
                    modifier = Modifier.padding(horizontal = 18.dp),
                )
            }
            if (!state.isLoading && state.visible.isEmpty()) {
                item { EmptyState(state.tab) }
            } else {
                items(state.visible, key = { it.id }) { habit ->
                    if (habit.isArchived) {
                        ArchivedCapsule(habit, onUnarchive)
                    } else {
                        HabitCapsule(habit, onClick = { onEditHabit(habit.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(tab: HabitsTab) {
    val text = if (tab == HabitsTab.ACTIVE) Res.string.habits_empty_active else Res.string.habits_empty_archived
    Text(
        stringResource(text),
        style = MaterialTheme.typography.headlineSmall,
        color = ParkTheme.colors.ink,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 32.dp).testTag(TestTags.HABITS_EMPTY_TEXT),
    )
}

/** Hábito activo: cápsula de su color; tocarla abre el formulario. */
@Composable
private fun HabitCapsule(habit: Habit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clip(CircleShape)
            .background(colorFromHex(habit.color))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 22.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            .testTag(TestTags.habitsRow(habit.id)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CapsuleText(habit, Modifier.weight(1f))
        RoundChip(ParkTheme.colors.water) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = ParkTheme.colors.ink)
        }
    }
}

/**
 * Hábito archivado: cápsula blanca con un sello "ARCHIVADO". No desaparece: queda sellado,
 * y el botón Desarchivar lo vuelve a activar (C5).
 */
@Composable
private fun ArchivedCapsule(habit: Habit, onUnarchive: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clip(CircleShape)
            .background(ParkTheme.colors.field)
            .padding(start = 22.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            .testTag(TestTags.habitsRow(habit.id)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CapsuleText(habit, Modifier.weight(1f), color = ParkTheme.colors.ink)
        Text(
            stringResource(Res.string.habits_archived_stamp),
            style = MaterialTheme.typography.labelSmall,
            color = ParkTheme.colors.ink,
            modifier = Modifier
                .rotate(-6f)
                .border(2.dp, ParkTheme.colors.ink, CircleShape)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        )
        PillTextButton(
            text = stringResource(Res.string.habits_unarchive),
            onClick = { onUnarchive(habit.id) },
            containerColor = ParkTheme.colors.ink,
            contentColor = ParkTheme.colors.water,
            modifier = Modifier.testTag(TestTags.habitsUnarchive(habit.id)),
        )
    }
}

@Composable
private fun CapsuleText(habit: Habit, modifier: Modifier, color: Color = ParkTheme.colors.onAccent) {
    Column(modifier) {
        Text(
            "${habit.emoji} ${habit.title}",
            style = MaterialTheme.typography.titleLarge,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(daysLabel(habit.days), style = MaterialTheme.typography.bodySmall, color = color)
    }
}
