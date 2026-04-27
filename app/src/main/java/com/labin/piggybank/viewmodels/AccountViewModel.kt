package com.labin.piggybank.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labin.piggybank.data.Account
import com.labin.piggybank.data.AccountRepository
import com.labin.piggybank.domain.AccountType
import com.labin.piggybank.ui.model.AccountUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AccountUIState())
    val state: StateFlow<AccountUIState> = _state

    val accounts: StateFlow<List<Account>> = repository.getAllAccounts()
        .catch { e ->
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateName(v: String) = _state.update { it.copy(name = v) }
    fun updateBalance(v: String) = _state.update { it.copy(balance = v) }
    fun updateType(v: AccountType) = _state.update { it.copy(type = v) }
    fun updateNote(v: String) = _state.update { it.copy(note = v) }
    fun updateBank(v: String) = _state.update { it.copy(bankName = v) }

    fun resetSuccess() {
        _state.update { it.copy(isSuccess = false, error = null) }
    }

    fun save(userId: Long) = viewModelScope.launch {
        val s = _state.value
        if (s.name.isBlank()) { _state.update { it.copy(error = "Введите название") }; return@launch }

        val balance = runCatching { BigDecimal(s.balance) }.getOrNull()
        if (balance == null || balance.signum() < 0) {
            _state.update { it.copy(error = "Некорректная сумма") }; return@launch
        }

        _state.update { it.copy(isLoading = true, error = null) }
        try {
            repository.createAccount(
                userId = userId,
                currencyId = s.currencyId,
                name = s.name,
                balance = balance,
                type = s.type,
                note = s.note,
                accountNumber = null,
                bankName = s.bankName,
                cardColor = s.cardColor
            )
            _state.update { it.copy(isSuccess = true) }
        } catch (e: Exception) {
            _state.update { it.copy(error = "Ошибка: ${e.localizedMessage}") }
        } finally {
            _state.update { it.copy(isLoading = false) }
        }
    }
}