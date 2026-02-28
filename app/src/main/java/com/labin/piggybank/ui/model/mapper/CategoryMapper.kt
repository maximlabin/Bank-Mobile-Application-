package com.labin.piggybank.ui.model.mapper
import androidx.compose.ui.graphics.Color
import com.labin.piggybank.data.CategoryExpense
import com.labin.piggybank.ui.model.PieChartData
import com.labin.piggybank.utilities.CategoryType
import kotlin.math.absoluteValue

object CategoryMapper {


    fun toPieChartData(expenses: List<CategoryExpense>): List<PieChartData> {
        val total = expenses.sumOf { it.totalAmount }.absoluteValue
        if (total == 0.0) return emptyList()

        return expenses.map { expense ->
            val percentage = (expense.totalAmount.absoluteValue / total) * 100
            val categoryType = CategoryType.fromLabel(expense.category)
            val color = categoryType?.color ?: Color.Gray

            PieChartData(
                amount = expense.totalAmount,
                color = color,
                percentage=percentage,
                label = expense.category
            )
        }
    }
}