package com.labin.piggybank.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 10")
    fun getLastTransactions(): Flow<List<TransactionEntity>>

    @Query("""
        SELECT 
            categoryName AS category,
            SUM(amount) AS totalAmount
        FROM transactions 
        WHERE IsIncome = 0
        GROUP BY categoryName
        ORDER BY totalAmount ASC
    """)
    fun getCategoryExpenses(): Flow<List<CategoryExpense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)
}

data class CategoryExpense(
    val category: String,
    val totalAmount: Double
)