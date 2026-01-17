package com.labin.piggybank.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PieChartData(
    val amount: Double,
    val color: Color,
    val label: String
)

data class HomeUiState(
    val balance: Double = 0.0,
    val lastTransactions: List<Transaction> = emptyList(),
    val cardNumber: Int = 0,
    val categories: List<PieChartData> = emptyList()
)

data class Transaction(
    val id: String,
    val description: String,
    val amount: Double,
    val isIncome: Boolean
)

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadData()
    }

    val mockCategories = listOf(
        PieChartData(40.00, Color(0xFF4CAF50), "Категория A"),
        PieChartData(25.79, Color(0xFF2196F3), "Категория B"),
        PieChartData(20.89, Color(0xFFFFC107), "Категория C"),
        PieChartData(30.89, Color(0xFFF44336), "Категория D")
    )

    private fun loadData() {
        // Здесь загружай данные из Repository
        viewModelScope.launch {
            val mockData = HomeUiState(
                balance = 12500.75,
                cardNumber = 2344,
                lastTransactions = listOf(
                    Transaction("1", "Зарплата", 50000.0, true),
                    Transaction("2", "Продукты", -2500.0, false),
                    Transaction("3", "Подписка", -499.0, false)
                ),
                categories = mockCategories
            )


            _uiState.value = mockData
        }
    }
}