package co.com.buildinghabittracker.ui.testing

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

/** Android: convierte cada `testTag` en `resource-id` para Maestro y Appium (UiAutomator2). */
@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.exposeTestTagsForUiTests(): Modifier =
    this.semantics { testTagsAsResourceId = true }
