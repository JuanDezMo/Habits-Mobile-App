import SwiftUI
import Shared

/// Punto de entrada de la app en iOS. Equivale a HabitTrackerApplication en Android:
/// inicia Koin una vez y muestra la UI compartida.
@main
struct iOSApp: App {
    init() {
        IosEntryPointKt.startKoinForIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
