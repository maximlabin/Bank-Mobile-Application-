package com.labin.piggybank.ui.model

import com.labin.piggybank.domain.AccountType

data class AccountUIState(
    val name: String = "",
    val balance: String = "0",
    val type: AccountType = AccountType.CASH,
    val currencyId: Long = 1,
    val note: String = "",
    val bankName: String = "",
    val cardColor: String = "#4CAF50",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)