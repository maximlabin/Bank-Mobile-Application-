package com.labin.piggybank.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHomeRepository(
        transactionDao: TransactionDao
    ): HomeRepository {
        return HomeRepository(transactionDao)
    }
}