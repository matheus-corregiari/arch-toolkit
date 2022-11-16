package br.com.arch.toolkit.sample.wearable

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.wearable.extension.listenToWearableRequests
import br.com.arch.toolkit.wearable.extension.registerCapability
import br.com.arch.toolkit.wearable.extension.removeCapability
import br.com.arch.toolkit.wearable.extension.sendRequestToWearable
import br.com.arch.toolkit.wearable.extension.stopListeningToWearableRequests
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.MessageClient

class RequestActivity : Activity(), MessageClient.RpcService {

    private val textView by viewProvider<TextView>(R.id.text_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)
    }

    override fun onResume() {
        super.onResume()
        registerCapability(Capabilities.REQUEST.name)
        listenToWearableRequests(this)
    }

    override fun onPause() {
        stopListeningToWearableRequests(this)
        removeCapability(Capabilities.REQUEST.name)
        super.onPause()
    }

    override fun onRequest(nodeId: String, path: String, data: ByteArray): Task<ByteArray> {
        textView.text = String(data)
        return sendRequestToWearable(nodeId, path, "Sent from wearable :)".toByteArray())
    }
}
