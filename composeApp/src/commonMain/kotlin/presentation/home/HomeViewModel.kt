package presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.CurrencyApiService
import domain.models.RateStatus
import domain.repository.PreferencesRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class HomeViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val api: CurrencyApiService
) : ScreenModel {

    private var _rateStatus = mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    init {
        screenModelScope.launch {
            fetchNewRates()
        }
    }

    fun onTriggerEvent(event: HomeEvent){
        when(event){
            HomeEvent.RefreshRates -> {
                screenModelScope.launch {
                    fetchNewRates()
                }
            }
        }
    }

    private suspend fun fetchNewRates() {
        try {
            api.getLatestExchangeRates()
//            val localCache = mongoDb.readCurrencyData().first()
//            if (localCache.isSuccess()) {
//                if (localCache.getSuccessData().isNotEmpty()) {
//                    println("HomeViewModel: DATABASE IS FULL")
//                    _allCurrencies.clear()
//                    _allCurrencies.addAll(localCache.getSuccessData())
//                    if (!preferences.isDataFresh(Clock.System.now().toEpochMilliseconds())) {
//                        println("HomeViewModel: DATA NOT FRESH")
//                        cacheTheData()
//                    } else {
//                        println("HomeViewModel: DATA IS FRESH")
//                    }
//                } else {
//                    println("HomeViewModel: DATABASE NEEDS DATA")
//                    cacheTheData()
//                }
//            } else if (localCache.isError()) {
//                println("HomeViewModel: ERROR READING LOCAL DATABASE ${localCache.getErrorMessage()}")
//            }
            getRateStatus()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private suspend fun getRateStatus() {
        _rateStatus.value = if (preferencesRepository.isDataFresh(
                currentTimestamp = Clock.System.now().toEpochMilliseconds()
            )
        ) RateStatus.Fresh
        else RateStatus.Stale
    }
}