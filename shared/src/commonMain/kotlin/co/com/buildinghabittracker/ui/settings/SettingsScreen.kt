package co.com.buildinghabittracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.com.buildinghabittracker.domain.settings.AppLanguage
import co.com.buildinghabittracker.domain.settings.AppTheme
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.language_english
import co.com.buildinghabittracker.resources.language_spanish
import co.com.buildinghabittracker.resources.settings_change_pin
import co.com.buildinghabittracker.resources.settings_language
import co.com.buildinghabittracker.resources.settings_pin_changed
import co.com.buildinghabittracker.resources.settings_security
import co.com.buildinghabittracker.resources.settings_subtitle
import co.com.buildinghabittracker.resources.settings_theme
import co.com.buildinghabittracker.resources.settings_theme_dark
import co.com.buildinghabittracker.resources.settings_theme_light
import co.com.buildinghabittracker.resources.settings_theme_system
import co.com.buildinghabittracker.resources.settings_title
import co.com.buildinghabittracker.ui.components.Lobe
import co.com.buildinghabittracker.ui.components.PillTab
import co.com.buildinghabittracker.ui.components.PillTabs
import co.com.buildinghabittracker.ui.components.RoundChip
import co.com.buildinghabittracker.ui.navigation.NavBarSpace
import co.com.buildinghabittracker.ui.testing.TestTags
import co.com.buildinghabittracker.ui.theme.ParkTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla Ajustes (con estado), tercera pestaña (G4, A8).
 *
 * @param pinChanged `true` cuando se vuelve de Cambiar PIN con éxito: se muestra el aviso
 * "PIN actualizado" y luego se llama a [onPinChangedShown].
 */
@Composable
fun SettingsScreen(
    pinChanged: Boolean,
    onPinChangedShown: () -> Unit,
    onChangePin: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsContent(
        state = state,
        pinChanged = pinChanged,
        onPinChangedShown = onPinChangedShown,
        onLanguageSelected = viewModel::onLanguageSelected,
        onThemeSelected = viewModel::onThemeSelected,
        onChangePin = onChangePin,
    )
}

/** Ajustes (sin estado). Diseño: lóbulo amarillo; idioma y tema en pestañas de cápsula. */
@Composable
fun SettingsContent(
    state: SettingsUiState,
    pinChanged: Boolean,
    onPinChangedShown: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onThemeSelected: (AppTheme) -> Unit,
    onChangePin: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val changedMessage = stringResource(Res.string.settings_pin_changed)
    LaunchedEffect(pinChanged) {
        if (pinChanged) {
            snackbarHostState.showSnackbar(changedMessage)
            onPinChangedShown()
        }
    }

    Box(Modifier.fillMaxSize().background(ParkTheme.colors.water).testTag(TestTags.SETTINGS_SCREEN)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(bottom = NavBarSpace),
        ) {
            Lobe(color = ParkTheme.colors.notice) {
                Text(stringResource(Res.string.settings_title), style = MaterialTheme.typography.displaySmall, color = ParkTheme.colors.onAccent)
                Spacer(Modifier.height(6.dp))
                Text(stringResource(Res.string.settings_subtitle), style = MaterialTheme.typography.bodyMedium, color = ParkTheme.colors.onAccent)
            }
            Column(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SectionLabel(stringResource(Res.string.settings_language))
                // Los nombres de idioma van siempre en su propio idioma.
                val languages = listOf(AppLanguage.SPANISH, AppLanguage.ENGLISH)
                PillTabs(
                    tabs = listOf(
                        PillTab(stringResource(Res.string.language_spanish), TestTags.SETTINGS_LANGUAGE_ES),
                        PillTab(stringResource(Res.string.language_english), TestTags.SETTINGS_LANGUAGE_EN),
                    ),
                    selectedIndex = languages.indexOf(state.language),
                    onSelect = { onLanguageSelected(languages[it]) },
                )

                Spacer(Modifier.height(8.dp))
                SectionLabel(stringResource(Res.string.settings_theme))
                PillTabs(
                    tabs = listOf(
                        PillTab(stringResource(Res.string.settings_theme_system), TestTags.SETTINGS_THEME_SYSTEM),
                        PillTab(stringResource(Res.string.settings_theme_light), TestTags.SETTINGS_THEME_LIGHT),
                        PillTab(stringResource(Res.string.settings_theme_dark), TestTags.SETTINGS_THEME_DARK),
                    ),
                    selectedIndex = state.theme.ordinal,
                    onSelect = { onThemeSelected(AppTheme.entries[it]) },
                )

                Spacer(Modifier.height(8.dp))
                SectionLabel(stringResource(Res.string.settings_security))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp)
                        .clip(CircleShape)
                        .background(ParkTheme.colors.field)
                        .clickable(role = Role.Button, onClick = onChangePin)
                        .padding(start = 22.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                        .testTag(TestTags.SETTINGS_CHANGE_PIN_BUTTON),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(Res.string.settings_change_pin),
                        style = MaterialTheme.typography.titleMedium,
                        color = ParkTheme.colors.ink,
                        modifier = Modifier.weight(1f),
                    )
                    RoundChip(ParkTheme.colors.water) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = ParkTheme.colors.ink)
                    }
                }
            }
        }
        SnackbarHost(
            snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = NavBarSpace)
                .testTag(TestTags.SETTINGS_PIN_CHANGED_TEXT),
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = ParkTheme.colors.ink, modifier = Modifier.padding(start = 4.dp))
}
