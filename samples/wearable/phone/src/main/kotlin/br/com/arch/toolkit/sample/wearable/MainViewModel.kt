package br.com.arch.toolkit.sample.wearable

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream

class MainViewModel {
    fun generateQRCode(): ByteArray {
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
}
