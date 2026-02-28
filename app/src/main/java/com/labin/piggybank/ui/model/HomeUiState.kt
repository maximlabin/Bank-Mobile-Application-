package com.labin.piggybank.ui.model

import com.labin.piggybank.compose.homepage.TransactionType

data class HomeUiState(
    val balance: Double = 0.0,
    val lastTransactions: List<Transaction> = emptyList(),
    val cardNumber: Int = 0,
    val categories: List<PieChartData> = emptyList(),
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val onTypeSelected: (TransactionType) -> Unit = {}
)