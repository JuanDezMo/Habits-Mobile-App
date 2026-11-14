package co.com.buildinghabittracker

import androidx.compose.ui.window.ComposeUIViewController
import co.com.buildinghabittracker.di.initKoin

/**
 * Funciones que llama el código Swift de `iosApp` (ver iOSApp.swift y ContentView.swift).
 * En Swift se usan así: `IosEntryPointKt.startKoinForIos()` y `IosEntryPointKt.MainViewController()`.
 */

/** Inicia Koin en iOS. Se llama una sola vez al arrancar la app. */
fun startKoinForIos() {
    initKoin()
}

/** Pantalla raíz para iOS: envuelve [HabitApp] en un UIViewController. */
fun MainViewController() = ComposeUIViewController { HabitApp() }
