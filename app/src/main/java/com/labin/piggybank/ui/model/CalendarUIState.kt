package com.labin.piggybank.ui.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year

sealed class CalendarSelectionMode {
    object SingleDate : CalendarSelectionMode()
    object DateRange : CalendarSelectionMode()
    object DayOfWeek : CalendarSelectionMode()
    object Year : CalendarSelectionMode()
}

sealed class CalendarSelectionResult {
    data class SingleDate(val date: LocalDate) : CalendarSelectionResult()
    data class DateRange(val start: LocalDate, val end: LocalDate) : CalendarSelectionResult()
    data class DayOfWeek(val day: java.time.DayOfWeek) : CalendarSelectionResult()
    data class Year(val year: java.time.Year) : CalendarSelectionResult()
}

data class CalendarUiState(
    val mode: CalendarSelectionMode = CalendarSelectionMode.SingleDate,
    val selectedDate: LocalDate? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val selectedDay: DayOfWeek? = null,
    val selectedYear: Year? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isSelectionValid: Boolean
        get() = when (mode) {
            is CalendarSelectionMode.SingleDate -> selectedDate != null
            is CalendarSelectionMode.DateRange -> startDate != null && endDate != null && (endDate >= startDate)
            is CalendarSelectionMode.DayOfWeek -> selectedDay != null
            is CalendarSelectionMode.Year -> selectedYear != null
        }
}