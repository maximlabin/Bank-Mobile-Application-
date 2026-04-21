package com.labin.piggybank.ui.model

import com.labin.piggybank.domain.CategoryType
import com.labin.piggybank.domain.TransactionType

data class CategoryEditorUiState(
    val name: String = "",
    val selectedColor: String = "#FF9800",
    val selectedIconResId: Int? = null,
    val type: CategoryType = CategoryType.EXPENSE,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)