package com.labin.piggybank.ui.model.mapper
import androidx.compose.ui.graphics.Color
import com.labin.piggybank.data.CategoryExpense
import com.labin.piggybank.ui.model.PieChartData
import kotlin.math.absoluteValue

object CategoryMapper {
    private val colorMap = mapOf(
        "Продукты" to Color(0xFF4CAF50),
        "Транспорт" to Color(0xFF2196F3),
        "Развлечения" to Color(0xFFFFC107),
        "Подписки" to Color(0xFFF44336)
    )

    fun toPieChartData(expenses: List<CategoryExpense>): List<PieChartData> {
        val total = expenses.sumOf { it.totalAmount }.absoluteValue

        return expenses.map { expense ->
            val percentage = (expense.totalAmount.absoluteValue / total) * 100
            val color = colorMap[expense.category] ?: Color.Gray

            PieChartData(
                amount = percentage,
                color = color,
                label = expense.category
            )
        }
    }
}