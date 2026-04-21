package com.labin.piggybank.data

import com.labin.piggybank.api.CurrencyApi
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton
import com.labin.piggybank.utilities.getCurrencyName
import kotlin.let


@Singleton
class RetrofitProvider @Inject constructor() {
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://open.er-api.com/v6/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun provideCurrencyApi(): CurrencyApi = provideRetrofit().create(CurrencyApi::class.java)
}

@Singleton
class CurrencyRepository @Inject constructor(
    private val api: CurrencyApi,
    private val dao: CurrencyDao
) {

    private val currencySymbols = mapOf(
        "RUB" to "₽", "USD" to "$", "EUR" to "€", "CNY" to "¥", "KZT" to "₸",
        "GBP" to "£", "JPY" to "¥", "CHF" to "Fr", "CAD" to "C$", "AUD" to "A$"
    )

    fun getCurrencyRatesFlow(baseCurrency: String = "RUB"): Flow<List<Currency>> {
        return dao.getCurrenciesByBase(baseCurrency)
    }

    suspend fun refreshFromNetwork(baseCurrency: String = "RUB"): Result<List<Currency>> {
        return try {
            val response = api.getRates(baseCurrency)

            val currencies = response.rates.mapNotNull { (code, rate) ->
                if (code == baseCurrency) return@mapNotNull null

                Currency(
                    id = code.hashCode().toLong(),
                    code = code,
                    name = getCurrencyName(code),
                    symbol = currencySymbols[code] ?: code,
                    exchangeRate = BigDecimal.valueOf(rate),
                    baseCurrency = baseCurrency,
                    lastUpdate = System.currentTimeMillis()
                )
            }

            dao.upsertCurrencies(currencies)

            Result.success(currencies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun initializeDefaults() {
        if (dao.getCount() > 0) return
        val defaults = listOf("RUB", "USD", "EUR", "CNY", "KZT", "GBP", "JPY", "CHF").map { code ->
            Currency(
                id = code.hashCode().toLong(),
                code = code,
                name = getCurrencyName(code),
                symbol = currencySymbols[code] ?: code,
                exchangeRate = if (code == "RUB") BigDecimal.ONE else BigDecimal("0.00"),
                baseCurrency = "RUB",
                lastUpdate = 0L
            )
        }
        dao.upsertCurrencies(defaults)
    }
    suspend fun getCurrencyRates(baseCurrency: String = "RUB"): Result<List<Currency>> {
        return refreshFromNetwork(baseCurrency)
    }


}