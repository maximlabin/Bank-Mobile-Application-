package com.labin.piggybank.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SharedDateFilterViewModel @Inject constructor() : ViewModel() {
    private val _selectedRange = MutableStateFlow(DateRange.ALL_TIME)
    val selectedRange: StateFlow<DateRange> = _selectedRange

    fun updateRange(range: DateRange) {
        _selectedRange.value = range as DateRange.ALL_TIME
    }

    fun reset() {
        _selectedRange.value = DateRange.ALL_TIME
    }
}

sealed class DateRange {
    object ALL_TIME : DateRange()
    data class Custom(val startDate: LocalDate, val endDate: LocalDate) : DateRange()
}