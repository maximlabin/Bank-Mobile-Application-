package com.labin.piggybank.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.labin.piggybank.data.Category
import com.labin.piggybank.data.TransactionRepository
import com.labin.piggybank.domain.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _saveResult = MutableStateFlow<SaveResult?>(null)
    val saveResult: StateFlow<SaveResult?> = _saveResult.asStateFlow()

    fun saveTransaction(
        amount: String,
        description: String,
        category: Category?,
        accountId: Long,
        currencyId: Long,
        merchantId: Long? = null,
        goalId: Long? = null,
        type: TransactionType,
        destinationAccountId: Long? = null,
        date: Date
    ) = viewModelScope.launch {
        if (amount.isBlank() || (category == null && type != TransactionType.TRANSFER)) {
            _saveResult.value = SaveResult.Error("Заполните сумму и категорию")
            return@launch
        }

        val amountValue = amount.replace(',', '.').trim().toBigDecimalOrNull()
        if (amountValue == null || amountValue <= BigDecimal.ZERO) {
            _saveResult.value = SaveResult.Error("Неверная сумма")
            return@launch
        }

        val maxAllowedDate = Date(System.currentTimeMillis() + 86_400_000L) // +24 часа
        if (date.after(maxAllowedDate)) {
            _saveResult.value = SaveResult.Error("Дата операции не может быть в будущем")
            return@launch
        }

        if (type != TransactionType.TRANSFER && category == null) {
            _saveResult.value = SaveResult.Error("Выберите категорию")
            return@launch
        }

        if (type == TransactionType.TRANSFER) {
            if (destinationAccountId == null) {
                _saveResult.value = SaveResult.Error("Выберите счёт зачисления")
                return@launch
            }
            if (destinationAccountId == accountId) {
                _saveResult.value = SaveResult.Error("Счета отправления и зачисления не могут совпадать")
                return@launch
            }
        }

        if (description.length > 50) {
            _saveResult.value = SaveResult.Error("Описание не должно превышать 50 символов")
            return@launch
        }

        _saveResult.value = null

        try {
            repository.saveTransaction(
                amount = amountValue,
                description = description.ifBlank { null },
                category = category,
                accountId = accountId,
                currencyId = currencyId,
                merchantId = merchantId,
                goalId = goalId,
                type = type,
                destinationAccountId = destinationAccountId,
                transactionDate = date,
            )
            _saveResult.value = SaveResult.Success
        } catch (e: Exception) {
            _saveResult.value = SaveResult.Error(e.localizedMessage ?: "Ошибка сохранения")
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }

    fun formatDate(timestamp: Long): String {
        return java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
            .format(java.util.Date(timestamp))
    }
}

sealed class SaveResult {
    object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}