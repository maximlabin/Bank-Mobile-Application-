package com.labin.piggybank.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface FinancialGoalDao {
    @Query("SELECT * FROM financial_goals ORDER BY deadline ASC")
    fun getAllGoals(): Flow<List<FinancialGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: FinancialGoal)

    @Query("UPDATE financial_goals SET currentAmount = currentAmount + :amount WHERE id = :goalId")
    suspend fun addToGoal(goalId: Long, amount: BigDecimal)

    @Delete
    suspend fun deleteGoal(goal: FinancialGoal)
}