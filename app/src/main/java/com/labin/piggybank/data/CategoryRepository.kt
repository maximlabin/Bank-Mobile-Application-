package com.labin.piggybank.data

import com.labin.piggybank.domain.CategoryType
import com.labin.piggybank.data.seed.PresetCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    suspend fun initializeDefaults() {
        val presets = PresetCategory.entries.map { it.toEntity() }
        categoryDao.insertAllIgnoringDuplicates(presets)
    }

    fun getCategoriesByType(type: CategoryType): Flow<List<Category>> {
        return categoryDao.getCategoriesByType(type)
    }

    suspend fun createUserCategory(
        name: String,
        color: String,
        type: CategoryType,
        iconResId: Int? = null
    ) {
        val newCategory = Category(
            name = name,
            colorHex = color,
            iconResId = iconResId,
            type = type,
            isDefault = false
        )
        categoryDao.insert(newCategory)
    }
}