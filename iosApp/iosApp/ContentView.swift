import UIKit
import SwiftUI
import Shared

/// Puente entre SwiftUI y Compose: muestra la pantalla raíz HabitApp del módulo compartido.
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        IosEntryPointKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}
