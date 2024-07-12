import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.painter.BitmapPainter
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGDataProviderCreateWithData
import platform.CoreGraphics.CGImageCreateWithJPEGDataProvider
import platform.CoreGraphics.CGImageCreateWithPNGDataProvider
import platform.Foundation.NSData
import platform.UIKit.UIImage

@Composable
actual fun ByteArrayImage(byteArray: ByteArray) {
    val imageBitmap = byteArray.toImageBitmap()
    Image(bitmap = imageBitmap, contentDescription = null)
}

fun ByteArray.toImageBitmap(): ImageBitmap {
    val data = NSData.create(bytes = this, length = this.size.toULong())
    val uiImage = UIImage.imageWithData(data) ?: error("Could not create image from ByteArray.")
    return uiImage.toImageBitmap()
}

fun UIImage.toImageBitmap(): ImageBitmap {
    val cgImage = this.CGImage!!
    val width = cgImage.width.toInt()
    val height = cgImage.height.toInt()
    val colorSpace = CGColorSpaceCreateDeviceRGB()
    val rawData = ByteArray(width * height * 4)
    val context = CGBitmapContextCreate(
        data = rawData.usePinned { it.addressOf(0) },
        width = width.toULong(),
        height = height.toULong(),
        bitsPerComponent = 8,
        bytesPerRow = width * 4,
        space = colorSpace,
        bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value or CGBitmapInfo.kCGBitmapByteOrder32Big.value
    )
    context.drawImage(CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()), cgImage)
    return ImageBitmap(width, height).also {
        val buffer = it.asAndroidBitmap().copyPixelsFromBuffer(ByteBuffer.wrap(rawData))
    }
}