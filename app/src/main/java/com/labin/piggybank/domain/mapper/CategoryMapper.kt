package com.labin.piggybank.domain.mapper
import androidx.compose.ui.graphics.Color
import com.labin.piggybank.domain.CategoryType
import com.labin.piggybank.ui.model.PieChartData
import kotlin.math.absoluteValue
import android.graphics.Color as AndroidColor
import com.labin.piggybank.data.Category
import com.labin.piggybank.data.CategorySummary
import com.labin.piggybank.domain.TransactionType


object CategoryMapper {

    /**
     * Преобразует список расходов по категориям в данные для круговой диаграммы.
     */
    fun toPieChartData(summaries: List<CategorySummary>): List<PieChartData> {
        val total = summaries.sumOf { it.totalAmount.absoluteValue }
        if (total == 0.0) return emptyList()

        return summaries.map { summary ->
            val percentage = (summary.totalAmount.absoluteValue / total) * 100

            val color = runCatching {
                Color(AndroidColor.parseColor(summary.colorHex))
            }.getOrDefault(Color.Gray)

            PieChartData(
                amount = summary.totalAmount,
                color = color,
                percentage = percentage,
                label = summary.categoryName
            )
        }
    }
}