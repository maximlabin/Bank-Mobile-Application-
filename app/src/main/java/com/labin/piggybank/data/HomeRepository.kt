package com.labin.piggybank.data

import com.labin.piggybank.ui.model.PieChartData
import com.labin.piggybank.ui.model.mapper.CategoryMapper
import com.labin.piggybank.ui.model.mapper.TransactionMapper
import com.labin.piggybank.ui.model.Transaction as UiTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getLastTransactions(): Flow<List<UiTransaction>> =
        transactionDao.getLastTransactions()
            .map { TransactionMapper.toUiList(it) }

    fun getCategoryPieData(): Flow<List<PieChartData>> =
        transactionDao.getCategoryExpenses()
            .map { expenses -> CategoryMapper.toPieChartData(expenses) }

    suspend fun saveTransaction(transaction: TransactionEntity) {
        transactionDao.insert(transaction)
    }
}