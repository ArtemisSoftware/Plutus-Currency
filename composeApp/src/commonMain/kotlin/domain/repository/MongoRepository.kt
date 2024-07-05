package domain.repository

import data.CurrencyEntity
import domain.RequestState
import kotlinx.coroutines.flow.Flow

interface MongoRepository {
    fun configureTheRealm()
    suspend fun insertCurrencyData(currency: CurrencyEntity)
    fun readCurrencyData(): Flow<RequestState<List<CurrencyEntity>>>
    suspend fun cleanUp()
}