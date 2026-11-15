package co.com.buildinghabittracker.ui.today

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.month_1
import co.com.buildinghabittracker.resources.month_10
import co.com.buildinghabittracker.resources.month_11
import co.com.buildinghabittracker.resources.month_12
import co.com.buildinghabittracker.resources.month_2
import co.com.buildinghabittracker.resources.month_3
import co.com.buildinghabittracker.resources.month_4
import co.com.buildinghabittracker.resources.month_5
import co.com.buildinghabittracker.resources.month_6
import co.com.buildinghabittracker.resources.month_7
import co.com.buildinghabittracker.resources.month_8
import co.com.buildinghabittracker.resources.month_9
import co.com.buildinghabittracker.resources.pin_greeting
import co.com.buildinghabittracker.resources.today_all_done
import co.com.buildinghabittracker.resources.today_create_first
import co.com.buildinghabittracker.resources.today_date
import co.com.buildinghabittracker.resources.today_due
import co.com.buildinghabittracker.resources.today_empty
import co.com.buildinghabittracker.resources.today_missing_many
import co.com.buildinghabittracker.resources.today_missing_one
import co.com.buildinghabittracker.resources.today_nothing_scheduled
import co.com.buildinghabittracker.resources.today_progress_of
import co.com.buildinghabittracker.resources.today_streak
import co.com.buildinghabittracker.resources.today_streak_due
import co.com.buildinghabittracker.resources.today_undo
import co.com.buildinghabittracker.resources.today_unmarked
import co.com.buildinghabittracker.resources.today_yesterday
import co.com.buildinghabittracker.resources.today_yesterday_done
import co.com.buildinghabittracker.resources.today_yesterday_pending
import co.com.buildinghabittracker.ui.components.Lobe
import co.com.buildinghabittracker.ui.components.ParkSnackbarHost
import co.com.buildinghabittracker.ui.components.NoticePill
import co.com.buildinghabittracker.ui.components.PillButton
import co.com.buildinghabittracker.ui.components.RoundChip
import co.com.buildinghabittracker.ui.habits.colorFromHex
import co.com.buildinghabittracker.ui.habits.dayFullName
import co.com.buildinghabittracker.ui.navigation.NavBarSpace
import co.com.buildinghabittracker.ui.testing.TestTags
import co.com.buildinghabittracker.ui.theme.ParkTheme
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla Hoy (con estado): los hábitos del día y el check-in (D1–D3, E1, E2).
 *
 * @param onCreateHabit abre el formulario vacío (solo se ofrece cuando no hay hábitos).
 */
@Composable
fun TodayScreen(
    onCreateHabit: () -> Unit,
    viewModel: TodayViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TodayContent(
        state = state,
        onToggle = viewModel::onToggle,
        onUndo = viewModel::onUndo,
        onUndoDismissed = viewModel::onUndoDismissed,
        onCreateHabit = onCreateHabit,
    )
}

/**
 * Hoy (sin estado). Diseño "Mapa de parque": lóbulo verde con la fecha, el saludo y el
 * progreso "2 de 3"; el lago del lóbulo es lo que falta del día. Cada hábito es una cápsula
 * de su color: tocarla entera marca o desmarca. Las cápsulas no cambian de orden al marcar.
 */
@Composable
fun TodayContent(
    state: TodayUiState,
    onToggle: (TodayHabit) -> Unit,
    onUndo: (UndoUnmark) -> Unit,
    onUndoDismissed: (UndoUnmark) -> Unit,
    onCreateHabit: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val undo = state.undo
    val undoMessage = undo?.let { stringResource(Res.string.today_unmarked, it.title) }
    val undoLabel = stringResource(Res.string.today_undo)

    // D3: aviso "Meditar desmarcado · Deshacer" por si fue un toque accidental.
    LaunchedEffect(undo) {
        if (undo == null || undoMessage == null) return@LaunchedEffect
        var handled = false
        try {
            val result = snackbarHostState.showSnackbar(undoMessage, actionLabel = undoLabel, duration = SnackbarDuration.Long)
            handled = true
            if (result == SnackbarResult.ActionPerformed) onUndo(undo) else onUndoDismissed(undo)
        } finally {
            // Si el usuario sale de Hoy con el aviso abierto, se descarta: no reaparece al volver.
            if (!handled) onUndoDismissed(undo)
        }
    }

    Scaffold(
        modifier = Modifier.testTag(TestTags.TODAY_SCREEN),
        containerColor = ParkTheme.colors.water,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            ParkSnackbarHost(
                snackbarHostState,
                modifier = Modifier.navigationBarsPadding().padding(bottom = NavBarSpace).testTag(TestTags.TODAY_UNDO_SNACKBAR),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = NavBarSpace + 40.dp),
        ) {
            item { Header(state) }
            when {
                state.isLoading -> Unit
                !state.hasHabits -> item { EmptyState(onCreateHabit) }
                else -> {
                    items(state.today, key = { "today_${it.habit.id}" }) { item ->
                        TodayCapsule(item, onToggle)
                    }
                    if (state.yesterday.isNotEmpty()) {
                        item(key = "yesterday_label") {
                            Text(
                                stringResource(Res.string.today_yesterday),
                                style = MaterialTheme.typography.bodySmall,
                                color = ParkTheme.colors.ink,
                                modifier = Modifier.padding(start = 22.dp, top = 8.dp).testTag(TestTags.TODAY_YESTERDAY_LABEL),
                            )
                        }
                        items(state.yesterday, key = { "yesterday_${it.habit.id}" }) { item ->
                            YesterdayCapsule(item, onToggle)
                        }
                    }
                }
            }
        }
    }
}

/** Lóbulo verde: fecha, saludo, progreso y aviso de lo que falta. */
@Composable
private fun Header(state: TodayUiState) {
    val total = state.today.size
    val showProgress = state.hasHabits && total > 0
    // El lago crece con lo que falta del día; sin hábitos hoy no hay lago.
    val lake = if (showProgress) (total - state.doneCount).toFloat() / total else null
    Lobe(color = ParkTheme.colors.land, lake = lake) {
        state.date?.let {
            Text(formatDate(it), style = MaterialTheme.typography.bodyMedium, color = ParkTheme.colors.onAccent)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            stringResource(Res.string.pin_greeting, state.userName),
            style = MaterialTheme.typography.headlineMedium,
            color = ParkTheme.colors.onAccent,
        )
        if (showProgress) {
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.testTag(TestTags.TODAY_PROGRESS_TEXT),
            ) {
                Text("${state.doneCount}", style = MaterialTheme.typography.displayLarge, color = ParkTheme.colors.onAccent)
                Text(
                    " " + stringResource(Res.string.today_progress_of, total),
                    style = MaterialTheme.typography.headlineSmall,
                    color = ParkTheme.colors.onAccent,
                    modifier = Modifier.padding(bottom = 14.dp),
                )
            }
        }
        if (state.hasHabits) {
            Spacer(Modifier.height(10.dp))
            val pending = state.pending
            val notice = when {
                total == 0 -> stringResource(Res.string.today_nothing_scheduled)
                pending.isEmpty() -> stringResource(Res.string.today_all_done)
                pending.size == 1 -> stringResource(Res.string.today_missing_one, pending.first().habit.title)
                else -> stringResource(Res.string.today_missing_many, pending.size)
            }
            NoticePill(notice, modifier = Modifier.testTag(TestTags.TODAY_NOTICE_TEXT))
        }
    }
}

@Composable
private fun EmptyState(onCreateHabit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(Res.string.today_empty),
            style = MaterialTheme.typography.headlineSmall,
            color = ParkTheme.colors.ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp).testTag(TestTags.TODAY_EMPTY_TEXT),
        )
        PillButton(
            text = stringResource(Res.string.today_create_first),
            onClick = onCreateHabit,
            icon = Icons.Filled.Add,
            modifier = Modifier.testTag(TestTags.TODAY_CREATE_HABIT_BUTTON),
        )
    }
}

/** Hábito de hoy: cápsula de su color con la racha. */
@Composable
private fun TodayCapsule(item: TodayHabit, onToggle: (TodayHabit) -> Unit) {
    val streak = if (item.streak > 0) pluralStringResource(Res.plurals.today_streak, item.streak, item.streak) else null
    val subtitle = when {
        item.isDone -> streak ?: ""
        streak != null -> stringResource(Res.string.today_streak_due, streak)
        else -> stringResource(Res.string.today_due)
    }
    CheckCapsule(
        title = "${item.habit.emoji} ${item.habit.title}",
        subtitle = subtitle,
        isDone = item.isDone,
        background = colorFromHex(item.habit.color),
        textColor = ParkTheme.colors.onAccent,
        onToggle = { onToggle(item) },
        modifier = Modifier.testTag(TestTags.todayHabit(item.habit.id)),
    )
}

/** Hábito de ayer: cápsula blanca, para que no se confunda con los de hoy. */
@Composable
private fun YesterdayCapsule(item: TodayHabit, onToggle: (TodayHabit) -> Unit) {
    CheckCapsule(
        title = "${item.habit.emoji} ${item.habit.title}",
        subtitle = stringResource(if (item.isDone) Res.string.today_yesterday_done else Res.string.today_yesterday_pending),
        isDone = item.isDone,
        background = ParkTheme.colors.field,
        textColor = ParkTheme.colors.ink,
        onToggle = { onToggle(item) },
        modifier = Modifier.testTag(TestTags.todayYesterdayHabit(item.habit.id)),
    )
}

/**
 * Cápsula que se marca y desmarca con un toque en cualquier parte.
 * Sin marcar: chip vacío al final. Marcada: el chip cruza al inicio, relleno, con ✓.
 * Para lectores de pantalla es una casilla (Role.Checkbox) con su estado.
 */
@Composable
private fun CheckCapsule(
    title: String,
    subtitle: String,
    isDone: Boolean,
    background: Color,
    textColor: Color,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clip(CircleShape)
            .background(background)
            .toggleable(value = isDone, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(start = if (isDone) 8.dp else 22.dp, end = if (isDone) 22.dp else 8.dp, top = 8.dp, bottom = 8.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (isDone) {
            // El chip toma el color del texto y la ✓ el de la cápsula: se ve igual en claro y oscuro.
            RoundChip(textColor) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = background)
            }
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = textColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle.isNotEmpty()) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = textColor)
            }
        }
        if (!isDone) {
            RoundChip(ParkTheme.colors.water)
        }
    }
}

private val monthNames = listOf(
    Res.string.month_1, Res.string.month_2, Res.string.month_3, Res.string.month_4,
    Res.string.month_5, Res.string.month_6, Res.string.month_7, Res.string.month_8,
    Res.string.month_9, Res.string.month_10, Res.string.month_11, Res.string.month_12,
)

/** "jueves 14 de noviembre" / "Thursday, November 14", en el idioma elegido. */
@Composable
private fun formatDate(date: LocalDate): String = stringResource(
    Res.string.today_date,
    dayFullName(date.dayOfWeek),
    date.day,
    stringResource(monthNames[date.month.ordinal]),
)
