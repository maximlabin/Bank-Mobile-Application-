package com.labin.piggybank.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labin.piggybank.data.Category
import com.labin.piggybank.data.CurrencyDao
import com.labin.piggybank.data.TransactionEntity
import com.labin.piggybank.data.TransactionRepository
import com.labin.piggybank.domain.TransactionType
import com.labin.piggybank.utilities.CurrencyIdResolver
import com.labin.piggybank.utilities.asTransactionType
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
    private val repository: TransactionRepository,
    private val currencyDao: CurrencyDao,

) : ViewModel() {
    private val _saveResult = MutableStateFlow<SaveResult?>(null)
    val saveResult: StateFlow<SaveResult?> = _saveResult.asStateFlow()

    fun saveTransaction(
        amount: String,
        description: String,
        category: Category?,
        accountId: Long,
        currencyId: Long,
        userId: Long,
        isIncome: Boolean = false,
        merchantId: Long? = null,
        goalId: Long? = null
    ) {
        viewModelScope.launch {
            try {
                _saveResult.value = null

                if (amount.trim().isEmpty() || category == null) {
                    _saveResult.value = SaveResult.Error("Заполните все поля")
                    return@launch
                }

                val sum = amount.replace(',', '.').trim()
                val amountValue = sum.toBigDecimalOrNull()

                if (amountValue == null) {
                    _saveResult.value = SaveResult.Error("Неверная сумма")
                    return@launch
                }

                if (amountValue <= BigDecimal.ZERO) {
                    _saveResult.value = SaveResult.Error("Сумма должна быть больше 0")
                    return@launch
                }

                if (description.length >= 50) {
                    _saveResult.value = SaveResult.Error("Описание не должно превышать 50 символов")
                    return@launch
                }
                val transactionType = category?.type?.asTransactionType
                    ?: TransactionType.EXPENSE

                try {
                    val finalCurrencyId = CurrencyIdResolver.resolve(currencyId, currencyDao)
                    val transaction = TransactionEntity(
                        accountId = accountId,
                        categoryId = category.id,
                        merchantId = merchantId,
                        currencyId = finalCurrencyId,
                        goalId = goalId,
                        amount = amountValue,
                        type = transactionType,
                        transactionDate = Date(),
                        description = description.ifEmpty { null }
                    )

                    repository.saveTransaction(transaction)
                    _saveResult.value = SaveResult.Success

                } catch (e: Exception) {
                    println("Database error: ${e.message}")
                    _saveResult.value = SaveResult.Error("Не удалось сохранить в БД: ${e.message}")
                }

            } catch (e: Exception) {
                _saveResult.value = SaveResult.Error(e.message ?: "Ошибка")
            }
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }
}

sealed class SaveResult {
    object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}