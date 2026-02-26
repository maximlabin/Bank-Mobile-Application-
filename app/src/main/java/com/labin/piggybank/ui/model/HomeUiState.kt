package com.labin.piggybank.ui.model

data class HomeUiState(
    val balance: Double = 0.0,
    val lastTransactions: List<Transaction> = emptyList(),
    val cardNumber: Int = 0,
    val categories: List<PieChartData> = emptyList()
)