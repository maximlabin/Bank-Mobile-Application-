package com.labin.piggybank.ui.model

import com.labin.piggybank.api.CurrencyResponse
import com.labin.piggybank.data.Currency

sealed class CurrencyUiState {
    object Loading : CurrencyUiState()
    data class Success(val rates: List<Currency>) : CurrencyUiState()
    data class Error(val message: String) : CurrencyUiState()
}
