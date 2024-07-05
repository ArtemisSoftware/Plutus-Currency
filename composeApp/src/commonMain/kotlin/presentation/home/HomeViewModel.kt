package presentation.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.dto.CurrencyDto
import data.mappers.toDto
import data.mappers.toEntity
import domain.CurrencyApiService
import domain.RequestState
import domain.models.RateStatus
import domain.repository.MongoRepository
import domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlin.time.Duration.Companion.days

class HomeViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val mongoRepository: MongoRepository,
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

    private var _allCurrencies = mutableStateListOf<CurrencyDto>()
    val allCurrencies: List<CurrencyDto> = _allCurrencies

    init {
        screenModelScope.launch {
            fetchNewRates()
        }
    }

    val day = Clock.System.now().toEpochMilliseconds()
    private fun dayNowForBackEnd(): Long {
        // Get the current moment
        val now = Clock.System.now()

        // Subtract one day
        val oneDayBefore = now - 1.days
        //val oneDayBefore = now.minus(value = 1, unit = DateTimeUnit.DAY)

        // Get the epoch milliseconds
        return oneDayBefore.toEpochMilliseconds()
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
            val localCache = mongoRepository.readCurrencyData().first()
            if (localCache.isSuccess()) {
                if (localCache.getSuccessData().isNotEmpty()) {
                    println("HomeViewModel: DATABASE IS FULL")
                    _allCurrencies.clear()
                    _allCurrencies.addAll(localCache.getSuccessData().map { it.toDto() })
                    if (!preferencesRepository.isDataFresh(dayNowForBackEnd())) {
                        println("HomeViewModel: DATA NOT FRESH")
                        cacheTheData()
                    } else {
                        println("HomeViewModel: DATA IS FRESH")
                    }
                } else {
                    println("HomeViewModel: DATABASE NEEDS DATA")
                    cacheTheData()
                }
            } else if (localCache.isError()) {
                println("HomeViewModel: ERROR READING LOCAL DATABASE ${localCache.getErrorMessage()}")
            }
            getRateStatus()
        } catch (e: Exception) {
            println(e.message)
        }
        }
    }

    private suspend fun fetchNewRates() {
        try {
            val localCache = mongoRepository.readCurrencyData().first()
            if (localCache.isSuccess()) {
                if (localCache.getSuccessData().isNotEmpty()) {
                    println("HomeViewModel: DATABASE IS FULL")
                    _allCurrencies.clear()
                    _allCurrencies.addAll(localCache.getSuccessData().map { it.toDto() })
                    if (!preferencesRepository.isDataFresh(dayNowForBackEnd())) {
                        println("HomeViewModel: DATA NOT FRESH")
                        cacheTheData()
                    } else {
                        println("HomeViewModel: DATA IS FRESH")
                    }
                } else {
                    println("HomeViewModel: DATABASE NEEDS DATA")
                    cacheTheData()
                }
            } else if (localCache.isError()) {
                println("HomeViewModel: ERROR READING LOCAL DATABASE ${localCache.getErrorMessage()}")
            }
            getRateStatus()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private suspend fun cacheTheData() {
        val fetchedData = api.getLatestExchangeRates()
        if (fetchedData.isSuccess()) {
            mongoRepository.cleanUp()
            fetchedData.getSuccessData().forEach {
                println("HomeViewModel: ADDING ${it.code}")
                mongoRepository.insertCurrencyData(it.toEntity())
            }
            println("HomeViewModel: UPDATING _allCurrencies")
            _allCurrencies.clear()
            _allCurrencies.addAll(fetchedData.getSuccessData())
        } else if (fetchedData.isError()) {
            println("HomeViewModel: FETCHING FAILED ${fetchedData.getErrorMessage()}")
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