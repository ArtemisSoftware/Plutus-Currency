package data.dto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyDto(
    @SerialName("code")
    val code: String,
    @SerialName("value")
    val value: Double
)
