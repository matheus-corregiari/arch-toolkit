import SwiftUI
import Showcase

@main
struct ShowcaseApp: App {
    var body: some Scene {
        WindowGroup {
            ShowcaseView()
                .ignoresSafeArea()
                .onOpenURL { ControllerKt.openShowcaseLink(value: $0.absoluteString) }
        }
    }
}

struct ShowcaseView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        ControllerKt.createController()
    }
    func updateUIViewController(_ controller: UIViewController, context: Context) {}
}
