import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        KoinHelper.shared.initKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}