package com.labin.piggybank.data

import com.labin.piggybank.domain.AccountType
import com.labin.piggybank.utilities.CurrencyIdResolver
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject


class AccountRepository @Inject constructor(
    private val dao: AccountDao,
    private val currencyDao: CurrencyDao
) {
    suspend fun createAccount(
        userId: Long, currencyId: Long, name: String, balance: BigDecimal,
        type: AccountType, note: String?, accountNumber: String?,
        bankName: String?, cardColor: String?
    ): Long {
        require(name.isNotBlank()) { "Название счета обязательно" }
        require(balance.signum() >= 0) { "Баланс не может быть отрицательным" }

        val finalCurrencyId = CurrencyIdResolver.resolve(currencyId, currencyDao)

        val now = System.currentTimeMillis()

        val account = Account(
            userId = userId,
            currencyId = finalCurrencyId,
            name = name.trim(),
            balance = balance,
            type = type,
            isArchived = false,
            createdAt = now,
            updatedAt = now,
            note = note?.trim(), accountNumber = accountNumber?.trim(),
            bankName = bankName?.trim(), cardColor = cardColor
        )
        return dao.insert(account)
    }

    fun getAllAccounts(): Flow<List<Account>> = dao.getActiveAccounts(1L)
}