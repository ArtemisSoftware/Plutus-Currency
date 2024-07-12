import android.os.Build

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap

@Composable
actual fun ByteArrayImage(byteArray: ByteArray) {
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    val imageBitmap = bitmap.asImageBitmap()
    Image(bitmap = imageBitmap, contentDescription = null)
}