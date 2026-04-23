package com.labin.piggybank.ui.model

import com.labin.piggybank.domain.TransactionType
import java.math.BigDecimal

data class HomeUiState(
    val balance: BigDecimal = BigDecimal.ZERO,
    val lastTransactions: List<Transaction> = emptyList(),
    val cardNumber: Int = 0,
    val categories: List<PieChartData> = emptyList(),
    val selectedType: TransactionType = TransactionType.EXPENSE
)