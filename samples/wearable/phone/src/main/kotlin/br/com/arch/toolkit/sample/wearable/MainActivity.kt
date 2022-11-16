package br.com.arch.toolkit.sample.wearable

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.wearable.extension.listenToWearableRequests
import br.com.arch.toolkit.wearable.extension.registerCapability
import br.com.arch.toolkit.wearable.extension.removeCapability
import br.com.arch.toolkit.wearable.extension.requestDataFromWearable
import br.com.arch.toolkit.wearable.extension.stopListeningToWearableRequests
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.MessageClient

class MainActivity : AppCompatActivity(R.layout.activity_main), MessageClient.RpcService {

    private val buttonTextSample: View by viewProvider(R.id.bt_text_example)
    private val buttonQRCodeSample: View by viewProvider(R.id.bt_qrcode_example)
    private val buttonRequestSample: View by viewProvider(R.id.bt_request_example)

    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buttonTextSample.setOnClickListener { viewModel.sendText(it.context) }
        buttonQRCodeSample.setOnClickListener { viewModel.sendQrCode(it.context) }
        buttonRequestSample.setOnClickListener { viewModel.requestData(it.context) }

        viewModel.textLiveData.observe(this) {
            success(observer = ::onSuccess)
            error(observer = ::onError)
        }

        viewModel.qrCodeLiveData.observe(this) {
            success(observer = ::onSuccess)
            error(observer = ::onError)
        }

        viewModel.requestLiveData.observe(this) {
            success(observer = ::onSuccess)
            error(observer = ::onError)
        }
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

    private val String.toast: Unit
        get() = Toast.makeText(baseContext, this, Toast.LENGTH_SHORT).show()

    private fun onSuccess() = "Sent!".toast

    private fun onError(throwable: Throwable) = "Failed!".toast

    override fun onRequest(nodeId: String, path: String, data: ByteArray): Task<ByteArray>? {
        "Response: ${String(data)}".toast
        return null
    }
}