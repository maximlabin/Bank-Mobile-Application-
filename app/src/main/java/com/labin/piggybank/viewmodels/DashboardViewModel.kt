package com.labin.piggybank.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labin.piggybank.data.TransactionRepository
import com.labin.piggybank.di.DateFilterManager
import com.labin.piggybank.ui.model.HomeUiState
import com.labin.piggybank.ui.model.PieChartData
import com.labin.piggybank.domain.TransactionType
import com.labin.piggybank.domain.mapper.CategoryMapper
import com.labin.piggybank.ui.model.Transaction as UiTransaction
import com.labin.piggybank.domain.mapper.TransactionMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import com.labin.piggybank.utilities.toTimestampRange

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val dateFilterManager: DateFilterManager
) : ViewModel() {

    private val _selectedType = MutableStateFlow(TransactionType.EXPENSE)

    private val dateRangeFlow: Flow<Pair<Long, Long>> =
        dateFilterManager.currentFilter
            .map { it.toTimestampRange() }
            .distinctUntilChanged()

    private val pieDataFlow: Flow<List<PieChartData>> =
        combine(_selectedType, dateRangeFlow) { type, range ->
            type to range
        }
            .flatMapLatest { (type, range) ->
                val (start, end) = range
                transactionRepository.getCategoryStats(type, start, end)
                    .map { entities -> CategoryMapper.toPieChartData(entities) }
            }
            .catch {
                emit(emptyList<PieChartData>())
            }

    private val balanceFlow: Flow<BigDecimal> =
        dateRangeFlow.flatMapLatest { (start, end) ->
            transactionRepository.getBalanceFlow(start, end)
        }
            .catch {
                emit(BigDecimal.ZERO)
            }

    private val transactionsFlow: Flow<List<UiTransaction>> =
        _selectedType.flatMapLatest { type ->
            dateRangeFlow.flatMapLatest { (start, end) ->
                transactionRepository.getLastTransactions(type, start, end, limit = 200)
                    .map { entities -> TransactionMapper.toUiList(entities) }
            }
        }
            .catch { emit(emptyList<UiTransaction>()) }

    val uiState: StateFlow<HomeUiState> = combine(
        balanceFlow,
        transactionsFlow,
        pieDataFlow,
    ) { balance, transactions, categories ->
        HomeUiState(
            balance = balance,
            lastTransactions = transactions,
            categories = categories,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        viewModelScope.launch {
            dateFilterManager.currentFilter.collect { filter ->
                Log.d("DashboardVM", "Filter changed: $filter")
            }
        }
    }

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