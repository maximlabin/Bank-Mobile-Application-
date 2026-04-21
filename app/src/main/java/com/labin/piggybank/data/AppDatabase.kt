package com.labin.piggybank.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Currency::class,
        Account::class,
        Category::class,
        Merchant::class,
        TransactionEntity::class,
        Budget::class,
        FinancialGoal::class,
        Attachment::class
               ],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
//    abstract fun budgetDao(): BudgetDao
//    abstract fun goalDao(): FinancialGoalDao
//    abstract fun merchantDao(): MerchantDao
    abstract fun currencyDao(): CurrencyDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}