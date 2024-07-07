package util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import plutuscurrency.composeapp.generated.resources.Res
import plutuscurrency.composeapp.generated.resources.bebas_nue_regular

object FontUtil {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun GetBebasFontFamily() = FontFamily(Font(Res.font.bebas_nue_regular))
}