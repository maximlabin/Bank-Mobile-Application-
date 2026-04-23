package com.labin.piggybank.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labin.piggybank.data.AccountRepository
import com.labin.piggybank.data.TransactionRepository
import com.labin.piggybank.ui.model.HomeUiState
import com.labin.piggybank.ui.model.PieChartData
import com.labin.piggybank.domain.TransactionType
import com.labin.piggybank.domain.mapper.CategoryMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _selectedType = MutableStateFlow(TransactionType.EXPENSE)

    private val pieDataFlow: Flow<List<PieChartData>> = _selectedType.flatMapLatest { type ->
        transactionRepository.getCategoryStats(type)
            .map { list -> CategoryMapper.toPieChartData(list) }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        transactionRepository.getBalanceFlow(),
        transactionRepository.getLastTransactions(),
        pieDataFlow
    ) { balance, transactions, categories ->
        HomeUiState(
            balance = balance,
            lastTransactions = transactions,
            categories = categories
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun setFilterType(type: TransactionType) {
        _selectedType.value = type
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransactionById(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}