package com.labin.piggybank.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labin.piggybank.data.Category
import com.labin.piggybank.data.CategoryRepository
import com.labin.piggybank.domain.CategoryType
import com.labin.piggybank.domain.TransactionType
import com.labin.piggybank.ui.model.CategoryEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.String

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryEditorUiState())
    val uiState: StateFlow<CategoryEditorUiState> = _uiState

    val categories: StateFlow<List<Category>> = _uiState
        .map { it.type }
        .distinctUntilChanged()
        .flatMapLatest { type ->
            repository.getCategoriesByType(type)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onNameChange(name: String) = _uiState.update { it.copy(name = name) }
    fun onColorSelect(color: String) = _uiState.update { it.copy(selectedColor = color) }
    fun onIconSelect(iconId: Int?) = _uiState.update { it.copy(selectedIconResId = iconId) }
    fun onTypeSelect(type: CategoryType) = _uiState.update { it.copy(type = type) }

    fun saveCategory() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.name.trim().isEmpty()) {
                _uiState.update { it.copy(error = "Введите название категории") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.createUserCategory(
                    name = state.name.trim(),
                    color = state.selectedColor,
                    type = state.type,
                    iconResId = state.selectedIconResId,
                )
                _uiState.update { it.copy(isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ошибка: ${e.localizedMessage ?: "Неизвестная ошибка"}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun resetSuccess() = _uiState.update { it.copy(isSuccess = false, error = null) }
}