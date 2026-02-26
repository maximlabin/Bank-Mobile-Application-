package com.labin.piggybank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val description: String,
    val amount: Double,
    val isIncome: Boolean,
    val categoryName: String,
    val timeStamp: Long
)