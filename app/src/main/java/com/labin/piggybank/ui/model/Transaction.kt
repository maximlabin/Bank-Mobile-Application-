package com.labin.piggybank.ui.model

import com.labin.piggybank.domain.TransactionType
import java.math.BigDecimal
import java.util.Date

data class Transaction(
    val id: Long,
    val description: String?,
    val amount: BigDecimal,
    val type: TransactionType,
    val date: Date? = null,
)