package com.labin.piggybank.ui.model

import com.labin.piggybank.domain.TransactionType
import java.math.BigDecimal

data class Transaction(
    val id: Long,
    val description: String?,
    val amount: BigDecimal,
    val type: TransactionType,
)