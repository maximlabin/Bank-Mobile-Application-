package com.labin.piggybank.data

import androidx.room.Transaction
import com.labin.piggybank.domain.TransactionType
import com.labin.piggybank.domain.mapper.TransactionMapper
import com.labin.piggybank.utilities.CurrencyIdResolver
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val currencyDao: CurrencyDao
) {
    fun getLastTransactions(
        type: TransactionType,
        start: Long,
        end: Long,
        limit: Int
    ): Flow<List<TransactionEntity>> {
        return transactionDao.getLastTransactions(type, start, end, limit)

    }

    fun getCategoryStats(type: TransactionType, start: Long, end: Long): Flow<List<CategorySummary>> {
        return transactionDao.getAggregatedStats(type, start, end)
    }

    @Transaction
    suspend fun saveTransaction(
        amount: BigDecimal,
        description: String?,
        category: Category?,
        accountId: Long,
        currencyId: Long,
        merchantId: Long?,
        goalId: Long?,
        type: TransactionType,
        destinationAccountId: Long?,
        transactionDate: Date,
    ) {
        val finalCurrencyId = CurrencyIdResolver.resolve(currencyId, currencyDao)
        val sourceAccount = accountDao.getAccountById(accountId)
            ?: throw IllegalStateException("Счёт отправления не найден")

        when (type) {
            TransactionType.TRANSFER -> {
                val destId = destinationAccountId
                    ?: throw IllegalArgumentException("Для перевода необходим счёт зачисления")
                if (destId == accountId) throw IllegalArgumentException("Счета не могут совпадать")

                val destAccount = accountDao.getAccountById(destId)
                    ?: throw IllegalStateException("Счёт зачисления не найден")

                val newBalance = sourceAccount.balance - amount

                if(!sourceAccount.type.allowsNegativeBalance() && newBalance < BigDecimal.ZERO) {
                    throw IllegalArgumentException("Недостаточно средств на счёте ${sourceAccount.name}")
                }

                accountDao.updateBalance(accountId, sourceAccount.balance - amount)
                accountDao.updateBalance(destId, destAccount.balance + amount)
            }
            TransactionType.EXPENSE -> {
                if (category == null) throw IllegalArgumentException("Для расхода необходима категория")
                val newBalance = sourceAccount.balance - amount
                if (!sourceAccount.type.allowsNegativeBalance() && newBalance < BigDecimal.ZERO) {
                    throw IllegalArgumentException("Недостаточно средств на счёте ${sourceAccount.name}")
                }
                accountDao.updateBalance(accountId, newBalance)
            }
            TransactionType.INCOME -> {
                if (category == null) throw IllegalArgumentException("Для дохода необходима категория")
                accountDao.updateBalance(accountId, sourceAccount.balance + amount)
            }
        }

        val transaction = TransactionEntity(
            id = 0,
            accountId = accountId,
            categoryId = category?.id,
            merchantId = merchantId,
            currencyId = finalCurrencyId,
            goalId = goalId,
            amount = amount.setScale(2, RoundingMode.HALF_UP),
            type = type,
            description = description,
            transactionDate = transactionDate,
        )

        transactionDao.insert(transaction)
    }

    fun getBalanceFlow(start: Long, end: Long): Flow<BigDecimal> {
        return transactionDao.getBalanceForPeriod(start, end)
    }

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