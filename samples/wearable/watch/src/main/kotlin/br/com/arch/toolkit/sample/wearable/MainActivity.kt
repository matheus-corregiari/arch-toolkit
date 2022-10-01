package br.com.arch.toolkit.sample.wearable

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import br.com.arch.toolkit.delegate.viewProvider

class MainActivity : Activity() {

    private val buttonTextSample: View by viewProvider(R.id.button_text)
    private val buttonQRCodeSample: View by viewProvider(R.id.button_qr_code)
    private val buttonRequestSample: View by viewProvider(R.id.button_request)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        buttonTextSample.setOnClickListener {
            startActivity(Intent(this, TextActivity::class.java))
        }

        buttonQRCodeSample.setOnClickListener {
            startActivity(Intent(this, QRCodeActivity::class.java))
        }

        buttonRequestSample.setOnClickListener {
            startActivity(Intent(this, RequestActivity::class.java))
        }
    }
}
