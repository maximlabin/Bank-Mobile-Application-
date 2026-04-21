package com.labin.piggybank.utilities

import com.labin.piggybank.R
import java.util.Currency
import java.util.Locale


/**
* Constants used throughout the app.
**/

const val DATABASE_NAME = "piggybank-db"

val PRESET_COLORS = listOf(
    "#FF9800", "#4CAF50", "#2196F3", "#9C27B0", "#E91E63",
    "#00BCD4", "#795548", "#607D8B", "#F44336", "#8BC34A"
)

val PRESET_ICONS = listOf(
    R.drawable.ic_food, R.drawable.ic_home, R.drawable.ic_transport,
    R.drawable.ic_entertainment, R.drawable.ic_health, R.drawable.ic_shopping,
    R.drawable.ic_salary, R.drawable.ic_wallet, R.drawable.ic_education
)

fun Double.toFormattedBalance(): String {
    val format = java.text.DecimalFormat("#,##0")
    return format.format(this)
}

fun getCurrencyName(code: String): String = runCatching {
    Currency.getInstance(code).getDisplayName(Locale("ru", "RU"))
}.getOrDefault(code)