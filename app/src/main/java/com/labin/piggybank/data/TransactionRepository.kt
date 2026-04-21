package com.labin.piggybank.data

import com.labin.piggybank.domain.TransactionType
import com.labin.piggybank.domain.mapper.TransactionMapper
import com.labin.piggybank.ui.model.Transaction as UiTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getLastTransactions(): Flow<List<UiTransaction>> =
        transactionDao.getLastTransactions()
            .map { TransactionMapper.toUiList(it) }

    fun getCategoryStats(type: TransactionType): Flow<List<CategorySummary>> {
        return transactionDao.getAggregatedStats(type)
    }

    suspend fun saveTransaction(transaction: TransactionEntity) {
        transactionDao.insert(transaction)
    }

    fun getBalanceFlow(): Flow<Double> = transactionDao.getTotalBalance()

    suspend fun deleteTransactionById(id: Long) {
        val deletedRows = transactionDao.deleteTransactionByID(id);
        if (deletedRows == 0) {
            throw NoSuchElementException("Транзакция с ID $id не найдена в базе данных")
        }
    }
}