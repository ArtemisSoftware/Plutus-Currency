package presentation.currencies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import domain.models.CurrencyType
import presentation.home.HomeEvent
import presentation.home.HomeViewModel
import presentation.home.composables.CurrencyPickerDialog
import presentation.home.composables.HomeBody
import presentation.home.composables.HomeHeader
import ui.theme.surfaceColor

object CurrenciesTab : Tab {
    @Composable
    override fun Content() {
        Navigator(CurrenciesScreen()){ navigator ->
            SlideTransition(navigator)
        }
    }

    override val options: TabOptions
        @Composable
        get() = remember {
            TabOptions(
                index = 1u,
                title = "Currencies",
                icon = null
            )
        }
}