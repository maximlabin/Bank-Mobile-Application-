package com.labin.piggybank.compose.calendar

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.labin.piggybank.utilities.getMonthNameNominative
import com.labin.piggybank.ui.model.CalendarSelectionMode
import com.labin.piggybank.ui.model.CalendarUiState
import com.labin.piggybank.viewmodels.CalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val modes = listOf(
        CalendarSelectionMode.SingleDate to "Дата",
        CalendarSelectionMode.DateRange to "Период",
        CalendarSelectionMode.Month to "Месяц",
        CalendarSelectionMode.Year to "Год"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выбор периода") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.confirmSelection()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                enabled = state.isSelectionValid
            ) {
                Text("Применить")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
            ) {
                modes.forEachIndexed { index, (mode, label) ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                        onClick = { viewModel.setMode(mode) },
                        selected = state.mode::class == mode::class
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            when (state.mode) {
                is CalendarSelectionMode.SingleDate -> {
                    SingleDatePicker(
                        selectedDate = state.selectedDate,
                        onDateSelected = viewModel::selectDate
                    )
                }
                is CalendarSelectionMode.DateRange -> {
                    DateRangePicker(
                        startDate = state.startDate,
                        endDate = state.endDate,
                        onDateSelected = viewModel::selectDate
                    )
                }
                is CalendarSelectionMode.Month -> {
                    MonthSelector(
                        selectedMonth = state.selectedMonth,
                        onMonthSelected = viewModel::selectMonth
                    )
                }
                is CalendarSelectionMode.Year -> {
                    YearSelector(
                        selectedYear = state.selectedYear,
                        onYearSelected = viewModel::selectYear
                    )
                }
            }
            SelectionSummary(state)
        }
    }
}

@Composable
fun SingleDatePicker(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val daysInMonth = today.lengthOfMonth()
    val firstDayOfMonth = today.withDayOfMonth(1).dayOfWeek.value

    Column {
        Text(
            text = getMonthNameNominative(today.month),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(DayOfWeek.values().toList()) { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale("ru")),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp)
                )
            }
            items((1 until firstDayOfMonth).toList()) {
                Box(modifier = Modifier.size(40.dp))
            }
            items((1..daysInMonth).toList()) { day ->
                val date = today.withDayOfMonth(day)
                val isSelected = selectedDate == date
                val isToday = date == LocalDate.now()

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                isToday -> MaterialTheme.colorScheme.primaryContainer
                                else -> Color.Transparent
                            }
                        )
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.toString(),
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DateRangePicker(
    startDate: LocalDate?,
    endDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Начало: ${startDate?.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "—"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Конец: ${endDate?.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "—"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        SingleDatePicker(
            selectedDate = startDate,
            onDateSelected = onDateSelected
        )
        Text(
            text = "💡 Нажмите дважды: первый раз — начало, второй — конец периода",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun MonthSelector(
    selectedMonth: Month?,
    onMonthSelected: (Month) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(Month.entries.toList()) { month ->
            val isSelected = selectedMonth == month
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMonthSelected(month) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getMonthNameNominative(month),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun YearSelector(
    selectedYear: Year?,
    onYearSelected: (Year) -> Unit
) {
    val currentYear = LocalDate.now().year
    val years = (currentYear - 10..currentYear + 10).map { Year.of(it) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(years) { year ->
            val isSelected = selectedYear == year
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onYearSelected(year) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = year.value.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SelectionSummary(state: CalendarUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Выбрано:", style = MaterialTheme.typography.labelMedium)
            Text(
                text = when (state.mode) {
                    is CalendarSelectionMode.SingleDate ->
                        state.selectedDate?.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "—"
                    is CalendarSelectionMode.DateRange ->
                        "${state.startDate?.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM")) ?: "—"} — ${state.endDate?.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "—"}"
                    is CalendarSelectionMode.Month ->
                        state.selectedMonth?.let { getMonthNameNominative(it) } ?: "—"
                    is CalendarSelectionMode.Year ->
                        state.selectedYear?.value?.toString() ?: "—"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}