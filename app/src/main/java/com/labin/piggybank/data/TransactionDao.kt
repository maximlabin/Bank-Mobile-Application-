package com.labin.piggybank.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.labin.piggybank.domain.TransactionType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface TransactionDao {

    @Query("""
    SELECT * FROM transactions 
    WHERE type = :type 
      AND transaction_date BETWEEN :startDate AND :endDate
    ORDER BY transaction_date DESC 
    LIMIT :limit
""")
    fun getLastTransactions(
        type: TransactionType,
        startDate: Long,
        endDate: Long,
        limit: Int = 200
    ): Flow<List<TransactionEntity>>

    @Query("""
        SELECT 
            c.id AS categoryId,
            c.name AS categoryName,
            c.colorHex,
            SUM(t.amount) AS totalAmount
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.type = :type
          AND t.transaction_date BETWEEN :startDate AND :endDate
        GROUP BY t.category_id
        ORDER BY totalAmount DESC
    """)
    fun getAggregatedStats(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<List<CategorySummary>>


    @Query("""
        SELECT SUM(
            CASE 
                WHEN t.type = 'EXPENSE' THEN -t.amount
                WHEN t.type = 'INCOME' THEN t.amount
                ELSE 0
            END
        )
        FROM transactions t
        WHERE t.transaction_date BETWEEN :startDate AND :endDate
    """)
    fun getBalanceForPeriod(startDate: Long, endDate: Long): Flow<BigDecimal>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionByID(id: Long): Int

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?
}

data class CategorySummary(
    val categoryId: Long,
    val categoryName: String,
    val colorHex: String,
    val totalAmount: Double
)