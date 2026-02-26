package com.labin.piggybank.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.labin.piggybank.data.Category

@Database(
    entities = [TransactionEntity::class,
        Category::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}