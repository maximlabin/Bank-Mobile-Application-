package com.labin.piggybank.di

import com.labin.piggybank.api.CurrencyApi
import com.labin.piggybank.data.RetrofitProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitProvider(): RetrofitProvider = RetrofitProvider()

    @Provides
    @Singleton
    fun provideCurrencyApi(provider: RetrofitProvider): CurrencyApi = provider.provideCurrencyApi()


}