@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.currencies

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import domain.models.CurrencyCode
import org.jetbrains.compose.resources.painterResource
import presentation.details.DetailsScreen
import ui.theme.headerColor
import ui.theme.textColor

class CurrenciesScreen : Screen {
    @Composable
    override fun Content() {
        var searchQuery by remember { mutableStateOf("") }
        val navigator = LocalNavigator.current

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = headerColor,
                        titleContentColor = Color.White,
                    ),
                    title = {
                        Text("Currencies")
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(size = 99.dp)),
                    value = searchQuery,
                    onValueChange = { query ->

                    },
                    placeholder = {
                        Text(
                            text = "Search here",
                            color = textColor.copy(alpha = 0.38f),
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = textColor.copy(alpha = 0.1f),
                        unfocusedContainerColor = textColor.copy(alpha = 0.1f),
                        disabledContainerColor = textColor.copy(alpha = 0.1f),
                        errorContainerColor = textColor.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = textColor,
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(CurrencyCode.entries.toList()){ code ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navigator?.push(DetailsScreen(code = code))
                                }
                                .padding(all = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row {
                                Image(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(code.flag),
                                    contentDescription = "Currency Flag",
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = code.name,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}