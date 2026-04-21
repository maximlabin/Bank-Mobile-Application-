package com.labin.piggybank

import android.app.Application
import com.labin.piggybank.data.CategoryRepository
import com.labin.piggybank.data.CurrencyRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class PiggyBankApplication : Application() {
    @Inject
    lateinit var categoryRepository: CategoryRepository
    @Inject
    lateinit var currencyRepository: CurrencyRepository

    private val appScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            categoryRepository.initializeDefaults()
            currencyRepository.initializeDefaults()
        }
    }
}