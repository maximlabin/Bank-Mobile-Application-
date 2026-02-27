package com.labin.piggybank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val description: String,
    val amount: Double,
    val isIncome: Boolean,
    val categoryName: String,
    val timeStamp: Long
)