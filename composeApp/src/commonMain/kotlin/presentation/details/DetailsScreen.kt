@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.details

import ByteArrayImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import domain.models.CurrencyCode
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import presentation.currencies.CurrenciesScreen
import ui.theme.headerColor
import ui.theme.textColor
import util.FontUtil.GetBebasFontFamily

data class DetailsScreen(val code: CurrencyCode): Screen {
    @Composable
    override fun Content() {

        var picture by remember {
            mutableStateOf(ByteArray(0))
        }
        var showCamera by remember { mutableStateOf(false) }
        var hasPermissionAlready by remember { mutableStateOf(false) }
        var isPermissionChecked by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()

        val singleImagePicker = rememberImagePickerLauncher(
            selectionMode = SelectionMode.Single,
            scope = scope,
            onResult = { byteArrays ->
                byteArrays.firstOrNull()?.let { array: ByteArray ->
                    // Process the selected images' ByteArrays.
                    picture = array
                    println(array)
                }
            }
        )

        val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
        val permissionsController: PermissionsController =
            remember(factory) { factory.createPermissionsController() }

        BindEffect(permissionsController)

        LaunchedEffect(Unit) {
            hasPermissionAlready = permissionsController.isPermissionGranted(Permission.CAMERA)
            isPermissionChecked = true
        }

        val navigator = LocalNavigator.current

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = headerColor,
                        titleContentColor = Color.White,
                    ),
                    title = {
                        Text("Detail")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator?.pop() }){
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                if(showCamera) {
                    PeekabooCameraView(
                        modifier = Modifier.fillMaxSize(),
                        onBack = { showCamera = false },
                        onFrame = {},
                        onCapture = { byteArray ->
                            byteArray?.let {
                                picture = it
                            }
                            showCamera = false
                        },
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {  }
                        .padding(all = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Image(
                            modifier = Modifier.size(36.dp),
                            painter = painterResource(code.flag),
                            contentDescription = "Currency Flag",
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = code.country,
                            color = textColor,
                            fontSize = 32.sp,
                            fontFamily = GetBebasFontFamily(),
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(onClick = {
//                    scope.launch {
//                        permissionsController.providePermission(Permission.CAMERA)
//                    }
                    showCamera = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = ""
                        )
                    }
                    IconButton(onClick = {
                        singleImagePicker.launch()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = ""
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    if(picture.isNotEmpty()) {
                        ByteArrayImage(picture)
                    }
                }
            }
        }
    }
}

@Composable
internal fun PeekabooCameraView(
    modifier: Modifier = Modifier,
    onCapture: (ByteArray?) -> Unit,
    onFrame: (ByteArray) -> Unit,
    onBack: () -> Unit,
) {
    val state = rememberPeekabooCameraState(onCapture = onCapture)
    Box(modifier = modifier) {
        PeekabooCamera(
            state = state,
            modifier = Modifier.fillMaxSize(),
            permissionDeniedContent = {
                PermissionDenied(
                    modifier = Modifier.fillMaxSize(),
                )
            },
        )
        CameraOverlay(
            isCapturing = state.isCapturing,
            onBack = onBack,
            onCapture = { state.capture() },
            onConvert = { state.toggleCamera() },
            modifier = Modifier.fillMaxSize(),
        )
    }
}


@Composable
fun PermissionDenied(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Please grant the camera permission!",
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CameraOverlay(
    isCapturing: Boolean,
    onCapture: () -> Unit,
    onConvert: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Back Button",
                tint = Color.White,
            )
        }
        if (isCapturing) {
            CircularProgressIndicator(
                modifier =
                Modifier
                    .size(80.dp)
                    .align(Alignment.Center),
                color = Color.White.copy(alpha = 0.7f),
                strokeWidth = 8.dp,
            )
        }

        IconButton(
            onClick = onCapture,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = 16.dp, start = 16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Back Button",
                tint = Color.White,
            )
        }

//        CircularButton(
//            imageVector = IconCached,
//            modifier =
//            Modifier
//                .align(Alignment.BottomEnd)
//                .padding(bottom = 16.dp, end = 16.dp),
//            onClick = onConvert,
//        )
//        InstagramCameraButton(
//            modifier =
//            Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 16.dp),
//            onClick = onCapture,
//        )
    }
}