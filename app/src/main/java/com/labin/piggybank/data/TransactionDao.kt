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
    @Query("SELECT * FROM transactions ORDER BY transaction_date DESC LIMIT 10")
    fun getLastTransactions(): Flow<List<TransactionEntity>>

    @Query("""
    SELECT 
        c.id AS categoryId,
        c.name AS categoryName,
        c.colorHex,
        SUM(t.amount) AS totalAmount
    FROM transactions t
    INNER JOIN categories c ON t.category_id = c.id
    WHERE t.type = :type
    GROUP BY t.category_id
    ORDER BY totalAmount DESC
    """)
    fun getAggregatedStats(type: TransactionType): Flow<List<CategorySummary>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionByID(id: Long) : Int

    @Query("SELECT SUM(balance) FROM accounts")
    fun getTotalBalance(): Flow<BigDecimal>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?

}

data class CategorySummary(
    val categoryId: Long,
    val categoryName: String,
    val colorHex: String,
    val totalAmount: Double
)