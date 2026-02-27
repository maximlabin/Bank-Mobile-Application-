package com.labin.piggybank.compose.homepage

import android.R.attr.onClick
import android.text.TextPaint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.labin.piggybank.ui.model.HomeUiState
import com.labin.piggybank.ui.model.PieChartData
import com.labin.piggybank.ui.model.Transaction
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    //onEvent: (DashboardEvent) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PieChart(
                data = uiState.categories,
                modifier = Modifier.size(250.dp),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 1.dp) // Исправил отступ end на 16.dp для красоты
                .size(70.dp)
                .clip(CircleShape)
                .background(Color(0xFF6200EE))
                .zIndex(1f)
                .clickable { navController.navigate("profile/123") },
            contentAlignment = Alignment.Center // Центрируем иконку
        ) {
            Icon(
                imageVector = Icons.Default.Person, // Иконка профиля
                contentDescription = "Профиль",
                tint = Color.White, // Белый цвет
                modifier = Modifier.size(36.dp) // Размер иконки
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 215.dp, end = 1.dp)
                .size(70.dp)
                .clip(CircleShape)
                .background(Color(0xFF6200EE))
                .zIndex(1f)
                .clickable { navController.navigate("newOperation/123") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить операцию",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

//        Button(onClick = { onClick(Text("Hello")) } ) {
//            Text("Hello")
//        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 300.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BankCardWithBalance(
                cardNumber = uiState.cardNumber,
                balance = uiState.balance
            )

            Text(
                text = "Последние операции",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,

                )

            Text(
                text="Категории"
            )



            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.lastTransactions) { transaction ->
                    TransactionItem(transaction)
                }
            }


        }


    }
}


@Composable
fun BankCardWithBalance(
    cardNumber: Int,
    balance: Double,
    modifier: Modifier = Modifier
) {
    var isShowingBalance by remember { mutableStateOf(false) }

    // Анимация цвета при переключении
    val containerColor by animateColorAsState(
        targetValue = if (isShowingBalance) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(100)
    )

    val contentColor by animateColorAsState (
        targetValue = if (isShowingBalance) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            Color.White
        },
        animationSpec = tween(300)
    )

    Card(
        shape = RoundedCornerShape(16.dp), // единая скруглённая форма
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { isShowingBalance = !isShowingBalance },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        AnimatedContent(
            targetState = isShowingBalance,
            label = "card_content_switch"
        ) { showingBalance ->
            if (showingBalance) {
                // Режим баланса
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$balance ₽",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Text(
                        text = "Доступно на счёту",
                        style = MaterialTheme.typography.labelMedium,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
            } else {
                // Режим банковской карты
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "BANK",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "•••• •••• •••• ${cardNumber.toString().padStart(4, '0')}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Normal,
                        color = contentColor,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(transaction.description)
        Text(
            text = (if (transaction.isIncome) "+" else "") + "${transaction.amount} ₽",
            color = if (transaction.isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
    showOutline: Boolean = true,
    outlineColor: Color = Color.Black,
    outlineWidth: Dp = 5.dp,
    onSliceClick: (PieChartData) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf<PieChartData?>(null) }

    Canvas(
        modifier = modifier
            .pointerInput(data) {
                detectTapGestures { tapPosition ->
                    val canvasSize = size
                    val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
                    val radius = minOf(canvasSize.width, canvasSize.height) / 2f

                    val relativeX = tapPosition.x - center.x
                    val relativeY = tapPosition.y - center.y
                    val distanceFromCenter = sqrt(relativeX * relativeX + relativeY * relativeY)

                    if (distanceFromCenter <= radius) {
                        val tapAngle = Math.toDegrees(atan2(relativeY.toDouble(), relativeX.toDouble())).toFloat()
                        var normalizedAngle = (tapAngle + 90 + 360) % 360

                        var startAngle = 0f
                        for (item in data) {
                            // ✅ Используем готовый процент!
                            val sweepAngle = item.percentage.toFloat() * 3.6f
                            val endAngle = startAngle + sweepAngle

                            val adjustedStart = (startAngle + 360) % 360
                            val adjustedEnd = (endAngle + 360) % 360

                            val inRange = if (adjustedStart <= adjustedEnd) {
                                normalizedAngle in adjustedStart..adjustedEnd
                            } else {
                                normalizedAngle >= adjustedStart || normalizedAngle <= adjustedEnd
                            }

                            if (inRange) {
                                selectedCategory = item
                                onSliceClick(item)
                                return@detectTapGestures
                            }
                            startAngle = endAngle
                        }
                    }
                }
            }
    ) {
        val size = size.minDimension
        val canvasSize = Size(size, size)
        val center = Offset(size / 2f, size / 2f)
        val innerCircleRadius = size * 0.35f
        var startAngle = -90f

        for (item in data) {
            // ✅ Берём угол из процента
            val sweepAngle = item.percentage.toFloat() * 3.6f

            val isSelected = selectedCategory == item
            val offset = if (isSelected) {
                val angleRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                Offset(
                    (cos(angleRad) * 10).toFloat(),
                    (sin(angleRad) * 10).toFloat()
                )
            } else {
                Offset.Zero
            }

            val topLeft = center - Offset(size / 2f, size / 2f) + offset

            drawArc(
                color = item.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = topLeft,
                size = canvasSize
            )

            if (showOutline) {
                drawArc(
                    color = outlineColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = topLeft,
                    size = canvasSize,
                    style = Stroke(width = outlineWidth.toPx())
                )
            }

            startAngle += sweepAngle
        }

        // Рисуем центр
        drawCircle(
            color = Color.Black,
            radius = innerCircleRadius,
            center = center
        )

        val centerText = if (selectedCategory != null) {
            "${selectedCategory!!.amount.toInt()} ₽"
        } else {
            "${data.sumOf { it.amount }.toInt()} ₽"
        }

        drawCenteredText(
            text = centerText,
            center = center,
            color = Color.White,
            fontSize = size * 0.15f
        )
    }
}

// Вспомогательная функция для центрированного текста
private fun DrawScope.drawCenteredText(
    text: String,
    center: Offset,
    color: Color,
    fontSize: Float
) {
    // Настройка Paint для текста
    val textPaint = TextPaint().apply {
        this.color = color.toArgb()
        this.textSize = fontSize
        this.textAlign = android.graphics.Paint.Align.CENTER
        this.isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        text,
        center.x,
        center.y - (textPaint.ascent() + textPaint.descent()) / 2,
        textPaint
    )
}
