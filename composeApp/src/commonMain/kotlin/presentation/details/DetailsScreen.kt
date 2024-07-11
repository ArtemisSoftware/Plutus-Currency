@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import domain.models.CurrencyCode
import org.jetbrains.compose.resources.painterResource
import presentation.currencies.CurrenciesScreen
import ui.theme.headerColor
import ui.theme.textColor
import util.FontUtil.GetBebasFontFamily

data class DetailsScreen(val code: CurrencyCode): Screen {
    @Composable
    override fun Content() {
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

                IconButton(onClick = {  }){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}