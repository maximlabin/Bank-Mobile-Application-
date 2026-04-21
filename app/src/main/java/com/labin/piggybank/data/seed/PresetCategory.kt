package com.labin.piggybank.data.seed

import com.labin.piggybank.R
import com.labin.piggybank.data.Category
import com.labin.piggybank.domain.CategoryType

enum class PresetCategory(
    val label: String,
    val color: String,
    val icon: Int,
    val type: CategoryType
) {
    FOOD("Еда", "#FF9800", R.drawable.ic_food, CategoryType.EXPENSE),
    RENT("Жильё", "#4CAF50", R.drawable.ic_home, CategoryType.EXPENSE),
    TRANSPORT("Транспорт", "#FF5722", R.drawable.ic_transport, CategoryType.EXPENSE),
    ENTERTAINMENT("Развлечения", "#9C27B0", R.drawable.ic_entertainment, CategoryType.EXPENSE),
    HEALTH("Здоровье", "#E91E63", R.drawable.ic_health, CategoryType.EXPENSE),
    SHOPPING("Покупки", "#FFC107", R.drawable.ic_shopping, CategoryType.EXPENSE),

    SALARY("Зарплата", "#2196F3", R.drawable.ic_salary, CategoryType.INCOME),
    INVESTMENTS("Инвестиции", "#009688", R.drawable.ic_investments, CategoryType.INCOME),
    FREELANCE("Фриланс", "#3F51B5", R.drawable.ic_freelance, CategoryType.INCOME);

    fun toEntity(): Category = Category(
        name = this.label,
        colorHex = this.color,
        iconResId = this.icon,
        type = this.type,
        isDefault = true
    )
}