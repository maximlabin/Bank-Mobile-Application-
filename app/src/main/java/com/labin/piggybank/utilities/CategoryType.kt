package com.labin.piggybank.utilities

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.labin.piggybank.compose.operation.Category

enum class CategoryType(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val colorInt: Int
) {
    FOOD(
        label = "Еда",
        icon = Icons.Default.ShoppingCart,
        color = Color(0xFFFF9800),
        colorInt = 0xFFFF9800.toInt()
    ),
    HOUSING(
        label = "Жилье",
        icon = Icons.Default.Home,
        color = Color(0xFF4CAF50),
        colorInt = 0xFF4CAF50.toInt()
    ),
    ENTERTAINMENT(
        label = "Развлечения",
        icon = Icons.Default.Movie,
        color = Color(0xFFE91E63),
        colorInt = 0xFFE91E63.toInt()
    ),
    HEALTH(
        label = "Здоровье",
        icon = Icons.Default.Favorite,
        color = Color(0xFFF44336),
        colorInt = 0xFFF44336.toInt()
    ),
    GIFTS(
        label = "Подарки",
        icon = Icons.Default.CardGiftcard,
        color = Color(0xFF9C27B0),
        colorInt = 0xFF9C27B0.toInt()
    ),
    OTHER(
        label = "Другое",
        icon = Icons.Default.QuestionMark,
        color = Color(0xFF31312E),
        colorInt = 0xFF31312E.toInt()
    ),
    CREATE_NEW(
        label = "Создать",
        icon = Icons.Default.Add,
        color = Color(0x8AACAC03),
        colorInt = 0x8AACAC03.toInt()
    );

    companion object {
        fun fromLabel(label: String): CategoryType? =
            entries.find { it.label == label }

        fun getAllForUi(): List<CategoryType> = entries.toList()
    }

    fun toCategory(): Category = Category(
        name = this.label,
        icon = this.icon,
        color = this.color,
        type = this // Ссылка на enum для сравнения
    )
}