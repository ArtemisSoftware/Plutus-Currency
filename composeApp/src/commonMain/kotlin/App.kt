import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import di.initializeKoin
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import plutuscurrency.composeapp.generated.resources.Res
import plutuscurrency.composeapp.generated.resources.compose_multiplatform
import presentation.home.HomeScreen

@Composable
@Preview
fun App() {

    initializeKoin()

    MaterialTheme {
        Navigator(HomeScreen()){ navigator ->
            SlideTransition(navigator)
        }
    }
}