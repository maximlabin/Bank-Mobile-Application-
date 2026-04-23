package com.labin.piggybank.utilities

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency
import java.util.Locale

fun BigDecimal.toDisplayString(currencyCode: String = "RUB"): String {
    val formatted = this.setScale(2, RoundingMode.HALF_UP).toPlainString()
    val symbol = Currency.getInstance(currencyCode).symbol
    return "$formatted $symbol"
}

fun String.toAmountOrNull(): BigDecimal? =
    replace(',', '.').filter { it.isDigit() || it == '.' }
        .takeIf { it.isNotEmpty() }
        ?.toBigDecimalOrNull()