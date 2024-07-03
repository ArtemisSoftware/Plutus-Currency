package data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RateDto (
    @SerialName("meta")
    val metaData: MetaDataDto,
    @SerialName("data")
    val data: Map<String, CurrencyDto>
)