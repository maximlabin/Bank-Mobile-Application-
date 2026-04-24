package com.labin.piggybank.data

import android.util.Log
import androidx.room.Transaction
import com.labin.piggybank.domain.TransactionType
import com.labin.piggybank.domain.mapper.TransactionMapper
import com.labin.piggybank.utilities.CurrencyIdResolver
import com.labin.piggybank.utilities.asTransactionType
import com.labin.piggybank.ui.model.Transaction as UiTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val currencyDao: CurrencyDao
) {
    fun getLastTransactions(): Flow<List<UiTransaction>> =
        transactionDao.getLastTransactions()
            .map { TransactionMapper.toUiList(it) }

    fun getCategoryStats(type: TransactionType): Flow<List<CategorySummary>> {
        return transactionDao.getAggregatedStats(type)
    }

    @Transaction
    suspend fun saveTransaction(
        amount: BigDecimal,
        description: String?,
        category: Category,
        accountId: Long,
        currencyId: Long,
        merchantId: Long?,
        goalId: Long?
    ) {
        val finalCurrencyId = CurrencyIdResolver.resolve(currencyId, currencyDao)

        val account = accountDao.getAccountById(accountId)
            ?: throw IllegalStateException("Счёт не найден")

        val transactionType = category.type.asTransactionType
        val newBalance = when (transactionType) {
            TransactionType.INCOME -> account.balance + amount
            TransactionType.EXPENSE -> account.balance - amount
            TransactionType.TRANSFER -> account.balance
        }

        if (!account.type.allowsNegativeBalance() && newBalance < BigDecimal.ZERO) {
            throw IllegalArgumentException("Недостаточно средств на счёте ${account.name}")
        }

        accountDao.updateBalance(accountId, newBalance)

        val transaction = TransactionEntity(
            id = 0,
            accountId = accountId,
            categoryId = category.id,
            merchantId = merchantId,
            currencyId = finalCurrencyId,
            goalId = goalId,
            amount = amount.setScale(2, RoundingMode.HALF_UP),
            type = transactionType,
            transactionDate = Date(),
            description = description
        )

        transactionDao.insert(transaction)
    }

    fun getBalanceFlow(): Flow<BigDecimal>
        = transactionDao.getTotalBalance()

    @Transaction
    suspend fun deleteTransactionById(id: Long) {
        val transaction = transactionDao.getTransactionById(id)
            ?: throw NoSuchElementException("Транзакция с ID $id не найдена")

        val account = accountDao.getAccountById(transaction.accountId)
            ?: throw IllegalStateException("Счёт не найден")

        val restoredBalance = when (transaction.type) {
            TransactionType.INCOME -> account.balance - transaction.amount
            TransactionType.EXPENSE -> account.balance + transaction.amount
            TransactionType.TRANSFER -> account.balance
        }

        accountDao.updateBalance(transaction.accountId, restoredBalance)
        transactionDao.deleteTransactionByID(id)
    }
}