package data.models

data class Rate (
    val metaData: MetaData,
    val data: Map<String, Currency>
)