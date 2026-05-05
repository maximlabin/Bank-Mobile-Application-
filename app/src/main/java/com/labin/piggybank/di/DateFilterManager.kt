package com.labin.piggybank.di

import com.labin.piggybank.ui.model.CalendarSelectionMode
import com.labin.piggybank.ui.model.CalendarSelectionResult
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

@ActivityRetainedScoped
class DateFilterManager @Inject constructor() {
    private val _currentFilter = MutableStateFlow<CalendarSelectionResult>(
        CalendarSelectionResult.SingleDate(LocalDate.now())
    )
    val currentFilter: StateFlow<CalendarSelectionResult> = _currentFilter.asStateFlow()

    fun applyFilter(result: CalendarSelectionResult) {
        _currentFilter.value = result
    }
}