package com.labin.piggybank.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity(
    tableName = "financial_goals",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["targetAccountId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["targetAccountId"])]
)
data class FinancialGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val targetAccountId: Long? = null,
    val name: String,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal = BigDecimal.ZERO,
    val deadline: Date? = null,
    val icon: String? = null
)