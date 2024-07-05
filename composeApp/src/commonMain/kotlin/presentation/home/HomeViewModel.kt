package presentation.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.CurrencyEntity
import data.dto.CurrencyDto
import data.mappers.toDto
import data.mappers.toEntity
import domain.CurrencyApiService
import domain.RequestState
import domain.models.RateStatus
import domain.repository.MongoRepository
import domain.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
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

    private var _allCurrencies = mutableStateListOf<CurrencyEntity>()
    val allCurrencies: List<CurrencyEntity> = _allCurrencies

    init {
        screenModelScope.launch {
            fetchNewRates()
            readSourceCurrency()
            readTargetCurrency()
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
            is HomeEvent.SwitchCurrencies -> {
                switchCurrencies()
            }
        }
    }

    private fun readTargetCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferencesRepository.readTargetCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _targetCurrency.value = RequestState.Success(data = selectedCurrency.toDto())
                } else {
                    _targetCurrency.value = RequestState.Error(message = "Couldn't find the selected currency.")
                }
            }
        }
    }

    private fun readSourceCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferencesRepository.readSourceCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _sourceCurrency.value = RequestState.Success(data = selectedCurrency.toDto())
                } else {
                    _sourceCurrency.value = RequestState.Error(message = "Couldn't find the selected currency.")
                }
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
                    _allCurrencies.addAll(localCache.getSuccessData())
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
                    _allCurrencies.addAll(localCache.getSuccessData())
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
            _allCurrencies.addAll(fetchedData.getSuccessData().map { it.toEntity() })
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

    private fun switchCurrencies() {
        val source = _sourceCurrency.value
        val target = _targetCurrency.value
        _sourceCurrency.value = target
        _targetCurrency.value = source
    }
}