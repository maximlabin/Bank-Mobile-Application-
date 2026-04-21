package com.labin.piggybank.utilities

import com.labin.piggybank.domain.CategoryType
import com.labin.piggybank.domain.TransactionType

/**
 * Преобразует тип категории в тип транзакции.
 */
val CategoryType.asTransactionType: TransactionType
    get() = when (this) {
        CategoryType.INCOME -> TransactionType.INCOME
        CategoryType.EXPENSE -> TransactionType.EXPENSE
    }