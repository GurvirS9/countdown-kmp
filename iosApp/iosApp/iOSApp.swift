import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        MainViewControllerKt.initKoinHelper()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}