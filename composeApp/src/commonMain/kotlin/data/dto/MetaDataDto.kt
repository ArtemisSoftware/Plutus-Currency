package data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetaDataDto(
    @SerialName("last_updated_at")
    val lastUpdatedAt: String
)
