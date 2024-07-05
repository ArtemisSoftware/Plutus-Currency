package presentation.home

sealed class HomeEvent {
    data object RefreshRates : HomeEvent()
    data object SwitchCurrencies: HomeEvent()
    data class SaveSourceCurrencyCode(val code: String): HomeEvent()
    data class SaveTargetCurrencyCode(val code: String): HomeEvent()
}