package br.com.arch.toolkit.sample.wearable

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arch.toolkit.livedata.response.ResponseLiveData
import br.com.arch.toolkit.livedata.response.SwapResponseLiveData
import br.com.arch.toolkit.wearable.extension.requestDataFromWearable
import br.com.arch.toolkit.wearable.extension.sendDataToWearable
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    private val _textLiveData = SwapResponseLiveData<Unit>()
    val textLiveData: ResponseLiveData<Unit>
        get() = _textLiveData

    private val _qrCodeLiveData = SwapResponseLiveData<Unit>()
    val qrCodeLiveData: ResponseLiveData<Unit>
        get() = _qrCodeLiveData

    private val _requestLiveData = SwapResponseLiveData<Unit>()
    val requestLiveData: ResponseLiveData<Unit>
        get() = _qrCodeLiveData


    private fun generateQRCode(): ByteArray {
        val bitMatrix = QRCodeWriter().encode(
            "https://www.google.com",
            BarcodeFormat.QR_CODE,
            800,
            800
        )

        val height = bitMatrix.height
        val width = bitMatrix.width
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun sendText(context: Context) {
        _textLiveData.swapSource(
            context.sendDataToWearable(
                "Message sent from phone :)".toByteArray(),
                Capabilities.TEXT.name,
                viewModelScope
            )
        )
    }

    fun sendQrCode(context: Context) {
        _qrCodeLiveData.swapSource(
            context.sendDataToWearable(generateQRCode(), Capabilities.QRCODE.name, viewModelScope)
        )
    }

    fun requestData(context: Context) {
        _requestLiveData.swapSource(
            context.requestDataFromWearable(
                "Your fun data".toByteArray(),
                Capabilities.REQUEST.name,
                viewModelScope
            )
        )
    }
}
