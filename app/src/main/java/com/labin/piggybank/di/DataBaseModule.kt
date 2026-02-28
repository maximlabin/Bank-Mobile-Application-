package com.labin.piggybank.di

import android.content.Context
import androidx.room.Room
import com.labin.piggybank.data.AppDatabase
import com.labin.piggybank.data.TransactionDao
import com.labin.piggybank.utilities.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun provideTransactionDao(appDatabase: AppDatabase): TransactionDao {
        val dao = appDatabase.transactionDao()
        println("✅ TransactionDao created!")
        return dao
    }

}