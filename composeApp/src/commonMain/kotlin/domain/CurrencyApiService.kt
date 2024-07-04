package domain

import data.dto.CurrencyDto
import domain.models.Currency

interface CurrencyApiService {

    suspend fun getLatestExchangeRates(): RequestState<List<CurrencyDto>>
}