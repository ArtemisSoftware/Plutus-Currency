package data.mappers

import data.CurrencyEntity
import data.dto.CurrencyDto

fun CurrencyDto.toEntity(): CurrencyEntity{
    return CurrencyEntity().apply {
        code = this@toEntity.code
        value = this@toEntity.value
    }
}

fun CurrencyEntity.toDto(): CurrencyDto{
    return CurrencyDto(
        code = code,
        value = value
    )
}