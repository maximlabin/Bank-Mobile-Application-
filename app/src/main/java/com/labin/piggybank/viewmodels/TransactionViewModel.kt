package com.labin.piggybank.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labin.piggybank.compose.operation.Category
import com.labin.piggybank.data.HomeRepository
import com.labin.piggybank.data.TransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.hamcrest.Description
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _saveResult = MutableStateFlow<SaveResult?>(null)
    val saveResult: StateFlow<SaveResult?> = _saveResult.asStateFlow()

    fun saveTransaction(
        amount: String,
        description: String,
        category: Category?,
        userId: Long,
        isIncome: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                if (amount.isEmpty() || category == null) {
                    _saveResult.value = SaveResult.Error("Заполните все поля")
                    return@launch
                }

                val amountValue = amount.toDoubleOrNull()
                    ?: throw IllegalArgumentException("Неверная сумма")

                val transaction = TransactionEntity(
                    name = category.name,
                    description= description,
                    amount = amountValue,
                    isIncome = isIncome,
                    categoryName = category.name,
                    timeStamp = System.currentTimeMillis(),
                    userId = userId,
                    id = 0L
                )

                repository.saveTransaction(transaction)
                _saveResult.value = SaveResult.Success

            } catch (e: Exception) {
                println(e)
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