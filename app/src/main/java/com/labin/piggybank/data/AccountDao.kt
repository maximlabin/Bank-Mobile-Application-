package com.labin.piggybank.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account): Long

    @Query("SELECT * FROM accounts WHERE user_id = :userId AND is_archived = 0 ORDER BY name ASC")
    fun getActiveAccounts(userId: Long): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE id = :accountId LIMIT 1")
    suspend fun getAccountById(accountId: Long): Account?

    @Query("UPDATE accounts SET balance = :newBalance, updated_at = :timestamp WHERE id = :accountId")
    suspend fun updateBalance(accountId: Long, newBalance: BigDecimal, timestamp: Long = System.currentTimeMillis())
}