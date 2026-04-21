package com.labin.piggybank.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labin.piggybank.data.CurrencyRepository
import com.labin.piggybank.api.CurrencyResponse
import com.labin.piggybank.ui.model.CurrencyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _selectedBase = MutableStateFlow("RUB")
    val selectedBase: StateFlow<String> = _selectedBase

    val uiState: StateFlow<CurrencyUiState> = _selectedBase
        .flatMapLatest { base ->
            flow {
                emit(repository.getCurrencyRates(base))
            }
                .map { result ->
                    result.fold(
                        onSuccess = { CurrencyUiState.Success(it) },
                        onFailure = { CurrencyUiState.Error(it.localizedMessage ?: "Ошибка") }
                    )
                }
                .onStart { emit(CurrencyUiState.Loading) }
                .catch { e -> emit(CurrencyUiState.Error(e.message ?: "Неизвестная ошибка")) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CurrencyUiState.Loading)

    fun selectBaseCurrency(code: String) {
        _selectedBase.value = code
    }
}