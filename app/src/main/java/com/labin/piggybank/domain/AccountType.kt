package com.labin.piggybank.domain

enum class AccountType {
    CASH,
    BANK_ACCOUNT,
    CREDIT_CARD,
    DEBIT_CARD,
    INVESTMENT;

    fun allowsNegativeBalance(): Boolean = when (this) {
        CREDIT_CARD -> true
        BANK_ACCOUNT -> false
        else -> false
    }
}