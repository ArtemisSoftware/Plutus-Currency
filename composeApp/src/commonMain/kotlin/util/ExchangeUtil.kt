package util

object ExchangeUtil {

    fun calculateExchangeRate(source: Double, target: Double): Double {
        return target / source
    }

    fun convert(amount: Double, exchangeRate: Double): Double {
        return amount * exchangeRate
    }
}