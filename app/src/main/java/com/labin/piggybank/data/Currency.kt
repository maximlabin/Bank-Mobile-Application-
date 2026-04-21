package com.labin.piggybank.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey val id: Long,
    val code: String,
    val name: String,
    val symbol: String,
    val exchangeRate: BigDecimal,
    val baseCurrency: String,
    val lastUpdate: Long = System.currentTimeMillis()
)