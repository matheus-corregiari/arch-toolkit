package br.com.arch.toolkit.sample.wearable

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.wearable.extension.listenToWearableMessages
import br.com.arch.toolkit.wearable.extension.registerCapability
import br.com.arch.toolkit.wearable.extension.removeCapability
import br.com.arch.toolkit.wearable.extension.stopListeningToWearableMessages
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent

class QRCodeActivity : Activity(), MessageClient.OnMessageReceivedListener {

    private val imageView: ImageView by viewProvider(R.id.image_view)

    private val ByteArray.asBitmap: Bitmap
        get() = BitmapFactory.decodeByteArray(this, 0, size)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)
        imageView.doOnPreDraw { it.updateLayoutParams { height = width } }
    }

    override fun onResume() {
        super.onResume()
        registerCapability(Capabilities.QRCODE.name)
        listenToWearableMessages(this)
    }

    override fun onPause() {
        stopListeningToWearableMessages(this)
        removeCapability(Capabilities.QRCODE.name)
        super.onPause()
    }

    override fun onMessageReceived(event: MessageEvent) {
        if (event.path.equals(Capabilities.QRCODE.name, true)) {
            imageView.setImageBitmap(event.data.asBitmap)
            imageView.visibility = View.VISIBLE
        }
    }
}
