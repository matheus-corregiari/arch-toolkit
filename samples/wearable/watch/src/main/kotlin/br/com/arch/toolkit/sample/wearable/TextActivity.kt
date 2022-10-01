package br.com.arch.toolkit.sample.wearable

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.wearable.extension.listenToWearableMessages
import br.com.arch.toolkit.wearable.extension.registerCapability
import br.com.arch.toolkit.wearable.extension.removeCapability
import br.com.arch.toolkit.wearable.extension.stopListeningToWearableMessages
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent

class TextActivity : Activity(), MessageClient.OnMessageReceivedListener {

    private val textView by viewProvider<TextView>(R.id.text_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)
    }

    override fun onResume() {
        super.onResume()
        registerCapability(Capabilities.TEXT.name)
        listenToWearableMessages(this)
    }

    override fun onPause() {
        stopListeningToWearableMessages(this)
        removeCapability(Capabilities.TEXT.name)
        super.onPause()
    }

    override fun onMessageReceived(event: MessageEvent) {
        if (event.path.equals(Capabilities.TEXT.name, true)) {
            textView.text = String(event.data)
        }
    }
}
