package presentation.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.dto.CurrencyDto
import domain.CurrencyApiService
import domain.RequestState
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

    private var _sourceCurrency: MutableState<RequestState<CurrencyDto>> =
        mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<CurrencyDto>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<CurrencyDto>> =
        mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<CurrencyDto>> = _targetCurrency

    init {
        screenModelScope.launch {
            fetchNewRates()
        }
    }

    fun onTriggerEvent(event: HomeEvent){
        when(event){
            HomeEvent.RefreshRates -> {
                fetchNewRates_()
//                screenModelScope.launch {
//                    fetchNewRates()
//                }
            }
        }
    }

    private fun fetchNewRates_() {
        screenModelScope.launch {
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