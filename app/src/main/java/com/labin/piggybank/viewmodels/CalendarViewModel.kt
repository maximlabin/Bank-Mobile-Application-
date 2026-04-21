package com.labin.piggybank.viewmodels

import androidx.lifecycle.ViewModel
import com.labin.piggybank.ui.model.CalendarSelectionMode
import com.labin.piggybank.ui.model.CalendarUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    fun setMode(mode: CalendarSelectionMode) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun selectDate(date: LocalDate) {
        _uiState.update {
            when (it.mode) {
                is CalendarSelectionMode.SingleDate -> it.copy(selectedDate = date)
                is CalendarSelectionMode.DateRange -> {
                    if (it.startDate == null) {
                        it.copy(startDate = date, endDate = null)
                    } else if (it.endDate == null) {
                        val start = it.startDate
                        val end = date
                        if (end >= start) {
                            it.copy(endDate = end)
                        } else {
                            it.copy(startDate = end, endDate = start)
                        }
                    } else {
                        it.copy(startDate = date, endDate = null)
                    }
                }
                else -> it
            }
        }
    }

    fun selectDayOfWeek(day: DayOfWeek) {
        _uiState.update { it.copy(selectedDay = day) }
    }

    fun selectYear(year: Year) {
        _uiState.update { it.copy(selectedYear = year) }
    }

    fun reset() {
        _uiState.update { CalendarUiState(mode = it.mode) }
    }

    fun confirmSelection() {
    }

    private fun buildResult(): CalendarSelectionResult {
        val state = _uiState.value
        return when (state.mode) {
            is CalendarSelectionMode.SingleDate -> CalendarSelectionResult.SingleDate(state.selectedDate!!)
            is CalendarSelectionMode.DateRange -> CalendarSelectionResult.DateRange(state.startDate!!, state.endDate!!)
            is CalendarSelectionMode.DayOfWeek ->
                CalendarSelectionResult.DayOfWeek(state.selectedDay as java.time.DayOfWeek)
            is CalendarSelectionMode.Year -> CalendarSelectionResult.Year(state.selectedYear!!)
        }
    }
}

sealed class CalendarSelectionResult {
    data class SingleDate(val date: LocalDate) : CalendarSelectionResult()
    data class DateRange(val start: LocalDate, val end: LocalDate) : CalendarSelectionResult()
    data class DayOfWeek(val day: java.time.DayOfWeek) : CalendarSelectionResult()
    data class Year(val year: java.time.Year) : CalendarSelectionResult()
}