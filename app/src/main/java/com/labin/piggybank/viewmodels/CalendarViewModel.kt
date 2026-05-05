package com.labin.piggybank.viewmodels

import androidx.lifecycle.ViewModel
import com.labin.piggybank.di.DateFilterManager
import com.labin.piggybank.ui.model.CalendarSelectionMode
import com.labin.piggybank.ui.model.CalendarSelectionResult
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
class CalendarViewModel @Inject constructor(
    private val dateFilterManager: DateFilterManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    fun getSelectionResult(): CalendarSelectionResult = buildResult()

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
                        val start = it.startDate!!
                        val end = date
                        if (end >= start) it.copy(endDate = end)
                        else it.copy(startDate = end, endDate = start)
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

    fun confirmSelection() {
        val state = uiState.value
        if (!state.isSelectionValid) return

        val result = when (state.mode) {
            CalendarSelectionMode.SingleDate ->
                CalendarSelectionResult.SingleDate(state.selectedDate!!)
            CalendarSelectionMode.DateRange ->
                CalendarSelectionResult.DateRange(state.startDate!!, state.endDate!!)
            CalendarSelectionMode.DayOfWeek ->
                CalendarSelectionResult.DayOfWeek(state.selectedDay!!)
            CalendarSelectionMode.Year ->
                CalendarSelectionResult.Year(state.selectedYear!!)
        }
        dateFilterManager.applyFilter(result)
    }

    private fun buildResult(): CalendarSelectionResult {
        val state = _uiState.value
        return when (state.mode) {
            is CalendarSelectionMode.SingleDate -> CalendarSelectionResult.SingleDate(state.selectedDate!!)
            is CalendarSelectionMode.DateRange -> CalendarSelectionResult.DateRange(state.startDate!!, state.endDate!!)
            is CalendarSelectionMode.DayOfWeek -> CalendarSelectionResult.DayOfWeek(state.selectedDay as DayOfWeek)
            is CalendarSelectionMode.Year -> CalendarSelectionResult.Year(state.selectedYear!!)
        }
    }
}