package com.labin.piggybank.ui.model

data class Transaction(
    val id: String,
    val description: String,
    val amount: Double,
    val isIncome: Boolean
)