package com.labin.piggybank.api

import retrofit2.http.GET
import retrofit2.http.Path

data class CurrencyResponse(
    val result: String,
    val base_code: String,
    val rates: Map<String, Double>
)

interface CurrencyApi {
    @GET("latest/{base_currency}")
    suspend fun getRates(@Path("base_currency") baseCurrency: String): CurrencyResponse
}
