import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import di.initializeKoin
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.currencies.CurrenciesScreen
import presentation.currencies.CurrenciesTab
import presentation.home.HomeScreen

@Composable
@Preview
fun App() {

    initializeKoin()

    MaterialTheme {
//        Navigator(HomeScreen()){ navigator ->
//            SlideTransition(navigator)
//        }

        TabNavigator(HomeScreen){
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(HomeScreen)
                        TabNavigationItem(CurrenciesTab)
                    }
                }
            ){ innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                ) {
                    CurrentTab()
                }
            }
        }

    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        label = { Text(tab.options.title) },
        icon = {}
    )

}
