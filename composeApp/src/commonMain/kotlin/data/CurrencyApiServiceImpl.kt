package data

import data.dto.CurrencyDto
import data.dto.RateDto
import domain.CurrencyApiService
import domain.RequestState
import domain.models.CurrencyCode
import domain.repository.PreferencesRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds


class CurrencyApiServiceImpl(
    private val preferencesRepository: PreferencesRepository
): CurrencyApiService {

    companion object {
        const val ENDPOINT = "https://api.currencyapi.com/v3/latest"
        const val API_KEY = "cur_live_33dYO48f6GYYsoY8Yjyf4SDLFe0iNUiprPulfEbU"
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15.seconds.inWholeMilliseconds
        }
        install(DefaultRequest) {
            headers {
                append("apikey", API_KEY)
            }
        }
    }

    override suspend fun getLatestExchangeRates(): RequestState<List<CurrencyDto>> {
        return try {
            val response = httpClient.get(ENDPOINT)

            if (response.status.value == 200) {
                val apiResponse = response.body<RateDto>()
                println("API RESPONSE: ${response.body<String>()}")

                val availableCurrencyCodes = apiResponse.data.keys
                    .filter {
                        CurrencyCode.entries
                            .map { code -> code.name }
                            .toSet()
                            .contains(it)
                    }

                val availableCurrencies = apiResponse.data.values
                    .filter { currency ->
                        availableCurrencyCodes.contains(currency.code)
                    }

                // Persist a timestamp
                val lastUpdated = apiResponse.metaData.lastUpdatedAt
                preferencesRepository.saveLastUpdated(lastUpdated)

                RequestState.Success(data = availableCurrencies)
            } else {
                RequestState.Error(message = "HTTP Error Code: ${response.status}")
            }
        } catch (e: Exception) {
            RequestState.Error(message = e.message.toString())
        }
    }
}