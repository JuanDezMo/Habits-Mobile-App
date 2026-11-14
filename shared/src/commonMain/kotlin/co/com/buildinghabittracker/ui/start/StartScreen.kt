package co.com.buildinghabittracker.ui.start

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.com.buildinghabittracker.ui.testing.TestTags
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla de arranque: muestra un indicador de carga mientras revisa si hay usuario,
 * y después avisa a dónde ir. Dura una fracción de segundo.
 */
@Composable
fun StartScreen(
    onNeedsRegister: () -> Unit,
    onNeedsPin: () -> Unit,
    viewModel: StartViewModel = koinViewModel(),
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    // LaunchedEffect se ejecuta cuando cambia `destination`; ahí se navega una sola vez.
    LaunchedEffect(destination) {
        when (destination) {
            StartDestination.REGISTER -> onNeedsRegister()
            StartDestination.PIN -> onNeedsPin()
            StartDestination.LOADING -> Unit
        }
    }

    Box(Modifier.fillMaxSize().testTag(TestTags.START_SCREEN), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
