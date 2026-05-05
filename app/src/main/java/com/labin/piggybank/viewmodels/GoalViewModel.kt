package com.labin.piggybank.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labin.piggybank.data.FinancialGoal
import com.labin.piggybank.data.FinancialGoalDao
import com.labin.piggybank.data.AccountDao
import com.labin.piggybank.ui.model.GoalsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

data class GoalFormState(
    val name: String = "",
    val targetAmount: String = "",
    val deadlineMillis: Long? = null,
    val targetAccountId: Long? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalDao: FinancialGoalDao,
    private val accountDao: AccountDao
) : ViewModel() {

    private val _formState = MutableStateFlow(GoalFormState())
    val formState: StateFlow<GoalFormState> = _formState.asStateFlow()

    private val _accounts = MutableStateFlow<List<com.labin.piggybank.data.Account>>(emptyList())
    val accounts: StateFlow<List<com.labin.piggybank.data.Account>> = _accounts.asStateFlow()

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState

    init {
        viewModelScope.launch {
            goalDao.getAllGoals().collect { goals ->
                _uiState.value = GoalsUiState(goals = goals, isLoading = false)
            }
        }
        viewModelScope.launch {
            accountDao.getActiveAccounts(1).collect { _accounts.value = it }
        }
    }

    fun deleteGoal(goal: FinancialGoal) {
        viewModelScope.launch {
            goalDao.deleteGoal(goal)
        }
    }

    fun onNameChanged(name: String) = _formState.update { it.copy(name = name, error = null) }
    fun onAmountChanged(amount: String) = _formState.update { it.copy(targetAmount = amount, error = null) }
    fun onDeadlineChanged(millis: Long?) = _formState.update { it.copy(deadlineMillis = millis) }
    fun onAccountSelected(id: Long?) = _formState.update { it.copy(targetAccountId = id) }

    fun saveGoal() {
        val state = _formState.value

        if (state.name.isBlank()) {
            _formState.update { it.copy(error = "Введите название цели") }
            return
        }
        val amount = state.targetAmount.replace(',', '.').trim().toBigDecimalOrNull()
        if (amount == null || amount <= BigDecimal.ZERO) {
            _formState.update { it.copy(error = "Укажите корректную сумму (> 0)") }
            return
        }

        _formState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            try {
                val userId = 1L

                val goal = FinancialGoal(
                    userId = userId,
                    name = state.name.trim(),
                    targetAmount = amount,
                    deadline = state.deadlineMillis?.let { Date(it) },
                    targetAccountId = state.targetAccountId
                )
                goalDao.insertGoal(goal)
                _formState.update { it.copy(isSaving = false, isSuccess = true) }
            } catch (e: Exception) {
                _formState.update { it.copy(isSaving = false, error = "Ошибка: ${e.localizedMessage}") }
            }
        }
    }

    fun resetSuccess() = _formState.update { it.copy(isSuccess = false) }
}