//
//  ContentView.swift
//  ios
//
//  Created by Matheus Barbieri Corregiari on 20/08/25.
//

import SwiftUI
import BacateKit

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return ControllerKt.Controller()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea()
    }
}
