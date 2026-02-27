package com.labin.piggybank.ui.model

import androidx.compose.ui.graphics.Color

data class PieChartData(
    val amount: Double,
    val color: Color,
    val percentage: Double,
    val label: String
)