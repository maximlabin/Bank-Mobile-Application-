package com.labin.piggybank.ui.model

import com.labin.piggybank.data.FinancialGoal

data class GoalsUiState(
    val goals: List<FinancialGoal> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
