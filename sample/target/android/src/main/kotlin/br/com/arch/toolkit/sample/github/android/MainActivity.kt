package br.com.arch.toolkit.sample.github.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.arch.toolkit.sample.shared.ShowcaseApp

class MainActivity : AppCompatActivity() {
    private val deepLink = androidx.compose.runtime.mutableStateOf<String?>(null)

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLink.value = intent.dataString
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        deepLink.value = if (savedInstanceState == null) intent.dataString else null
        setContent { ShowcaseApp(deepLink.value) { deepLink.value = null } }
    }
}
