package com.labin.piggybank.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labin.piggybank.compose.homepage.TransactionItem
import com.labin.piggybank.compose.homepage.TransactionType
import com.labin.piggybank.compose.operation.Category
import com.labin.piggybank.data.HomeRepository
import com.labin.piggybank.ui.model.HomeUiState
import com.labin.piggybank.ui.model.PieChartData
import com.labin.piggybank.ui.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: HomeRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                flowOf(12500.75),
                flowOf(2344),
                repository.getLastTransactions(),
                repository.getCategoryPieData()
            ) { balance, cardNumber, transactions, categories ->
                HomeUiState(
                    balance = balance,
                    cardNumber = cardNumber,
                    lastTransactions = transactions,
                    categories = categories
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onTypeSelected(newType: TransactionType) {
        _uiState.update { it.copy(selectedType = newType) }
    }
}