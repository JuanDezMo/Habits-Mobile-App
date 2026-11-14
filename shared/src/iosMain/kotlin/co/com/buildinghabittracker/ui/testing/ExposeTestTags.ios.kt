package co.com.buildinghabittracker.ui.testing

import androidx.compose.ui.Modifier

/** iOS: no hace falta nada; Compose Multiplatform ya publica el `testTag` como `accessibilityIdentifier`. */
actual fun Modifier.exposeTestTagsForUiTests(): Modifier = this
